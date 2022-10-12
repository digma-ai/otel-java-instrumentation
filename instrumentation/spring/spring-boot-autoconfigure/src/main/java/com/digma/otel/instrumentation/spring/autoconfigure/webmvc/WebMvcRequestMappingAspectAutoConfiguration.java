package com.digma.otel.instrumentation.spring.autoconfigure.webmvc;

import io.opentelemetry.api.OpenTelemetry;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;

/** Configures {@link CodeAttributesToRequestMappingAspect} to trace bean methods annotated with {@link RequestMapping}. */
@Configuration
@EnableConfigurationProperties(WebMvcProperties.class)
@ConditionalOnProperty(prefix = "otel.springboot.web", name = "enabled", matchIfMissing = true)
@ConditionalOnClass(Aspect.class)
@ConditionalOnBean(OpenTelemetry.class)
public class WebMvcRequestMappingAspectAutoConfiguration {

  @Bean
  @ConditionalOnClass(RequestMapping.class)
  public CodeAttributesToRequestMappingAspect codeAttributesToRequestMappingAspect(
      OpenTelemetry openTelemetry) {

    return new CodeAttributesToRequestMappingAspect();
  }

}
