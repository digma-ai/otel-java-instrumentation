package com.digma.springboot.otlp.autoconf;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * exact file content as in org.springframework.boot.actuate.autoconfigure.opentelemetry.OpenTelemetryProperties (since sb 3.2.0)
 *
 * see org.springframework.boot.actuate.autoconfigure.opentelemetry.OpenTelemetryProperties
 * see https://github.com/spring-projects/spring-boot/blob/main/spring-boot-project/spring-boot-actuator-autoconfigure/src/main/java/org/springframework/boot/actuate/autoconfigure/opentelemetry/OpenTelemetryProperties.java
 * see https://github.com/spring-projects/spring-boot/blob/3.2.x/spring-boot-project/spring-boot-actuator-autoconfigure/src/main/java/org/springframework/boot/actuate/autoconfigure/opentelemetry/OpenTelemetryProperties.java
 *
 * care about property (and system property) entries:
 * management.opentelemetry.resource-attributes.key1=value1
 *  (environment variable equivalent : MANAGEMENT_OPENTELEMETRY_RESOURCE-ATTRIBUTES_key2=value2
 */
@ConfigurationProperties(prefix = "management.opentelemetry")
public class DigmaOpenTelemetryProperties {

    /**
     * Resource attributes.
     */
    private Map<String, String> resourceAttributes = new HashMap<>();

    public Map<String, String> getResourceAttributes() {
        return this.resourceAttributes;
    }

    public void setResourceAttributes(Map<String, String> resourceAttributes) {
        this.resourceAttributes = resourceAttributes;
    }

}