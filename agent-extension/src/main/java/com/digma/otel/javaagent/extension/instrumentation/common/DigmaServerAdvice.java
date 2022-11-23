package com.digma.otel.javaagent.extension.instrumentation.common;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.api.instrumenter.LocalRootSpan;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

/**
 * should be used as an interceptor (advise) for server side endpoints.
 */
public class DigmaServerAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void methodEnter(
        @Advice.This Object target,
        @Advice.Origin Method method,
        @Advice.Origin String methodFqn) {

        Class<?> classOfTarget = target.getClass();
        // taking local root span (servlet of tomcat or jetty) and set the code attributes on it
        Span localRootSpan = LocalRootSpan.current();

        localRootSpan.setAttribute(SemanticAttributes.CODE_NAMESPACE, classOfTarget.getName());
        localRootSpan.setAttribute(SemanticAttributes.CODE_FUNCTION, method.getName());
    }

}
