package com.digma.otel.instrumentation.spring.autoconfigure.resources;

import io.opentelemetry.instrumentation.spring.autoconfigure.OpenTelemetryAutoConfiguration;
import io.opentelemetry.sdk.autoconfigure.spi.ResourceProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DigmaResourceAutoConfiguration.
 * looks similar to {@link io.opentelemetry.instrumentation.spring.autoconfigure.resources.OtelResourceAutoConfiguration}.
 */
@Configuration
@EnableConfigurationProperties(DigmaResourceProperties.class)
@AutoConfigureBefore(OpenTelemetryAutoConfiguration.class)
@ConditionalOnProperty(prefix = "digma.otel.springboot.resource", name = "enabled", matchIfMissing = true)
public class DigmaResourceAutoConfiguration {

    /**
     * this bean will get merged with {@link OpenTelemetryAutoConfiguration.OpenTelemetryBeanConfig#otelResource}
     */
    @Bean
    public ResourceProvider digmaResourceProvider(DigmaResourceProperties properties) {
        return new DigmaResourceProvider(properties);
    }

}
