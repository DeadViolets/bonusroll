package org.example.model;

import java.util.List;

/**
 * The fully-parsed, application-level view of one raidbots report.
 *
 * @param baseMeanDps  The baseline mean DPS from sim.statistics.raid_dps.mean
 * @param items        Every augmented item with its gain and source metadata
 */
public record ReportResult(
        double baseMeanDps,
        List<AugmentedItem> items
) {

    /**
     * A single item augmentation result, enriched with human-readable names and
     * source information extracted from itemLibrary.
     *
     * @param itemId       Numeric item ID (matched from the name field in results)
     * @param itemName     Human-readable item name from itemLibrary
     * @param meanDps      Mean DPS when this item is equipped
     * @param dpsDelta     meanDps - baseMeanDps  (positive = upgrade)
     * @param source       Where the item comes from (boss name or dungeon name)
     * @param sourceType   RAID_BOSS or DUNGEON
     */
    public record AugmentedItem(
            int itemId,
            String itemName,
            double meanDps,
            double dpsDelta,
            String source,
            SourceType sourceType
    ) {}

    public enum SourceType { RAID_BOSS, DUNGEON }
}
