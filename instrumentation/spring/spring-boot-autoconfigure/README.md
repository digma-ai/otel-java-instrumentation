# Digma - OpenTelemetry Spring Auto-Configuration

It extends and relies on
the [OpenTelemetry Spring Boot Auto Configuration](https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/main/instrumentation/spring/spring-boot-autoconfigure)

## Quickstart

### Add these dependencies to your project.

Replace `DIGMA_INSTRUMENTATION_VERSION` with the latest stable [release](https://search.maven.org/search?q=g:com.digma).

- Minimum version: `0.0.8`

For Gradle add to your dependencies:

```groovy
//digma opentelemetry common auto-configuration
implementation("com.digma:digma-otel-instr-common:DIGMA_INSTRUMENTATION_VERSION")
//digma opentelemetry spring boot auto-configuration
implementation("com.digma:digma-otel-instr-spring-boot:DIGMA_INSTRUMENTATION_VERSION")
```

### Features

#### Digma OpenTelemetry Auto Configuration

#### Digma Spring Web MVC Auto Configuration

This feature relies on
the [existing auto configuration for Spring WebMVC controllers](https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/main/instrumentation/spring/spring-boot-autoconfigure#spring-web-mvc-auto-configuration)
This feature autoconfigures extra instrumentation for Spring WebMVC controllers by using spring-aop to wrap methods
annotated with `@GetMapping`, `@PostMapping` on controllers classes (annotated with `@Controller`).
It will add the following attributes to existing span:
`code.function`
`code.namespace`

#### Spring Support

Auto-configuration is natively supported by Springboot applications. To enable these features in "vanilla"
use `@EnableDigmaForSpringBoot` to complete a component scan of this package.

##### Usage

```java
import io.opentelemetry.instrumentation.spring.autoconfigure.EnableOpenTelemetry;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDigmaForSpringBoot
public class OpenTelemetryConfig {
}
```

#### Configuration Properties

##### Resource Properties

| Feature  | Property                                   | Default Value | Comments                                                                                                                                                                                                                      |
|----------|--------------------------------------------|---------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Resource | digma.otel.springboot.resource.enabled     | `true`        |                                                                                                                                                                                                                               |
|          | digma.otel.springboot.resource.environment | ``            | digma environment, value for example: PRODCTION, CI, QA. it overrides the value of expected environment variable named DEPLOYMENT_ENV. leave it empty if its the developer environment (by default it will take the hostname) |
