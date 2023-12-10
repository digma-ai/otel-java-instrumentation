package com.digma.springboot.otlp.autoconf;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.resources.ResourceBuilder;
import org.springframework.core.env.Environment;

public final class DigmaOtelSpringBootCommon {

    /**
     * Default value for application name if {@code spring.application.name} is not set.
     */
    protected static final String DEFAULT_APPLICATION_NAME = "application";

    protected static final AttributeKey<String> ATTRIBUTE_KEY_SERVICE_NAME = AttributeKey.stringKey("service.name");

    // same code as in DigmaOtelSpringBootVersion3dot1AutoConfiguration
    protected static Resource openTelemetryResourceAsInSpring3dot2(Environment environment, DigmaOpenTelemetryProperties properties) {
        String applicationName = environment.getProperty("spring.application.name", DEFAULT_APPLICATION_NAME);
        return Resource.getDefault()
                .merge(Resource.create(Attributes.of(ATTRIBUTE_KEY_SERVICE_NAME, applicationName)))
                .merge(toResource(properties));
    }

    protected static Resource toResource(DigmaOpenTelemetryProperties properties) {
        ResourceBuilder builder = Resource.builder();
        properties.getResourceAttributes().forEach(builder::put);
        return builder.build();
    }
}
