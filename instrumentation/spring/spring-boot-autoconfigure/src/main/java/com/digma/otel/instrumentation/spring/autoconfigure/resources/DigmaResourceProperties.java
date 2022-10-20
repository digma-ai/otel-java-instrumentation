package com.digma.otel.instrumentation.spring.autoconfigure.resources;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "digma.otel.springboot.resource")
public class DigmaResourceProperties {
    private boolean enabled = true;
    /**
     * environment.
     * environment which the application runs at. possible values: PRODUCTION, QA, CI.
     * you can leave it empty and Digma will evaluate it in the following order:
     * 1. read environment variable named DEPLOYMENT_ENV
     * 2. get local hostname, and append const values [local]. for example: for local machine named myhost it will return myhost[local]
     */
    private String environment;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}
