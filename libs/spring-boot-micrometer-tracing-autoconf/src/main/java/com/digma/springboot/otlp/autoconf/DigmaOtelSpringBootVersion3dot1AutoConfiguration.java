package com.digma.springboot.otlp.autoconf;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.resources.ResourceBuilder;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import org.springframework.boot.actuate.autoconfigure.tracing.SdkTracerProviderBuilderCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * inspired by class org.springframework.boot.actuate.autoconfigure.opentelemetry.OpenTelemetryAutoConfiguration since version 3.2.0
 *
 * https://github.com/spring-projects/spring-boot/blob/main/spring-boot-project/spring-boot-actuator-autoconfigure/src/main/java/org/springframework/boot/actuate/autoconfigure/opentelemetry/OpenTelemetryAutoConfiguration.java
 * https://github.com/spring-projects/spring-boot/blob/3.2.x/spring-boot-project/spring-boot-actuator-autoconfigure/src/main/java/org/springframework/boot/actuate/autoconfigure/opentelemetry/OpenTelemetryAutoConfiguration.java
 */
@AutoConfiguration
@ConditionalOnClass(OpenTelemetrySdk.class)
@EnableConfigurationProperties(DigmaOpenTelemetryProperties.class)
public class DigmaOtelSpringBootVersion3dot1AutoConfiguration {

    /**
     * Default value for application name if {@code spring.application.name} is not set.
     */
    protected static final String DEFAULT_APPLICATION_NAME = "application";

    protected static final AttributeKey<String> ATTRIBUTE_KEY_SERVICE_NAME = AttributeKey.stringKey("service.name");


    /**
     *
     * in version 3.2.0 the class Resource is being provided - thats why conditioning on it
     */
    @Bean
    @ConditionalOnMissingBean(Resource.class)
    SdkTracerProviderBuilderCustomizer otelSdkTracerProviderBuilderCustomizer(Environment environment, DigmaOpenTelemetryProperties properties) {
        return new ResourceAttributesSdkCustomizer(environment, properties);
    }

    static class ResourceAttributesSdkCustomizer implements SdkTracerProviderBuilderCustomizer {
        //	class Abc {
        Environment environment;
        DigmaOpenTelemetryProperties properties;

        public ResourceAttributesSdkCustomizer(Environment environment, DigmaOpenTelemetryProperties properties) {
            this.environment = environment;
            this.properties = properties;
        }

        @Override
        public void customize(SdkTracerProviderBuilder builder) {
            Resource resource = openTelemetryResourceAsInSpring3dot2(environment, properties);
            builder.setResource(resource);
        }
    }

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
