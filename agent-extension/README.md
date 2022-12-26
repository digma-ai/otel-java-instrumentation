# Digma - OpenTelemetry Agent Extension

## Quickstart

This module is an extension to
existing [OpenTelemetry JavaAgent](https://github.com/open-telemetry/opentelemetry-java-instrumentation#getting-started)

### How to apply the OpenTelemetry agent at all

1. Download the latest version
   of [opentelemetry-javaagent.jar](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar)
   .
2. Add the agent definition to your application run command as follows:

```
java -javaagent:path/to/opentelemetry-javaagent.jar \
     -jar myapp.jar
```

### How to apply Digma extension

1. Download the latest version
   of [digma-otel-agent-extension.jar](https://github.com/digma-ai/otel-java-instrumentation/releases/latest)
2. Add the extension definition as follows:

```
java -javaagent:path/to/opentelemetry-javaagent.jar \
     -Dotel.javaagent.extensions=path/to/digma-otel-agent-extension.jar \
     -jar myapp.jar
```

### Supported technologies

- SpringMVC - supports instrumenting annotation of @Controller and @RestController.
  it includes SpringBoot applications which use those common annotations.
- JAX-RS 2.0
- GRPC 1.6 - see [GRPC Library](../instrumentation/grpc-16/library/README.md)
  <br>
  <b>Known issue</b>: currently (as of 0.5.30), if you have your own server side interceptors, Digma interceptor won't
  work
  correctly. (see issue #19).

### Configuring the environment

The extension supports overriding default values through environment variables or SystemProperties.
here are the available config entries:

| Description                                                                                                                    | Environment Variable  | System Property       | default value        |
|--------------------------------------------------------------------------------------------------------------------------------|-----------------------|-----------------------|----------------------|
| Allows defining environment name. <br>values for example: PROD, CI                                                             | DEPLOYMENT_ENV        | DEPLOYMENT_ENV        | <my-hostname>[local] |
| Package prefixes of you application/service. value is comma separated. <br>value for example: com.mycomp.myservice,aaa.bbb.ccc | CODE_PACKAGE_PREFIXES | CODE_PACKAGE_PREFIXES | <empty>              |

## Origins

This OpenTelemetry agent-extension is based on
the [Otel Extension Example](https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/main/examples/extension)
