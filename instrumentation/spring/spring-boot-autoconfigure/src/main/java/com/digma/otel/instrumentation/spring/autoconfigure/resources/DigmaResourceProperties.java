package com.digma.otel.instrumentation.spring.autoconfigure.resources;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "digma.otel.springboot.resource")
public class DigmaResourceProperties {
    private boolean enabled = true;
    private String environmentOverride;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEnvironmentOverride() {
        return environmentOverride;
    }

    public void setEnvironmentOverride(String environmentOverride) {
        this.environmentOverride = environmentOverride;
    }
}
