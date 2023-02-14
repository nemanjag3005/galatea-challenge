package com.quickstep;

import com.google.common.base.Joiner;
import com.quickstep.backend.healthcheck.HealthCheckServlet;
import com.quickstep.misc.guice.server.GuiceVaadinServlet;
import com.quickstep.misc.guice.server.VaadinModule;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.Servlet;
import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URL;

public class App {

    static {
        //Force JBoss logging to SLF4J explicitly
        System.setProperty("org.jboss.logging.provider", "slf4j");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Webapp with args: " + Joiner.on(",").join(args));
        HideBanners();
        VaadinModule.initializeProfiles(args);
        var properties = VaadinModule.getProperties();
        //Port 8080
        final var server = new Server(8080);

        final var context = new WebAppContext();
        context.setBaseResource(findWebRoot());
        context.setContextPath("/");
        // Exploded war or not.
        context.setExtractWAR(false);

        // It pulls the respective config from the VaadinServlet.
        final ServletHolder vaadinServletHolder = context.addServlet(GuiceVaadinServlet.class, "/*");
        vaadinServletHolder.setInitOrder(1);


        final ServletHolder healthCheckServletHolder = context.addServlet(HealthCheckServlet.class, HealthCheckServlet.SERVLET_PATH);
        healthCheckServletHolder.setInitOrder(4);

        context.addEventListener(new LifeCycle.Listener() {

            @Override
            public void lifeCycleStarted(LifeCycle event) {
                try {
                    // attempt to reuse the injector created for VaadinServlet
                    GuiceVaadinServlet vaadinServlet = (GuiceVaadinServlet) vaadinServletHolder.getServlet();
                    // this runs when servlet is already created by jetty

                    Servlet healthCheckServlet = healthCheckServletHolder.getServlet();
                    vaadinServlet.getInjector().injectMembers(healthCheckServlet);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*");
        context.setConfigurationDiscovered(true);
        context.setParentLoaderPriority(true);
        server.setHandler(context);

        server.start();

        context.getSessionHandler().setMaxInactiveInterval(Integer.parseInt(properties.getProperty("demo.config.session.timeout")));

        server.join();
    }

    //Jooq Banner disable
    //https://stackoverflow.com/questions/28272284/how-to-disable-jooqs-self-ad-message-in-3-4
    private static void HideBanners() {
        System.setProperty("org.jooq.no-logo", "true");
        System.setProperty("org.jooq.no-tips", "true");
    }


    @NotNull
    private static Resource findWebRoot() throws MalformedURLException {
        // don't look up directory as a resource, it's unreliable: https://github.com/eclipse/jetty.project/issues/4173#issuecomment-539769734
        // instead we'll look up the /webapp/sw.js and retrieve the parent folder from that.
        final URL f = App.class.getResource("/webapp/.root");
        if (f == null) {
            throw new IllegalStateException("Invalid state: the resource /webapp/.root doesn't exist, has webapp been packaged in as a resource?");
        }
        final String url = f.toString();
        if (!url.endsWith("/.root")) {
            throw new RuntimeException("Parameter url: invalid value " + url + ": doesn't end with /.root");
        }
        System.err.println("/webapp/.root is " + f);

        // Resolve file to directory
        URL webRoot = new URL(url.substring(0, url.length() - 5));
        System.err.println("WebRoot is " + webRoot);
        return Resource.newResource(webRoot);
    }
}
