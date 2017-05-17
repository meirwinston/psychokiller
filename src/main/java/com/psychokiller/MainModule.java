package com.psychokiller;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.psychokiller.cli.Application;
import com.psychokiller.ws.*;
import com.psychokiller.ws.resources.AuthResource;
import com.psychokiller.ws.resources.MainResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainModule extends AbstractModule{
    private static final Logger logger = LoggerFactory.getLogger(MainModule.class);
    private ResolvedProperties properties;

    public MainModule(ResolvedProperties properties){
        this.properties = properties;
    }

    @Override
    protected void configure() {
        bind(ResolvedProperties.class).toInstance(this.properties);
        Names.bindProperties(binder(),this.properties);

        install(new DbModule());
        bind(Application.class).asEagerSingleton();
        bind(MainResource.class).asEagerSingleton();
        bind(AuthResource.class).asEagerSingleton();
//        bind(AuthFilter.class).asEagerSingleton();
    }

}
