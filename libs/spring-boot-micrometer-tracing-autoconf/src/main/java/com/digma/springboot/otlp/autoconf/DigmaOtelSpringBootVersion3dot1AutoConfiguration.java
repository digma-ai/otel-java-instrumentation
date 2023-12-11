package com.digma.springboot.otlp.autoconf;

import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import org.springframework.boot.actuate.autoconfigure.tracing.SdkTracerProviderBuilderCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import static com.digma.springboot.otlp.autoconf.DigmaOtelSpringBootCommon.openTelemetryResourceAsInSpring3dot2;

/**
 * support spring boot 3.1.0
 * inspired by class org.springframework.boot.actuate.autoconfigure.opentelemetry.OpenTelemetryAutoConfiguration since version 3.2.0
 *
 * https://github.com/spring-projects/spring-boot/blob/main/spring-boot-project/spring-boot-actuator-autoconfigure/src/main/java/org/springframework/boot/actuate/autoconfigure/opentelemetry/OpenTelemetryAutoConfiguration.java
 * https://github.com/spring-projects/spring-boot/blob/3.2.x/spring-boot-project/spring-boot-actuator-autoconfigure/src/main/java/org/springframework/boot/actuate/autoconfigure/opentelemetry/OpenTelemetryAutoConfiguration.java
 *
 * checked with versions:
 *  3.0
 *  3.1
 *  3.2
 */
@AutoConfiguration
@ConditionalOnClass(name = {
        "org.springframework.boot.actuate.autoconfigure.tracing.SdkTracerProviderBuilderCustomizer", // this class exists since 3.1
        "io.opentelemetry.sdk.OpenTelemetrySdk" // relates to OTEL at all
})
@EnableConfigurationProperties(DigmaOpenTelemetryProperties.class)
public class DigmaOtelSpringBootVersion3dot1AutoConfiguration {

    /**
     *
     * in version 3.2.0 the class Resource is being provided - thats why conditioning on it
     */
    @Bean
    @ConditionalOnMissingClass("org.springframework.boot.actuate.autoconfigure.opentelemetry.OpenTelemetryProperties") // this class exists since 3.2, so skip this bean if its 3.2 or later
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

}
