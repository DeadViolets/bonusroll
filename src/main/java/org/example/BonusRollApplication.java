package org.example;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/")
public class BonusRollApplication extends ResourceConfig {

    public BonusRollApplication() {
        packages("org.example.resource");
        register(new JteBinder());
    }
}
