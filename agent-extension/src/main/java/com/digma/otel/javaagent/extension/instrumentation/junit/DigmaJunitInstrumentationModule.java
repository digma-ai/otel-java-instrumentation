package com.digma.otel.javaagent.extension.instrumentation.junit;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.Collections;
import java.util.List;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;

@AutoService(InstrumentationModule.class)
public class DigmaJunitInstrumentationModule extends InstrumentationModule {

    public DigmaJunitInstrumentationModule() {
        super("digma-junit");
    }

    @Override
    public int order() {
        return 111; // should be triggered after original instrumentations
    }

    @Override
    public ElementMatcher.Junction<ClassLoader> classLoaderMatcher() {
        return hasClassesNamed("org.junit.jupiter.api.Test") // junit 5
                .or(hasClassesNamed("org.junit.Test") // junit 4 and below
                );
    }

    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        return Collections.singletonList(new JunitAnnotationsInstrumentation());
    }

    @Override
    public boolean isHelperClass(String className) {
        return className.startsWith("com.digma.otel.javaagent.extension.instrumentation.junit");
    }
}
