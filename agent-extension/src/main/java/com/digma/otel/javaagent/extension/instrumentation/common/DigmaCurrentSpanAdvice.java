package com.digma.otel.javaagent.extension.instrumentation.common;

import io.opentelemetry.api.trace.Span;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

import static io.opentelemetry.api.common.AttributeKey.stringKey;

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

        currentSpan.setAttribute(stringKey("code.namespace"), classOfTarget.getName());
        currentSpan.setAttribute(stringKey("code.function"), method.getName());
    }

}
