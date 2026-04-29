package org.example.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.raidbots.AggregatedReport;

public record ResultsViewModel(List<WeightedRow> reportEntries) {

    public record WeightedRow(String name, Double weight, List<AggregatedReport.ItemInfo> items) {}

    public static ResultsViewModel fromAggregatedReports(List<AggregatedReport> aggregatedReports) {
        Map<String, List<AggregatedReport.ItemInfo>> reportEntries = new HashMap<>();
        for (AggregatedReport aggregatedReport : aggregatedReports) {
            reportEntries.putAll(aggregatedReport.encounters());
        }
        List<WeightedRow> rows =
                reportEntries.entrySet().stream()
                        .map(
                                e -> {
                                    double weight =
                                            e.getValue().stream()
                                                    .mapToDouble(AggregatedReport.ItemInfo::dps)
                                                    .average()
                                                    .getAsDouble();
                                    return new WeightedRow(e.getKey(), weight, e.getValue());
                                })
                        .sorted(Comparator.comparing(WeightedRow::weight).reversed())
                        .toList();

        return new ResultsViewModel(rows);
    }
}
