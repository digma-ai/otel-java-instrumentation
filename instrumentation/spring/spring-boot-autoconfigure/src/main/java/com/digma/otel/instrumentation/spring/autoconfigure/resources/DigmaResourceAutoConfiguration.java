package com.digma.otel.instrumentation.spring.autoconfigure.resources;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.instrumentation.spring.autoconfigure.OpenTelemetryAutoConfiguration;
import io.opentelemetry.instrumentation.spring.autoconfigure.resources.OtelResourceProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ResourceProvider;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.opentelemetry.api.common.AttributeKey.stringKey;

@Configuration
@EnableConfigurationProperties(OtelResourceProperties.class)
@AutoConfigureBefore(OpenTelemetryAutoConfiguration.class)
@ConditionalOnProperty(prefix = "digma.otel.springboot.resource", name = "enabled", matchIfMissing = true)
public class DigmaResourceAutoConfiguration {

    @Bean
    public ResourceProvider dummyResourceProvider() {
        return new DumResourceProvider();
    }

    /**
     * this bean will get merged with {@link OpenTelemetryAutoConfiguration.OpenTelemetryBeanConfig#otelResource}
     */
    @Bean
    public ResourceProvider digmaResourceProvider(DigmaResourceProperties properties) {
        return new DigmaResourceProvider(properties);
    }

    private static final AttributeKey<String> DUMMY_ATTR = stringKey("dummy.attr");

    static class DumResourceProvider implements ResourceProvider {

        @Override
        public Resource createResource(ConfigProperties config) {
            Attributes attrs = Attributes.of(
                    DUMMY_ATTR, "dummy.val");

            return Resource.create(attrs,
                    ResourceAttributes.SCHEMA_URL);
        }
    }

}
