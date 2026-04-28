package org.example.raidbots;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Root of a raidbots data.json response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RaidbotsReport(Sim sim, Simbot simbot) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Sim(Statistics statistics, Profilesets profilesets) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Statistics(RaidDps raid_dps) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RaidDps(double mean) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Profilesets(java.util.List<ProfilesetResult> results) {}

    /**
     * One entry in profilesets.results.
     * The {@code name} field is a slash-delimited key:
     * {@code instanceId/encounterId/difficulty/itemId/itemLevel/bonusId/slot///}
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ProfilesetResult(String name, double mean) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Simbot(Meta meta) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Meta(
            java.util.List<ItemLibraryEntry> itemLibrary,
            java.util.List<InstanceLibraryEntry> instanceLibrary
    ) {}

    /**
     * One entry in simbot.meta.instanceLibrary.
     * Used to resolve a dungeon instanceId to a human-readable name.
     * Special IDs: -1 = M+ chest pool, -32 = Normal dungeon pool.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record InstanceLibraryEntry(int id, String name) {}

    /**
     * One entry in simbot.meta.itemLibrary.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ItemLibraryEntry(
            int id,
            String name,
            String difficulty,
            Encounter encounter,
            Instance instance,
            java.util.List<Source> sources
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Encounter(int id, String name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Instance(int id, String name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Source(int instanceId, int encounterId) {}
}
