package com.digma.otel.javaagent.extension.instrumentation.junit;

import com.digma.otel.instrumentation.common.DigmaSemanticAttributes;
import com.digma.otel.instrumentation.common.DigmaSemanticConventions;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

import static com.digma.otel.javaagent.extension.instrumentation.junit.JunitSingletons.instrumenter;

/**
 * took idea from OTEL implementation of WithSpan.
 *
 * see https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/release/v1.32.x/instrumentation/opentelemetry-extension-annotations-1.0/javaagent/src/main/java/io/opentelemetry/javaagent/instrumentation/extensionannotations/WithSpanInstrumentation.java
 */
public class JunitTestAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void methodEnter(
            @Advice.Origin String methodFqn,
            @Advice.Origin Method originMethod,
            @Advice.Local("otelMethod") Method method,
            @Advice.Local("otelContext") Context context,
            @Advice.Local("otelScope") Scope scope) {

        // Every usage of @Advice.Origin Method is replaced with a call to Class.getMethod, copy it
        // to local variable so that there would be only one call to Class.getMethod.
        method = originMethod;

//        System.out.println("DBG: JunitTestAdvice.OnMethodEnter methodFqn=" + methodFqn);

        Instrumenter<Method, Object> instrumenter = instrumenter();
        Context current = Java8BytecodeBridge.currentContext();

        if (instrumenter.shouldStart(current, method)) {
            context = instrumenter.start(current, method);
            scope = context.makeCurrent();
        }
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
    public static void methodExit(
            @Advice.Local("otelMethod") Method method,
            @Advice.Local("otelContext") Context context,
            @Advice.Local("otelScope") Scope scope,
            @Advice.Thrown Throwable throwable) {

        String testingResultValue = DigmaSemanticConventions.TestingResultValues.SUCCESS;
        Throwable throwableToRecord = null;
        if (throwable != null) {
            System.out.println("throwable.getClass()=" +throwable.getClass().getName());
            if (throwable.getClass().isAssignableFrom(AssertionError.class)) {
                testingResultValue = DigmaSemanticConventions.TestingResultValues.FAIL;
            } else {
                testingResultValue = DigmaSemanticConventions.TestingResultValues.ERROR;
                throwableToRecord = throwable;
            }
        }
        Span.current().setAttribute(DigmaSemanticAttributes.TESTING_RESULT, testingResultValue);

        if (scope == null) {
            return;
        }
        scope.close();

        instrumenter().end(context, method, null, throwableToRecord);
    }

}
