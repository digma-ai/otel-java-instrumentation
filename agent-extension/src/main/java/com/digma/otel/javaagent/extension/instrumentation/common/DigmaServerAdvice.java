package com.digma.otel.javaagent.extension.instrumentation.common;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.api.instrumenter.LocalRootSpan;
import io.opentelemetry.instrumentation.api.internal.HttpRouteState;
import io.opentelemetry.javaagent.bootstrap.Java8BytecodeBridge;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

public class DigmaServerAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void methodEnter(
            @Advice.This Object target,
            @Advice.Origin Method method,
            @Advice.Origin String methodFqn) {

//        if (target == null) {
//            System.err.println("DBG: DigmaServerAdvice.methodEnter (target is null) " + method.getName() + "methodFqn=" + methodFqn);
//        } else {
//            System.err.println("DBG: DigmaServerAdvice.methodEnter " + target.getClass().getName() + "." + method.getName() + "methodFqn=" + methodFqn);
//        }

        String targetClassName = "";
        if (target != null) {
            targetClassName = target.getClass().getName();
        }else{
            targetClassName = method.getDeclaringClass().getName();
        }
        // taking local root span (servlet of tomcat or jetty) and set the code attributes on it

        //Find http route span and set it
        HttpRouteState routeStateNew = HttpRouteState.fromContextOrNull(Java8BytecodeBridge.currentContext());
        Span routeSpan = null;

        if (routeStateNew != null) {
            try {
                routeSpan = routeStateNew.getSpan();
                if (routeSpan != null) {
                    routeSpan.setAttribute("code.namespace", targetClassName);
                    routeSpan.setAttribute("code.function", method.getName());
                }
            } catch (Error e) {
                //ignore
            }
        }

        //Fallback to previous behavior
        else {
            Span rootSpan = LocalRootSpan.current();
            if (rootSpan != null) {
                rootSpan.setAttribute("code.namespace", targetClassName);
                rootSpan.setAttribute("code.function", method.getName());
            }
        }

        Span span = Java8BytecodeBridge.spanFromContext(Java8BytecodeBridge.currentContext());
        if (span != null) {
            span.setAttribute("code.namespace", targetClassName);
            span.setAttribute("code.function", method.getName());
        }
    }
}
