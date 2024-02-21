package com.digma.otel.javaagent.extension.instrumentation.common;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.LocalRootSpan;
import io.opentelemetry.instrumentation.api.internal.HttpRouteState;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

import static io.opentelemetry.api.common.AttributeKey.stringKey;

public class DigmaServerAdvice {


    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void methodEnter(
        @Advice.This Object target,
        @Advice.Origin Method method,
        @Advice.Origin String methodFqn) {

        //System.out.println("DBG: DigmaServerAdvice.methodEnter " + method.getName());

        Class<?> classOfTarget = target.getClass();
        // taking local root span (servlet of tomcat or jetty) and set the code attributes on it

        //Find http route span and set it
        HttpRouteState routeStateNew = HttpRouteState.fromContextOrNull(Context.current());
        Span routeSpan =null;

        if (routeStateNew!=null){
            try {
                routeSpan = routeStateNew.getSpan();
                if (routeSpan != null) {
                    routeSpan.setAttribute(stringKey("code.namespace"), classOfTarget.getName());
                    routeSpan.setAttribute(stringKey("code.function"), method.getName());
                }
            }
            catch (Exception e){
                //do nothing
            }
        }

        Span span = Span.fromContextOrNull(Context.current());
        if (span!=null){

            if (routeSpan!=null && routeSpan!=span){
                span.setAttribute(stringKey("code.namespace"), classOfTarget.getName());
                span.setAttribute(stringKey("code.function"), method.getName());
            }
        }

//        Span rootSpan = LocalRootSpan.current();
//        if (rootSpan!=null){
//            rootSpan.setAttribute(stringKey("code.namespace"), classOfTarget.getName());
//            rootSpan.setAttribute(stringKey("code.function"), method.getName());
//        }



    }



}
