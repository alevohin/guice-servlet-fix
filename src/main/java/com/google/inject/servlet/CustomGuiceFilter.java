package com.google.inject.servlet;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.lang.reflect.Field;
import javax.inject.Inject;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

/**
 *  Created by yuriy.alevohin on 21.09.2015.
 *  Extension for GuiceFilter for using it with multiple injectors.
 */
public class CustomGuiceFilter extends GuiceFilter {

    private static Injector lastCreatedInjector = null;

    @Inject
    static void preInit(Injector injector) {
        synchronized (CustomGuiceFilter.class) {
            lastCreatedInjector = injector;
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        synchronized (CustomGuiceFilter.class) {
            if (lastCreatedInjector != null) {
                setLocalPipeline(lastCreatedInjector.getInstance(FilterPipeline.class));
                lastCreatedInjector = null;
            } else {
                System.err.println(
                        this.getClass().getCanonicalName() +
                                ": Injector not found. FilterPipeline injection skipped!");
            }
        }

        super.init(filterConfig);
    }

    private void setLocalPipeline(FilterPipeline pipeline) {
        try {
            Field injectedPipeline = GuiceFilter.class.getDeclaredField("injectedPipeline");
            injectedPipeline.setAccessible(true);
            injectedPipeline.set(this, pipeline);
            injectedPipeline.setAccessible(false);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Module to configure Injector.
     * @return
     */
    public static Module staticInjectionModule() {
        return new AbstractModule() {
            @Override
            protected void configure() {
                requestStaticInjection(CustomGuiceFilter.class);
            }
        };
    }
}
