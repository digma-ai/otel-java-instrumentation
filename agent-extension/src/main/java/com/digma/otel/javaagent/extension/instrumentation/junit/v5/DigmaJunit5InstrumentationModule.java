package com.digma.otel.javaagent.extension.instrumentation.junit.v5;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.Collections;
import java.util.List;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;

@AutoService(InstrumentationModule.class)
public class DigmaJunit5InstrumentationModule extends InstrumentationModule {

    public DigmaJunit5InstrumentationModule() {
        super("digma-junit-5.0");
    }

    @Override
    public int order() {
        return 111; // should be triggered after original instrumentations
    }

    // require junit 5
    @Override
    public ElementMatcher.Junction<ClassLoader> classLoaderMatcher() {
        return hasClassesNamed("org.junit.jupiter.api.Test");
    }

    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        return Collections.singletonList(new Junit5AnnotationsInstrumentation());
    }

    @Override
    public boolean isHelperClass(String className) {
        return className.startsWith("com.digma.otel.javaagent.extension.instrumentation.junit");
    }
}
