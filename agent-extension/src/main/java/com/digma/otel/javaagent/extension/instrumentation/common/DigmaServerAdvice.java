package com.digma.otel.javaagent.extension.instrumentation.common;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.LocalRootSpan;
import io.opentelemetry.instrumentation.api.internal.HttpRouteState;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

import static io.opentelemetry.api.common.AttributeKey.stringKey;

public class DigmaServerAdvice {

    //Note when declaring static variables here like a logger the advice doesn't run, strange bytebuddy issue

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
        HttpRouteState routeStateNew = HttpRouteState.fromContextOrNull(Context.current());
        Span routeSpan = null;

        if (routeStateNew != null) {

            try {
                routeSpan = routeStateNew.getSpan();
                if (routeSpan != null) {

                    routeSpan.setAttribute(stringKey("code.namespace"), targetClassName);
                    routeSpan.setAttribute(stringKey("code.function"), method.getName());
                }
            } catch (Error e) {

                //do nothing
            }
        }

        //Fallback to previous behavior
        else {

            Span rootSpan = LocalRootSpan.current();
            if (rootSpan != null) {
                // System.out.println("DBG: setting local root span ");

                rootSpan.setAttribute(stringKey("code.namespace"), targetClassName);
                rootSpan.setAttribute(stringKey("code.function"), method.getName());
            }
        }

        Span span = Span.fromContextOrNull(Context.current());
        if (span != null) {

            span.setAttribute(stringKey("code.namespace"), targetClassName);
            span.setAttribute(stringKey("code.function"), method.getName());
        }


    }



}
