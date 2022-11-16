# Library Instrumentation for gRPC 1.6.0+

Provides Digma OpenTelemetry instrumentation for [gRPC](https://grpc.io/).

## Quickstart

Assuming that you already using the
standard [OpenTelemetry gRPC instrumentation](https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/main/instrumentation/grpc-1.6/library)
You should add Digma instrumentation as well.
Digma provides the following extensions:

1. Server side Interceptor
   The interceptor adds to current span the following attributes:
   `code.namespace`
   `code.function`
   as described standards both in Semantic Conventions
   of [Span source code attributes](https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/trace/semantic_conventions/span-general.md#source-code-attributes)
   and [RPC common attributes](https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/trace/semantic_conventions/rpc.md#common-attributes)

### Add the following dependencies to your project:

Replace `DIGMA_OTEL_INSTR_VERSION` with
the [latest release](https://search.maven.org/search?q=g:io.opentelemetry.instrumentation%20AND%20a:opentelemetry-grpc-1.6)
.

For Maven, add the following to your `pom.xml` dependencies:

```xml

<dependencies>
    <dependency>
        <groupId>io.github.digma-ai</groupId>
        <artifactId>digma-otel-instr-grpc</artifactId>
        <version>DIGMA_OTEL_INSTR_VERSION</version>
    </dependency>
</dependencies>
```

For Gradle, add the following to your dependencies:

```groovy
implementation("io.github.digma-ai:digma-otel-instr-grpc:DIGMA_OTEL_INSTR_VERSION")
```

### Usage

The instrumentation library provides the implementation of `ServerInterceptor` to extend OpenTelemetry-based spans.

```java
// For server-side, attatch the Digma interceptor to your service, to be affective **after** otel's original one:
ServerServiceDefinition configureServerInterceptor(Opentelemetry opentelemetry,ServerServiceDefinition serviceDefinition) {
  GrpcTelemetry grpcTelemetry=GrpcTelemetry.create(opentelemetry);
  return ServiceInterceptors.intercept(serviceDefinition,
    DigmaTracingServerInterceptor.create(), // acts second
    grpcTelemetry.newServerInterceptor()    // acts first
    );
}
```
