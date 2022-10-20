package com.digma.otel.instrumentation.spring.autoconfigure;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Auto-configures Digma (OpenTelemetry) for SpringBoot application
 */
@Configuration
@ComponentScan(basePackages = "com.digma.otel.instrumentation.spring.autoconfigure")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableDigmaForSpringBoot {
}
