package com.psychokiller.cli;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.psychokiller.MainModule;
import com.psychokiller.ResolvedProperties;
import com.psychokiller.ws.WebServiceApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;

public class Application {
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
            injector.getInstance(WebServiceApplication.class).run(new String[]{"server",ymlPath});

            logger.info("STARTED {}", injector);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            logger.error(e.getMessage(), e);
        }
    }
}
