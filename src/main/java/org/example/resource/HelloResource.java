package org.example.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Returns small HTML fragments consumed by HTMX.
 */
@Path("/hello")
public class HelloResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String hello() {
        return "<p>Hello from the server! <strong>HTMX + Jersey + Jte</strong> is working.</p>";
    }
}
