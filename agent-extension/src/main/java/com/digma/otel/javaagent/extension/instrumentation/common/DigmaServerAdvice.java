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

        System.out.println("DBG: DigmaServerAdvice.methodEnter " + method.getName());

        Class<?> classOfTarget = target.getClass();
        // taking local root span (servlet of tomcat or jetty) and set the code attributes on it
        Span span = Span.fromContextOrNull(Context.current());
        Span rootSpan = LocalRootSpan.current();

        HttpRouteState routeStateNew = HttpRouteState.fromContextOrNull(Context.current());
        Method[] methods = routeStateNew.getClass().getMethods();
        for (int i=0; i< methods.length;i++){
            System.out.println("DBG:" + methods[i].getName());

            if (methods[i].getName().startsWith("getSpan")){
                try {
                    Span routeSpan = (Span) methods[i].invoke(routeStateNew);
                    System.out.println("DBG: Got span");

                    routeSpan.setAttribute(stringKey("code.namespace"), classOfTarget.getName());
                    routeSpan.setAttribute(stringKey("code.function"), Context.current().toString());

                }
                catch (Exception e){

                }
            }
        }
//        if (routeStateNew!=null){
//            System.out.println("DBG: Got route state");
//
//            Span routeSpan = routeStateNew.getSpan();
//            if (span!=null){
//                System.out.println("DBG: Got route state span");
//
//                routeSpan.setAttribute(stringKey("code.namespace"), classOfTarget.getName());
//                routeSpan.setAttribute(stringKey("code.function"), Context.current().toString());
//
//            }
//        }else{
//            System.out.println("Can't find http context " + Context.current().toString());
//
//        }

        if (span!=null){
            span.setAttribute(stringKey("code.namespace"), classOfTarget.getName());
            span.setAttribute(stringKey("code.function"), method.getName());
        }

        if (rootSpan!=null){
            rootSpan.setAttribute(stringKey("code.namespace"), classOfTarget.getName());
            rootSpan.setAttribute(stringKey("code.function"), method.getName());
        }



    }



}
