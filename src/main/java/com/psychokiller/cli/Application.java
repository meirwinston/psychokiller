package com.psychokiller.cli;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.psychokiller.MainModule;
import com.psychokiller.ResolvedProperties;
import com.psychokiller.wire.messages.User;
import com.psychokiller.ws.SimpleAuthenticator;
import com.psychokiller.ws.WebServiceApplication;
import io.dropwizard.Configuration;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;

public class Application extends WebServiceApplication{
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private static ResolvedProperties loadProperties() throws IOException {
        InputStream in = MainModule.class.getClassLoader().getResourceAsStream("dev/config.properties");
        ResolvedProperties properties = new ResolvedProperties();
        properties.load(in);
        logger.info("PROPERTIES: {}", properties);
        return properties;
    }

    public static void main(String[] args){
        try {
            String ymlPath = MainModule.class.getClassLoader().getResource( "dev/config.yml").getPath();
            logger.info("config.yml path {}", ymlPath);
            Injector injector = Guice.createInjector(new MainModule(loadProperties()));
            injector.getInstance(Application.class).run(new String[]{"server",ymlPath});

            logger.info("STARTED {}", injector);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        super.initialize(bootstrap);
        System.out.println("a");
    }

    @Override public void run(String... arguments) throws Exception {
        super.run(arguments);
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        environment.jersey().register(new BasicCredentialAuthFilter.Builder<User>()
                                              .setAuthenticator(new SimpleAuthenticator())
                                              .buildAuthFilter());
        super.run(configuration, environment);
    }
}
