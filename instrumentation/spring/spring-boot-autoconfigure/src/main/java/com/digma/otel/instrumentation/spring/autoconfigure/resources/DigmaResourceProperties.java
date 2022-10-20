package com.digma.otel.instrumentation.spring.autoconfigure.resources;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "digma.otel.springboot.resource")
public class DigmaResourceProperties {
    private boolean enabled = true;
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
