package com.digma.otel.javaagent.extension.instrumentation.junit.v5;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

import static com.digma.otel.javaagent.extension.instrumentation.junit.v5.JunitSingletons.instrumenter;


public class JunitTestAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void onEnter(
            @Advice.Origin String methodFqn,
            @Advice.Origin Method originMethod,
            @Advice.Local("otelMethod") Method method,
            @Advice.Local("otelContext") Context context,
            @Advice.Local("otelScope") Scope scope) {

        System.out.println("DBG: JunitTestAdvice.OnMethodEnter methodFqn=" + methodFqn);

        // Every usage of @Advice.Origin Method is replaced with a call to Class.getMethod, copy it
        // to local variable so that there would be only one call to Class.getMethod.
        method = originMethod;

        Instrumenter<Method, Object> instrumenter = instrumenter();
        Context current = Java8BytecodeBridge.currentContext();

        if (instrumenter.shouldStart(current, method)) {
            context = instrumenter.start(current, method);
            scope = context.makeCurrent();
        }
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
    public static void stopSpan(
            @Advice.Local("otelMethod") Method method,
            @Advice.Local("otelContext") Context context,
            @Advice.Local("otelScope") Scope scope,
            @Advice.Thrown Throwable throwable) {
        if (scope == null) {
            return;
        }
        scope.close();

        if (throwable != null) {
            instrumenter().end(context, method, null, throwable);
        }
    }

}
