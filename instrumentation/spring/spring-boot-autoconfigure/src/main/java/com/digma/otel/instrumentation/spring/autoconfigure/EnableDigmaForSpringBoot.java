package com.digma.otel.instrumentation.spring.autoconfigure;


import com.digma.otel.instrumentation.common.DigmaCommon;
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
    /**
     * controls digma deployment environment. values for example: PRODUCTION, CI.
     * 99 perecnt of time, better leave it empty, and let digma use the default evaluation (try environment variable named DEPLOYMENT_ENV, and if its empty it will take the local hostname)
     *
     * @see DigmaCommon#evaluateEnvironment
     */
    String environmentOverride() default "";
}
