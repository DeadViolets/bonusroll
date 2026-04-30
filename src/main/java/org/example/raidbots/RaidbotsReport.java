package org.example.raidbots;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Root of a raidbots data.json response. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RaidbotsReport(Sim sim, Simbot simbot) {

    public AggregatedReport aggregate() {
        double baseDps = sim.statistics().raid_dps().mean();
        Map<Integer, AggregatedReport.ItemInfo> items = new HashMap<>();

        /*
        General algorithm:
        1. Take all items from droptimizerItems and assign an initial value of 0
        2. Go through sim results and update dps values of items. Make sure to check for multiple encounters on items
        3. Ensure that either only catalyst or non-catalyst version of item exists in encounter
         */

        // Step 1: Add all droptimizer items
        for (var outerItem : simbot.meta.rawFormData.droptimizerItems) {
            var item = outerItem.item;
            final AggregatedReport.EncounterInfo encounterSource;
            if (item.encounter != null) {
                encounterSource =
                        new AggregatedReport.EncounterInfo(
                                item.encounter.name, AggregatedReport.EncounterType.RAID);
            } else {
                // Dungeon report
                int encounterId =
                        item.sources.stream()
                                .filter(s -> s.instanceId == -1)
                                .findFirst()
                                .get()
                                .encounterId;
                String encounterName =
                        item.instance.encounters.stream()
                                .filter(e -> e.id == encounterId)
                                .findFirst()
                                .get()
                                .name;
                encounterSource =
                        new AggregatedReport.EncounterInfo(
                                encounterName, AggregatedReport.EncounterType.DUNGEON);
            }
            items.compute(
                    item.id,
                    (k, current) -> {
                        if (current == null) {
                            Integer sourceId = null;
                            if (item.sourceItem != null) {
                                sourceId = item.sourceItem.id;
                            }
                            return new AggregatedReport.ItemInfo(
                                    item.name,
                                    item.id,
                                    sourceId,
                                    new HashSet<>(Set.of(encounterSource)),
                                    0);
                        } else {
                            current.encounterSources().add(encounterSource);
                            return current;
                        }
                    });
        }

        // Step 2: Update with sim results
        for (var result : sim.profilesets().normalizedResults(baseDps)) {
            int realId = result.realId();
            items.computeIfPresent(
                    realId,
                    (k, v) ->
                            (v.dps() > result.mean
                                    ? v
                                    : new AggregatedReport.ItemInfo(
                                            v.name(),
                                            v.id(),
                                            v.sourceItemId(),
                                            v.encounterSources(),
                                            result.mean)));
        }

        // Step 3: Combine Catalyst / non-catalyst
        // Step 3a: Group items by encounter
        Map<AggregatedReport.EncounterInfo, List<AggregatedReport.ItemInfo>> encounterItems =
                new HashMap<>();
        for (var item : items.values()) {
            for (AggregatedReport.EncounterInfo encounterInfo : item.encounterSources()) {
                encounterItems.computeIfAbsent(encounterInfo, k -> new ArrayList<>()).add(item);
            }
        }
        // Step 3b: Combine items
        for (List<AggregatedReport.ItemInfo> entry : encounterItems.values()) {
            Set<Integer> toRemove = new HashSet<>();
            for (var item : entry) {
                if (item.sourceItemId() != null) {
                    entry.stream()
                            .filter(i -> i.id() == item.sourceItemId())
                            .findFirst()
                            .ifPresent(
                                    source ->
                                            toRemove.add(
                                                    item.dps() > source.dps()
                                                            ? source.id()
                                                            : item.id()));
                }
            }
            entry.removeIf(i -> toRemove.contains(i.id()));
        }

        // TODO: Allow custom overrides

        return new AggregatedReport(encounterItems);
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
        public record Meta(RawFormData rawFormData) {

            @JsonIgnoreProperties(ignoreUnknown = true)
            public record RawFormData(List<DroptimizerItem> droptimizerItems) {

                @JsonIgnoreProperties(ignoreUnknown = true)
                public record DroptimizerItem(DroptimizerInnerItem item) {

                    @JsonIgnoreProperties(ignoreUnknown = true)
                    public record DroptimizerInnerItem(
                            int id,
                            String name,
                            String icon,
                            List<Source> sources,
                            SourceItem sourceItem,
                            Instance instance,
                            Encounter encounter) {

                        @JsonIgnoreProperties(ignoreUnknown = true)
                        public record Source(int instanceId, int encounterId) {}

                        @JsonIgnoreProperties(ignoreUnknown = true)
                        public record Instance(int id, List<Encounter> encounters) {}

                        @JsonIgnoreProperties(ignoreUnknown = true)
                        public record Encounter(int id, String name) {}

                        @JsonIgnoreProperties(ignoreUnknown = true)
                        public record SourceItem(int id, String name, String icon) {}
                    }
                }
            }
        }
    }
}
