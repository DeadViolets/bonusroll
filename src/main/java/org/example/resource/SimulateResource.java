package org.example.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import jakarta.inject.Inject;
import org.example.model.ReportResult;
import org.example.model.ResultsViewModel;
import org.example.model.ResultsViewModel.ReportEntry;
import org.example.service.RaidbotsReportParser;
import org.example.service.RaidbotsUrlValidator;

import java.util.ArrayList;

@Path("/simulate")
public class SimulateResource {

    private final TemplateEngine templateEngine;

    @Inject
    public SimulateResource(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response simulate(
            @FormParam("singleBossUrl") String singleBossUrl,
            @FormParam("multiBossUrl")  String multiBossUrl,
            @FormParam("dungeonUrl")    String dungeonUrl
    ) {
        record Input(String label, String url) {}
        var inputs = new Input[]{
                new Input("Single Boss", singleBossUrl),
                new Input("Multiple Bosses", multiBossUrl),
                new Input("Dungeons", dungeonUrl),
        };

        var entries = new ArrayList<ReportEntry>(inputs.length);
        for (var input : inputs) {
            if (input.url() == null || input.url().isBlank()) continue;
            entries.add(process(input.label(), input.url().strip()));
        }

        if (entries.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("<p class=\"error\">Please provide at least one report URL.</p>")
                    .build();
        }

        var output = new StringOutput();
        templateEngine.render("_results.jte", new ResultsViewModel(entries), output);
        return Response.ok(output.toString()).build();
    }

    private ReportEntry process(String label, String url) {
        try {
            var dataUri = RaidbotsUrlValidator.toDataJsonUri(url);
            ReportResult result = RaidbotsReportParser.fetch(dataUri);
            return new ReportEntry(label, url, result, null);
        } catch (IllegalArgumentException e) {
            return new ReportEntry(label, url, null, e.getMessage());
        } catch (Exception e) {
            return new ReportEntry(label, url, null,
                    "Failed to fetch or parse report: " + e.getMessage());
        }
    }
}
