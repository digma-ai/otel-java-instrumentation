package com.digma.otel.javaagent.extension.instrumentation.common;

import com.digma.otel.instrumentation.common.DigmaSemanticAttributes;
import io.opentelemetry.api.trace.Span;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

/**
 * should be used as an interceptor (advise) for adding code.object and code.namespace on Current Span.
 */
public class DigmaCurrentSpanAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void methodEnter(
        @Advice.This Object target,
        @Advice.Origin Method method,
        @Advice.Origin String methodFqn) {

        Class<?> classOfTarget = target.getClass();

        Span currentSpan = Span.current();

        currentSpan.setAttribute(DigmaSemanticAttributes.CODE_NAMESPACE, classOfTarget.getName());
        currentSpan.setAttribute(DigmaSemanticAttributes.CODE_FUNCTION, method.getName());
    }

}
