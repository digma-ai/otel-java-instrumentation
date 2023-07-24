package com.digma.springboot.otlp.autoconf;

import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.tracing.ConditionalOnEnabledTracing;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * OpenTelemetry ( otel ) AutoConfiguration for OtlpGrpcSpanExporter
 */
@AutoConfiguration
@ConditionalOnClass(OtlpGrpcSpanExporter.class)
public class DigmaOtlpAutoConfiguration {

    /**
     * see class org.springframework.boot.actuate.autoconfigure.tracing.otlp.OtlpAutoConfiguration (since 3.1)
     * see class org.springframework.boot.actuate.autoconfigure.tracing.otlp.OtlpProperties (since 3.1)
     */
    @Bean
    @ConditionalOnProperty(prefix = "management.otlp.tracing", name = "endpoint")
    @ConditionalOnEnabledTracing
    OtlpGrpcSpanExporter otlpGrpcSpanExporter(@Value("management.otlp.tracing.endpoint") String otlpEndpoint) {
        OtlpGrpcSpanExporter bean = OtlpGrpcSpanExporter.builder()
                .setEndpoint(otlpEndpoint)
                .build();
        return bean;
    }

}
