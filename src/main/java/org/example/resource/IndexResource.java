package org.example.resource;

import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

@Path("/")
public class IndexResource {

    private final TemplateEngine templateEngine;

    @Inject
    public IndexResource(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String index() {
        var output = new StringOutput();
        templateEngine.render("index.jte", Map.of(), output);
        return output.toString();
    }
}
