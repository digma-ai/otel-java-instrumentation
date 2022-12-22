# Development Notes

## Origins

This OpenTelemetry agent-extension is based on
the [Otel Extension Example](https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/main/examples/extension)

## OpenTelemetry Agent configuration

[OpenTelemetry Agent configuration](https://opentelemetry.io/docs/instrumentation/java/automatic/)

The Entries in the following link might be set within file of `otel.javaagent.configuration-file`
[OpenTelemetry SDK Autoconfigure](https://github.com/open-telemetry/opentelemetry-java/blob/main/sdk-extensions/autoconfigure/README.md#opentelemetry-sdk-autoconfigure)
for example `otel.exporter.otlp.traces.endpoint=http://localhost:4317`

### Instrumenting PetClinic

```shell
cd C:/Users/arik/Documents/GitHub/spring-petclinic

set    CODE_PACKAGE_PREFIXES=org.springframework.samples.petclinic
export CODE_PACKAGE_PREFIXES=org.springframework.samples.petclinic 

java \
 -javaagent:_lib/opentelemetry-javaagent-1.21.0.jar \
 -Dotel.javaagent.configuration-file=_lib/opentelemetry-javaagent.properties \
 -Dotel.javaagent.extensions=_lib/digma-agent-extension-0.4.4-SNAPSHOT-all.jar \
 -jar build/libs/spring-petclinic-2.7.5-alpha.jar
```

if want to debug add the following before the jar
-Dotel.javaagent.debug=true \

