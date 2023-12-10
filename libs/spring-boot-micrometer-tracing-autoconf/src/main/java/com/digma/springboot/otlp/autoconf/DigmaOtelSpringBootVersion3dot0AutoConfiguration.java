package com.digma.springboot.otlp.autoconf;

import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Objects;
import java.util.stream.Stream;

import static com.digma.springboot.otlp.autoconf.DigmaOtelSpringBootCommon.openTelemetryResourceAsInSpring3dot2;

/**
 * support spring boot 3.0
 */
@AutoConfiguration
@ConditionalOnClass(OpenTelemetrySdk.class)
@EnableConfigurationProperties(DigmaOpenTelemetryProperties.class)
public class DigmaOtelSpringBootVersion3dot0AutoConfiguration {

    /**
     * SdkTracerProviderBuilderCustomizer exists since 3.1.0 thats why conditioning on missing of it
     *
     *  @see org.springframework.boot.actuate.autoconfigure.tracing.SdkTracerProviderBuilderCustomizer (since 3.1.0)
     *  https://github.com/spring-projects/spring-boot/blob/3.1.x/spring-boot-project/spring-boot-actuator-autoconfigure/src/main/java/org/springframework/boot/actuate/autoconfigure/tracing/SdkTracerProviderBuilderCustomizer.java
     */
    // support spring boot 3.0
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingClass("org.springframework.boot.actuate.autoconfigure.tracing.SdkTracerProviderBuilderCustomizer")
    static class SpringBoot3dot0Configuration {

        /**
         * took same code as in 3.1.x - just init the resource with #openTelemetryResourceAsInSpring3dot2.
         * see https://github.com/spring-projects/spring-boot/blob/3.1.x/spring-boot-project/spring-boot-actuator-autoconfigure/src/main/java/org/springframework/boot/actuate/autoconfigure/tracing/OpenTelemetryAutoConfiguration.java#L102
         */
        @Bean
        SdkTracerProvider sb3dot0OtelSdkTracerProvider(Environment environment, ObjectProvider<SpanProcessor> spanProcessors, Sampler sampler, DigmaOpenTelemetryProperties properties) {
            Resource springResource = openTelemetryResourceAsInSpring3dot2(environment, properties);
            SdkTracerProviderBuilder builder = SdkTracerProvider.builder().setSampler(sampler).setResource(Resource.getDefault().merge(springResource));
            Stream<SpanProcessor> var10000 = spanProcessors.orderedStream();
            Objects.requireNonNull(builder);
            var10000.forEach(builder::addSpanProcessor);
            return builder.build();
        }
    }
}