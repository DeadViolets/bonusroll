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
    public record Meta(java.util.List<ItemLibraryEntry> itemLibrary) {}

    /**
     * One entry in simbot.meta.itemLibrary.
     *
     * <p>The {@code difficulty} field is polymorphic: a plain string ({@code "raid-mythic"})
     * for raid items, or a JSON object ({@code {"id":"dungeon-mythic-weekly10",...}}) for
     * dungeon items. The custom deserializer {@link DifficultyDeserializer} normalises both
     * to a plain string.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ItemLibraryEntry(
            int id,
            String name,
            @com.fasterxml.jackson.databind.annotation.JsonDeserialize(
                    using = DifficultyDeserializer.class)
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
