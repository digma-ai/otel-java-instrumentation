package com.digma.otel.javaagent.extension.instrumentation.spring.webmvc;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

@AutoService(InstrumentationModule.class)
public class DigmaSpringWebMvcInstrumentationModule extends InstrumentationModule {

    public DigmaSpringWebMvcInstrumentationModule() {
        super("digma-spring-webmvc", "digma-spring-webmvc-3.1");
    }

    @Override
    public int order() {
        return 111; // should be triggered after original instrumentations
    }

    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        return Collections.unmodifiableList(asList(
            new ControllerAnnotationsInstrumentation(),
            new RestControllerAnnotationsInstrumentation()
        ));
    }

    @Override
    public boolean isHelperClass(String className) {
        return className.startsWith("com.digma.otel.javaagent.extension.instrumentation.spring.webmvc") // catch all classes in this package
                || className.startsWith("com.digma.otel.instrumentation.common") // catch semantic conventions
                || className.startsWith("com.digma.otel.javaagent.extension.instrumentation.common");
    }
}
