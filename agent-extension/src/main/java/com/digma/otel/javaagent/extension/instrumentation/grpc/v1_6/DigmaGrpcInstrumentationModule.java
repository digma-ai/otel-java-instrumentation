package com.digma.otel.javaagent.extension.instrumentation.grpc.v1_6;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * DigmaGrpcInstrumentationModule.
 */
@AutoService(InstrumentationModule.class)
public class DigmaGrpcInstrumentationModule extends InstrumentationModule {

    public DigmaGrpcInstrumentationModule() {
        super("digma-grpc", "digma-grpc-1.6");
    }

    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        return asList(new DigmaGrpcServerBuilderInstrumentation());
    }

}
