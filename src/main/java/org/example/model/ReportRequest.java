package org.example.model;

/**
 * The three raidbots report URLs submitted by the user.
 * Each may be null/blank if the user left the field empty.
 */
public record ReportRequest(
        String singleBossUrl,
        String multiBossUrl,
        String dungeonUrl
) {}
