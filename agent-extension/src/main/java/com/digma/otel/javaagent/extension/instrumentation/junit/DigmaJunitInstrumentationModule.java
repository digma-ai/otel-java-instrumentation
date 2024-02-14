package com.digma.otel.javaagent.extension.instrumentation.junit;

import com.google.auto.service.AutoService;
import io.opentelemetry.instrumentation.api.instrumenter.code.CodeAttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.code.CodeAttributesGetter;
import io.opentelemetry.instrumentation.api.instrumenter.util.SpanNames;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.ArrayList;
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
        return className.startsWith("com.digma.otel.javaagent.extension.instrumentation.junit") // catch all classes in this package
                || className.startsWith("com.digma.otel.instrumentation.common") // catch semantic conventions
                ;
    }

    @Override
    public List<String> getAdditionalHelperClassNames() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(CodeAttributesGetter.class.getName());
        list.add(CodeAttributesExtractor.class.getName());
        list.add(SemanticAttributes.class.getName());

        list.add(SpanNames.class.getName());

        return list;
    }
}
