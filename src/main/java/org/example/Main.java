package org.example;

import java.nio.file.Path;
import org.eclipse.jetty.ee10.servlet.DefaultServlet;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.servlet.ServletContainer;

public class Main {

    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws Exception {
        int port = resolvePort(args);

        var server = new Server(port);

        var context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/");

        // Static assets — served from the /static directory on the classpath.
        // DefaultServlet requires a base resource; we point it at the packaged
        // "static" folder so it works both from an exploded build and a fat JAR.
        var staticHolder = new ServletHolder("default", DefaultServlet.class);
        staticHolder.setInitParameter(
                "resourceBase",
                "dev".equalsIgnoreCase(System.getProperty("app.env", "dev"))
                        ? Path.of("src/main/resources/static").toAbsolutePath().toString()
                        : Main.class.getResource("/static").toExternalForm());
        staticHolder.setInitParameter("dirAllowed", "false");
        staticHolder.setInitParameter("pathInfoOnly", "true");
        context.addServlet(staticHolder, "/static/*");

        // Jersey servlet — serves all other requests through JAX-RS
        var jerseyHolder = new ServletHolder(new ServletContainer(new BonusRollApplication()));
        jerseyHolder.setInitOrder(1);
        context.addServlet(jerseyHolder, "/*");

        server.setHandler(context);
        server.start();

        System.out.printf("BonusRoll started on http://localhost:%d%n", port);

        server.join();
    }

    private static int resolvePort(String[] args) {
        if (args.length > 0) {
            try {
                return Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
                System.err.println("Invalid port argument, using default " + DEFAULT_PORT);
            }
        }
        String envPort = System.getenv("PORT");
        if (envPort != null) {
            try {
                return Integer.parseInt(envPort);
            } catch (NumberFormatException ignored) {
                /* fall through */
            }
        }
        return DEFAULT_PORT;
    }
}
