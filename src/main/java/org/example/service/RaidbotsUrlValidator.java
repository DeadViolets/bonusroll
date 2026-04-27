package org.example.service;

import java.net.URI;
import java.util.regex.Pattern;

/**
 * Validates that a URL is a well-formed raidbots sim report URL and derives
 * the corresponding data.json URL.
 *
 * <p>Accepted form: {@code https://www.raidbots.com/simbot/report/<reportId>}
 * where {@code <reportId>} is a non-empty alphanumeric slug (letters, digits).
 */
public final class RaidbotsUrlValidator {

    private static final Pattern REPORT_URL_PATTERN = Pattern.compile(
            "^https://www\\.raidbots\\.com/simbot/report/([A-Za-z0-9]+)/?$"
    );

    private RaidbotsUrlValidator() {}

    /**
     * Validates the URL and returns the data.json URI to fetch.
     *
     * @param url the raw URL string provided by the user
     * @return the URI for {@code data.json}
     * @throws IllegalArgumentException if the URL does not match the expected pattern
     */
    public static URI toDataJsonUri(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL must not be blank");
        }
        var matcher = REPORT_URL_PATTERN.matcher(url.strip());
        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                    "Invalid raidbots URL. Expected: https://www.raidbots.com/simbot/report/<reportId>"
            );
        }
        String reportId = matcher.group(1);
        return URI.create("https://www.raidbots.com/simbot/report/" + reportId + "/data.json");
    }
}
