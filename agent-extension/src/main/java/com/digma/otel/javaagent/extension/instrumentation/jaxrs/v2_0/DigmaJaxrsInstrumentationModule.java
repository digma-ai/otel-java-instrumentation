package com.digma.otel.javaagent.extension.instrumentation.jaxrs.v2_0;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.Collections;
import java.util.List;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;

@AutoService(InstrumentationModule.class)
public class DigmaJaxrsInstrumentationModule extends InstrumentationModule {

    public DigmaJaxrsInstrumentationModule() {
        super("digma-jaxrs-2.0");
    }

    @Override
    public int order() {
        return 111; // should be triggered after original instrumentations
    }

    // require jax-rs 2
    @Override
    public ElementMatcher.Junction<ClassLoader> classLoaderMatcher() {
        return hasClassesNamed("javax.ws.rs.container.AsyncResponse");
    }

    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        return Collections.singletonList(new JaxrsAnnotationsInstrumentation());
    }
}
