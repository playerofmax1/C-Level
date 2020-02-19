package com.clevel.kudu.api.system;

import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ContextListener implements ServletContextListener {
    @Inject
    private Logger log;
    @Inject
    private Application app;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        log.debug("context initializing...");

        ServletContext ctx = servletContextEvent.getServletContext();
        app.setRealPath(ctx.getRealPath("/"));

        app.loadConfiguration();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        log.debug("context destroyed.");
    }
}
