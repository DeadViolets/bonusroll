package org.example.model;

import java.util.List;

public record IndexModel(List<FilterItem> filterItems) {

    public record FilterItem(List<Integer> ids, String name, String icon) {}
}
