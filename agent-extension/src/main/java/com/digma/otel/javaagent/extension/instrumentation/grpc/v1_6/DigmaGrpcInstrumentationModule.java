package com.digma.otel.javaagent.extension.instrumentation.grpc.v1_6;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DigmaGrpcInstrumentationModule.
 */
@AutoService(InstrumentationModule.class)
public class DigmaGrpcInstrumentationModule extends InstrumentationModule {

    public DigmaGrpcInstrumentationModule() {
        super("digma-grpc", "digma-grpc-1.6");
    }

    @Override
    public int order() {
        return -50; // should be called BEFORE the OTEL GrpcInstrumentationModule
    }

    @Override
    public boolean isHelperClass(String className) {
        // in order to pass the muzzle check
        // need to define the interceptor as helper class - it will be injected to application class path
        return className.startsWith("com.digma.otel.instrumentation.grpc.v1_6.DigmaTracingServerInterceptor");
    }

    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        return Collections.singletonList(new DigmaGrpcServerBuilderInstrumentation());
    }

    @Override
    public List<String> getAdditionalHelperClassNames() {
        List<String> classNames = new ArrayList<>();
        classNames.add("io.opentelemetry.instrumentation.api.instrumenter.util.ClassAndMethod");
        classNames.add("io.opentelemetry.instrumentation.api.instrumenter.code.CodeAttributesGetter");
        classNames.add("io.opentelemetry.instrumentation.api.instrumenter.util.AutoValue_ClassAndMethod");
        return classNames;
    }
}
