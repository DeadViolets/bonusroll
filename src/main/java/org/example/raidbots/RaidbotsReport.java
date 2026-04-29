package org.example.raidbots;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Root of a raidbots data.json response. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RaidbotsReport(Sim sim, Simbot simbot) {

    public AggregatedReport aggregate() {
        double baseDps = sim.statistics().raid_dps().mean();
        Map<Integer, List<AggregatedReport.ItemInfo>> encounterItems = new HashMap<>();

        for (var result : sim.profilesets().normalizedResults(baseDps)) {
            int realId = result.realId();
            encounterItems
                    .computeIfAbsent(
                            simbot.meta.getEncounterId(realId), k -> new ArrayList<>())
                    .add(new AggregatedReport.ItemInfo(, result.realId(), result.mean));
        }

        Map<String, List<AggregatedReport.ItemInfo>> namedEncounters =
                encounterItems.entrySet().stream()
                        .collect(
                                Collectors.toMap(
                                        e -> simbot.meta.getEncounterName(e.getKey()),
                                        Map.Entry::getValue));

        return new AggregatedReport(namedEncounters);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Sim(Statistics statistics, Profilesets profilesets) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Statistics(RaidDps raid_dps) {

            @JsonIgnoreProperties(ignoreUnknown = true)
            public record RaidDps(double mean) {}
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Profilesets(List<ProfilesetResult> results) {

            List<ProfilesetResult> normalizedResults(double baseDps) {
                return results.parallelStream().map(r -> r.normalize(baseDps)).toList();
            }

            /**
             * One entry in profilesets.results. The {@code name} field is a slash-delimited key:
             * {@code instanceId/encounterId/difficulty/itemId/itemLevel/bonusId/slot///}
             */
            @JsonIgnoreProperties(ignoreUnknown = true)
            public record ProfilesetResult(String name, double mean) {

                ProfilesetResult normalize(double baseDps) {
                    return new ProfilesetResult(name, Math.clamp(mean - baseDps, 0, mean));
                }

                public int realId() {
                    return Integer.parseInt(name.split("/", -1)[3]);
                }
            }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Simbot(Meta meta) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Meta(
                List<ItemLibraryEntry> itemLibrary, List<InstanceLibraryEntry> instanceLibrary) {

            public int getEncounterId(int itemId) {
                return itemLibrary.stream()
                        .filter(item -> item.id() == itemId)
                        .findFirst()
                        .map(
                                itemEntry -> {
                                    if (itemEntry.difficulty.contains("dungeon")) {
                                        ItemLibraryEntry.Source firstEntry =
                                                itemEntry.sources.getFirst();
                                        return firstEntry.instanceId < 0
                                                ? firstEntry.encounterId
                                                : firstEntry.instanceId;
                                    } else {
                                        return itemEntry.sources.getFirst().encounterId;
                                    }
                                })
                        .get();
            }

            public String getEncounterName(int encounterId) {
                final InstanceLibraryEntry instance;
                if (instanceLibrary.size() == 1) {
                    instance = instanceLibrary.getFirst();
                } else {
                    instance = instanceLibrary.stream().filter(i -> i.id == -1).findFirst().get();
                }
                return instance.encounters.stream()
                        .filter(e -> e.id == encounterId)
                        .findFirst()
                        .get()
                        .name;
            }

            /** One entry in simbot.meta.itemLibrary. */
            @JsonIgnoreProperties(ignoreUnknown = true)
            public record ItemLibraryEntry(
                    int id, String name, String difficulty, List<Source> sources) {

                @JsonIgnoreProperties(ignoreUnknown = true)
                public record Source(int instanceId, int encounterId) {}
            }

            /**
             * One entry in simbot.meta.instanceLibrary. Used to resolve a dungeon instanceId to a
             * human-readable name. Special IDs: -1 = M+ chest pool, -32 = Normal dungeon pool.
             */
            @JsonIgnoreProperties(ignoreUnknown = true)
            public record InstanceLibraryEntry(int id, List<EncounterEntry> encounters) {

                @JsonIgnoreProperties(ignoreUnknown = true)
                public record EncounterEntry(int id, String name) {}
            }
        }
    }
}
