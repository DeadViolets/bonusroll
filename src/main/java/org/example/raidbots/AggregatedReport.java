package org.example.raidbots;

import java.util.List;
import java.util.Map;

public record AggregatedReport(Map<String, List<ItemInfo>> encounters) {

    public record ItemInfo(String name, int id, double dps) {}
}
