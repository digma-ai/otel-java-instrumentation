package com.digma.otel.javaagent.instrumentation.spring.webmvc;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;

import java.util.Collections;
import java.util.List;

@AutoService(InstrumentationModule.class)
public class DigmaSpringWebMvcInstrumentationModule extends InstrumentationModule {

    public DigmaSpringWebMvcInstrumentationModule() {
        super("digma-spring-webmvc", "digma-spring-webmvc-3.1");
    }

    @Override
    public int order() {
        return 111; // should come after original instrumentations
    }

    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        return Collections.singletonList(new ControllerAnnotationsInstrumentation());
    }
}
