package org.example;

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

        // Jersey servlet — serves all requests through JAX-RS
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
            } catch (NumberFormatException ignored) { /* fall through */ }
        }
        return DEFAULT_PORT;
    }
}
