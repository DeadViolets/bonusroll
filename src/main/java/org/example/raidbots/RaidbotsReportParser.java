package org.example.raidbots;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RaidbotsReportParser {

    private static final ObjectMapper MAPPER =
            new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private static final HttpClient HTTP =
            HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

    private static final Pattern REPORT_URL_PATTERN =
            Pattern.compile("^https://www\\.raidbots\\.com/simbot/report/([A-Za-z0-9]+)/?$");
    private static final Logger log = LoggerFactory.getLogger(RaidbotsReportParser.class);

    private RaidbotsReportParser() {}

    public static Optional<RaidbotsReport> fetch(String raidbotsUrl) {
        Optional<URI> maybeUri = toJsonUris(raidbotsUrl);
        return maybeUri.map(RaidbotsReportParser::buildDefaultRequest)
                .map(req -> HTTP.sendAsync(req, HttpResponse.BodyHandlers.ofString()))
                .map(CompletableFuture::join)
                .map(r -> parse(r.body()));
    }

    static Optional<URI> toJsonUris(String rawUri) {
        var matcher = REPORT_URL_PATTERN.matcher(rawUri.strip());
        if (!matcher.matches()) {
            log.warn("Encountered invalid raidbots URL {}", rawUri);
            return Optional.empty();
        }
        String reportId = matcher.group(1);
        return Optional.of(URI.create("https://raidbots.com/reports/%s/data.json".formatted(reportId)));
    }

    static HttpRequest buildDefaultRequest(URI uri) {
        return HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .GET()
                .build();
    }

    static RaidbotsReport parse(String jsonStream) {
        try {
            return MAPPER.readValue(jsonStream, RaidbotsReport.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
