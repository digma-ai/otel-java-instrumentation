package com.digma.otel.instrumentation.spring.autoconfigure.webmvc;

import io.opentelemetry.api.OpenTelemetry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import static org.assertj.core.api.Assertions.assertThat;

public class WebMvcControllerAspectAutoConfigurationTest {

    private final ApplicationContextRunner contextRunnerWithoutOtel = new ApplicationContextRunner()
            .withConfiguration(
                    AutoConfigurations.of(
                            TestWebApp.class, WebMvcControllerAspectAutoConfiguration.class));

    private final ApplicationContextRunner contextRunnerWithOtel = new ApplicationContextRunner()
            .withUserConfiguration(OtelConfiguration.class)
            .withConfiguration(
                    AutoConfigurations.of(
                            TestWebApp.class, WebMvcControllerAspectAutoConfiguration.class));

    @TestConfiguration(proxyBeanMethods = false)
    static class OtelConfiguration {
        @Bean
        public OpenTelemetry customOpenTelemetry() {
            return OpenTelemetry.noop();
        }
    }

    @Test
    @DisplayName("when OTEL configured and web is ENABLED should initialize WebMvcTracingFilter bean")
    void otelEnabledAndWebEnabled() {
        this.contextRunnerWithOtel
                .withPropertyValues("otel.springboot.web.enabled=true")
                .run(context ->
                        assertThat(context.getBean("codeAttributesToRequestMappingAspect", CodeAttributesToRequestMappingAspect.class))
                                .isNotNull());
    }

    @Test
    @DisplayName("when OTEL configured and web is DISABLED should NOT initialize WebMvcTracingFilter bean")
    void otelEnabledAndWebDisabled() {
        this.contextRunnerWithOtel
                .withPropertyValues("otel.springboot.web.enabled=false")
                .run(context ->
                        assertThat(context.containsBean("codeAttributesToRequestMappingAspect"))
                                .isFalse());
    }

    @Test
    @DisplayName("when OTEL not configured and web is ENABLED should initialize WebMvcTracingFilter bean")
    void otelDisabledAndWebEnabled() {
        this.contextRunnerWithoutOtel
                .withPropertyValues("otel.springboot.web.enabled=true")
                .run(context ->
                        assertThat(context.containsBean("codeAttributesToRequestMappingAspect"))
                                .isFalse());
    }

    @Configuration(proxyBeanMethods = false)
    static class TestWebApp {

        @Bean
        TestWelcomeController welcomeController() {
            return new TestWelcomeController();
        }

    }

    @Controller
    static class TestWelcomeController {

        @GetMapping("/")
        public String welcome() {
            return "welcome";
        }
    }
}
