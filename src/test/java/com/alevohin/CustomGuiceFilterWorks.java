package com.alevohin;

import com.google.inject.Singleton;
import com.google.inject.servlet.CustomGuiceFilter;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import javax.ws.rs.core.Response;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Created by yuriy.alevohin on 21.09.2015.
 * Test to check CustomGuiceFilter work. If you comment
 * CustomGuiceFilter.staticInjectionModule() for ServerRule instantiation,
 * you will see one of tests failed.
 */
public class CustomGuiceFilterWorks {

    @Rule
    public ServerRule serverOK = new ServerRule(
        CustomGuiceFilter.class,
        new ServletModule() {
            @Override
            protected void configureServlets() {
                bind(EndpointOK.class).in(Singleton.class);
                serve("/*").with(GuiceContainer.class);
            }
        },
        CustomGuiceFilter.staticInjectionModule()
    );

    @Rule
    public ServerRule serverError = new ServerRule(
        CustomGuiceFilter.class,
        new ServletModule() {
            @Override
            protected void configureServlets() {
                bind(EndpointError.class).in(Singleton.class);
                serve("/*").with(GuiceContainer.class);
            }
        },
        CustomGuiceFilter.staticInjectionModule()
    );

    @Test
    public void okEndpointWorks() throws Exception {
        final String url = String.format("http://localhost:%s/ok", serverOK.getPort());
        System.out.println(url);
        MatcherAssert.assertThat(
            EntityUtils.toString(
                Request.Get(url)
                    .addHeader("User-Agent", "Spectrum")
                    .execute()
                    .returnResponse()
                    .getEntity(),
                "UTF-8"
            ),
            Matchers.equalTo("OK")
        );
    }

    @Test
    public void errorEndpointWorks() throws Exception {
        final String url = String.format("http://localhost:%s/error", serverError.getPort());
        System.out.println(url);
        MatcherAssert.assertThat(
            Request.Get(url)
                .addHeader("User-Agent", "Spectrum")
                .execute()
                .returnResponse()
                .getStatusLine()
                .getStatusCode(),
            Matchers.equalTo(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
        );
    }
}
