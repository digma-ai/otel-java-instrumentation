package com.digma.otel.javaagent.extension.instrumentation.common;

import io.opentelemetry.api.trace.Span;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

import static io.opentelemetry.api.common.AttributeKey.stringKey;

/**
 * should be used as an interceptor (advise) for adding code.object and code.namespace on Current Span.
 */
public class DigmaCurrentSpanAdvice {

    //Note when declaring static variables here like a logger the advice doesn't run, strange bytebuddy issue

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void methodEnter(
        @Advice.This Object target,
        @Advice.Origin Method method,
        @Advice.Origin String methodFqn) {

//        if (target == null) {
//            System.err.println("DBG: DigmaCurrentSpanAdvice.methodEnter (target is null) " + method.getName() + "methodFqn="+methodFqn);
//        }else{
//            System.err.println("DBG: DigmaCurrentSpanAdvice.methodEnter " + target.getClass().getName() + "." + method.getName() + "methodFqn="+methodFqn);
//        }

        String targetClassName = "";
        if (target != null) {
            targetClassName = target.getClass().getName();
        }

        Span currentSpan = Span.current();

        currentSpan.setAttribute(stringKey("code.namespace"), targetClassName);
        currentSpan.setAttribute(stringKey("code.function"), method.getName());
    }

}
