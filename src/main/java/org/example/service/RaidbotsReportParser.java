package org.example.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.ReportResult;
import org.example.model.ReportResult.AugmentedItem;
import org.example.model.ReportResult.SourceType;
import org.example.raidbots.RaidbotsReport;
import org.example.raidbots.RaidbotsReport.ItemLibraryEntry;
import org.example.raidbots.RaidbotsReport.ProfilesetResult;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Fetches and parses a single raidbots data.json report into a {@link ReportResult}.
 *
 * <p>Thread-safe; the {@link HttpClient} and {@link ObjectMapper} are shared.
 */
public final class RaidbotsReportParser {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private RaidbotsReportParser() {}

    /**
     * Fetches the given data.json URI and parses it into a {@link ReportResult}.
     */
    public static ReportResult fetch(URI dataJsonUri) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(dataJsonUri)
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .GET()
                .build();

        var response = HTTP.send(request, HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() != 200) {
            throw new IOException("Unexpected HTTP " + response.statusCode() + " fetching " + dataJsonUri);
        }

        return parse(MAPPER.readValue(response.body(), RaidbotsReport.class));
    }

    // -------------------------------------------------------------------------
    // Parsing
    // -------------------------------------------------------------------------

    static ReportResult parse(RaidbotsReport report) {
        double baseMean = report.sim().statistics().raid_dps().mean();

        // Build lookup maps from itemLibrary: id → name, id → source
        var nameById   = new HashMap<Integer, String>();
        var sourceById = new HashMap<Integer, ItemSource>();

        for (ItemLibraryEntry entry : report.simbot().meta().itemLibrary()) {
            nameById.putIfAbsent(entry.id(), entry.name());
            sourceById.putIfAbsent(entry.id(), extractSource(entry));
        }

        // Map each profileset result to an AugmentedItem
        List<ProfilesetResult> results = report.sim().profilesets().results();
        var items = new ArrayList<AugmentedItem>(results.size());

        for (ProfilesetResult result : results) {
            // name format: instanceId/encounterId/difficulty/itemId/itemLevel/bonusId/slot///
            String[] parts = result.name().split("/", -1);
            if (parts.length < 4) continue;

            int itemId;
            try {
                itemId = Integer.parseInt(parts[3]);
            } catch (NumberFormatException e) {
                continue;
            }

            double mean = result.mean();
            String itemName = nameById.getOrDefault(itemId, "Unknown (%d)".formatted(itemId));
            ItemSource src  = sourceById.getOrDefault(itemId, ItemSource.UNKNOWN);

            items.add(new AugmentedItem(
                    itemId,
                    itemName,
                    mean,
                    mean - baseMean,
                    src.name(),
                    src.type()
            ));
        }

        return new ReportResult(baseMean, List.copyOf(items));
    }

    // -------------------------------------------------------------------------
    // Source extraction
    // -------------------------------------------------------------------------

    private record ItemSource(String name, SourceType type) {
        static final ItemSource UNKNOWN = new ItemSource("Unknown", SourceType.RAID_BOSS);
    }

    private static ItemSource extractSource(ItemLibraryEntry entry) {
        String difficulty = entry.difficulty();
        if (difficulty == null) return ItemSource.UNKNOWN;

        if (difficulty.startsWith("raid-mythic")) {
            if (entry.encounter() != null) {
                return new ItemSource(entry.encounter().name(), SourceType.RAID_BOSS);
            }
            if (entry.instance() != null) {
                return new ItemSource(entry.instance().name(), SourceType.RAID_BOSS);
            }
            return new ItemSource("Raid", SourceType.RAID_BOSS);
        }

        if (difficulty.startsWith("dungeon-")) {
            if (entry.sources() != null) {
                for (var src : entry.sources()) {
                    if (src.instanceId() >= 0) {
                        if (entry.instance() != null) {
                            return new ItemSource(entry.instance().name(), SourceType.DUNGEON);
                        }
                        return new ItemSource("Dungeon (instance %d)".formatted(src.instanceId()), SourceType.DUNGEON);
                    }
                }
            }
            return new ItemSource("Dungeon", SourceType.DUNGEON);
        }

        return ItemSource.UNKNOWN;
    }
}
