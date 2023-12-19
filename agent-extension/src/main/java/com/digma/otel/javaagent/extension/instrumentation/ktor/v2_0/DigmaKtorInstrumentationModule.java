package com.digma.otel.javaagent.extension.instrumentation.ktor.v2_0;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.Collections;
import java.util.List;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;

/**
 * DigmaGrpcInstrumentationModule.
 */
@AutoService(InstrumentationModule.class)
public class DigmaKtorInstrumentationModule extends InstrumentationModule {

    public DigmaKtorInstrumentationModule() {
        super("digma-ktor", "digma-ktor-2.0");
    }

    @Override
    public ElementMatcher.Junction<ClassLoader> classLoaderMatcher() {
        return hasClassesNamed("io.ktor.server.routing.Route");
    }

    @Override
    public int order() {
        return 111; // should be called AFTER the OTEL Kotlin instrumentations
    }

    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        return Collections.singletonList(new DigmaKtorRouteInstrumentation());
    }

}
