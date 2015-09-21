package com.alevohin;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import javax.servlet.Filter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.rules.ExternalResource;

/**
 * Created by yuriy.alevohin on 21.09.2015.
 * JUnit Rule to start embedded Jetty at random port and bind
 * custom Guice modules. Please see <code>CustomGuiceFilterWorks</code>
 * for example of usage.
 */
public class ServerRule extends ExternalResource {

    private final Server server;
    private final Module[] modules;
    private final int port;
    private final Class<? extends Filter> filterClass;

    /**
     * Ctor.
     * @param filterClass - Class of Filter.
     * @param modules - Guice modules to bind.
     */
    public ServerRule(Class<? extends Filter> filterClass, Module... modules) {
        this.modules = modules;
        this.port = randomPort();
        this.filterClass = filterClass;
        this.server = new Server(port);
    }

    @Override
    protected void before() {
        server.setHandler(createHandler());
        try {
            server.start();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private ServletContextHandler createHandler() {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.addEventListener(new GuiceServletContextListener() {
            @Override
            public Injector getInjector() {
                return Guice.createInjector(modules);
            }
        });
        if (filterClass != null) {
            context.addFilter(filterClass, "/*", null);
        }
        context.addServlet(DefaultServlet.class, "/");
        return context;
    }

    private int randomPort() {
        try (ServerSocket socket = new ServerSocket()) {
            socket.setReuseAddress(true);
            final InetSocketAddress endpoint = new InetSocketAddress(
                InetAddress.getLoopbackAddress(),
                0
            );
            socket.bind(endpoint);
            return socket.getLocalPort();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Port used for starting Jetty.
     * @return
     */
    public int getPort() {
        return port;
    }

    @Override
    protected void after() {
        try {
            if (server != null) {
                server.stop();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
