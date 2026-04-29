package org.example.resource;

import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import org.example.model.ResultsViewModel;
import org.example.raidbots.AggregatedReport;
import org.example.raidbots.RaidbotsReport;
import org.example.raidbots.RaidbotsReportParser;

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
            @FormParam("voidspire") String voidspireUrl,
            @FormParam("dreamrift") String dreamRiftUrl,
            @FormParam("march") String marchUrl,
            @FormParam("dungeon") String dungeonUrl) {

        List<AggregatedReport> reportEntries =
                List.of(voidspireUrl, dreamRiftUrl, marchUrl, dungeonUrl).parallelStream()
                        .map(RaidbotsReportParser::fetch)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(RaidbotsReport::aggregate)
                        .toList();

        var output = new StringOutput();
        templateEngine.render(
                "_results.jte", ResultsViewModel.fromAggregatedReports(reportEntries), output);
        return Response.ok(output.toString()).build();
    }
}
