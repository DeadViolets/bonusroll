package org.example.raidbots;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record AggregatedReport(Map<EncounterInfo, List<ItemInfo>> encounters) {

    public enum EncounterType {
        RAID,
        DUNGEON,
    }

    public record EncounterInfo(String name, EncounterType type) {}

    public record ItemInfo(
            String name,
            int id,
            Integer sourceItemId,
            Set<EncounterInfo> encounterSources,
            double dps) {}
}
