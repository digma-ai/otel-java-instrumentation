package com.digma.otel.javaagent.extension.instrumentation.extendedobservability;

import com.digma.otel.extension.extension.version.BuildVersion;
import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.Collections;
import java.util.List;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;


@AutoService(InstrumentationModule.class)
public class DigmaExtendedObservabilityInstrumentationModule extends InstrumentationModule {

    public static final String DIGMA_MARKER_ANNOTATION_NAME = "org.digma.instrumentation.ExtendedObservability";

    public DigmaExtendedObservabilityInstrumentationModule() {
        super("digma-extended","digma-extended-"+ BuildVersion.getVersion());
    }

    @Override
    public int order() {
        return 10000;
    }

    @Override
    public ElementMatcher.Junction<ClassLoader> classLoaderMatcher() {
        return hasClassesNamed(DIGMA_MARKER_ANNOTATION_NAME);
    }

    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        return Collections.singletonList(new ExtendedObservabilityTypeInstrumentation());
    }

}
