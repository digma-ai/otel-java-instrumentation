package com.digma.otel.javaagent.extension.instrumentation.junit;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.code.CodeAttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.util.SpanNames;

import java.lang.reflect.Method;
import java.util.logging.Logger;

public final class JunitSingletons {

    private static final String INSTRUMENTATION_NAME = "com.digma.junit";

    private static final Logger logger = Logger.getLogger(JunitSingletons.class.getName());
    private static final Instrumenter<Method, Object> INSTRUMENTER = createInstrumenter();

    public static Instrumenter<Method, Object> instrumenter() {
        System.out.println("DBG: JunitSingletons.get.instrumenter");
        return INSTRUMENTER;
    }

    private static Instrumenter<Method, Object> createInstrumenter() {
        System.out.println("DBG: JunitSingletons.createInstrumenter");
        return Instrumenter.builder(
                        GlobalOpenTelemetry.get(),
                        INSTRUMENTATION_NAME,
                        JunitSingletons::spanNameFromMethod)
                .addAttributesExtractor(CodeAttributesExtractor.create(MethodCodeAttributesGetter.INSTANCE))
                .buildInstrumenter(JunitSingletons::spanKindFromMethod);
    }

    private static SpanKind spanKindFromMethod(Method method) {
        return SpanKind.INTERNAL;
    }

    private static String spanNameFromMethod(Method method) {
        String spanName = SpanNames.fromMethod(method);
        return spanName;
    }

    private JunitSingletons() {
    }
}
