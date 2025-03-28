package com.example.demo;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import org.atmosphere.cache.UUIDBroadcasterCache;
import org.atmosphere.cpr.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class AtmosphereConfig {

    private static final Logger log = LoggerFactory.getLogger(AtmosphereConfig.class);

    @Bean
    public AtmosphereFramework atmosphereFramework() {
        // Create framework instance with auto-init disabled
        AtmosphereFramework framework = new AtmosphereFramework();
        framework.setAsyncSupport(new Tomcat7CometSupport(framework.getAtmosphereConfig()));

        // Configure broadcaster cache
        framework.addInitParameter(
                "org.atmosphere.cpr.broadcasterCacheClass",
                UUIDBroadcasterCache.class.getName()
        );

        // Disable WebSocket if not needed
        framework.addInitParameter("org.atmosphere.cpr.jsr356", "false");

        try {
            framework.init();
            log.info("Atmosphere framework initialized with UUIDBroadcasterCache");
            return framework;
        } catch (Exception e) {
            log.error("Failed to initialize Atmosphere framework", e);
            throw new IllegalStateException("Atmosphere initialization failed", e);
        }
    }

    @Bean
    public BroadcasterFactory broadcasterFactory(AtmosphereFramework framework) {
        return framework.getBroadcasterFactory();
    }

    private class Tomcat7CometSupport implements AsyncSupport {
        public Tomcat7CometSupport(org.atmosphere.cpr.AtmosphereConfig atmosphereConfig) {
        }

        @Override
        public String getContainerName() {
            return "";
        }

        @Override
        public void init(ServletConfig servletConfig) throws ServletException {

        }

        @Override
        public Action service(AtmosphereRequest atmosphereRequest, AtmosphereResponse atmosphereResponse) throws IOException, ServletException {
            return null;
        }

        @Override
        public void action(AtmosphereResource atmosphereResource) {

        }

        @Override
        public boolean supportWebSocket() {
            return false;
        }

        @Override
        public AsyncSupport complete(AtmosphereResource atmosphereResource) {
            return null;
        }
    }
}