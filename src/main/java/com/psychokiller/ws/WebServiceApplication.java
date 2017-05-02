package com.psychokiller.ws;

import com.google.inject.Inject;
import com.psychokiller.ResolvedProperties;
import com.psychokiller.ws.resources.AuthResource;
import com.psychokiller.ws.resources.MainResource;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.server.session.SessionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.DispatcherType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.EnumSet;

/**
 * @author Meir Winston
 */
public class WebServiceApplication extends Application<Configuration> {
    final static Logger logger = LoggerFactory.getLogger(WebServiceApplication.class);

    @Inject
    private MainResource mainResource;

    @Inject
    private AuthResource authResource;

    @Inject
    private AuthFilter authFilter;

    @Inject
    private ResolvedProperties properties;

    @Inject
    public WebServiceApplication(){
    }

    @Override
    public String getName() {
        return "psychokiller";
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        logger.info("initialize");
//        bootstrap.addBundle(new AssetsBundle("/swagger/", "/swagger")); //swagger
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        logger.info("run");
        // Register the custom ExceptionMapper
        environment.jersey().register(new GenericExceptionMapper());

        environment.jersey().register(mainResource);
        environment.jersey().register(authResource);

        //to be able to get session: request.getSession()
        environment.servlets().setSessionHandler(new SessionHandler());

        environment.servlets()
                .addFilter("AuthFilter", authFilter)
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/topic/*", "/organization/*");
    }

    private static String getPulicIP() throws IOException {
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));

        String ip = in.readLine(); //you get the IP as a String
        return ip;
    }

}
