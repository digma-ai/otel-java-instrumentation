package com.digma.otel.javaagent.extension.instrumentation.grpc.v1_6;

import com.digma.otel.instrumentation.grpc.v1_6.DigmaTracingServerInterceptor;
import io.grpc.ServerBuilder;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.extendsClass;
import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

/**
 * DigmaGrpcServerBuilderInstrumentation.
 * Similar to io.opentelemetry.javaagent.instrumentation.grpc.v1_6.GrpcServerBuilderInstrumentation
 */
public class DigmaGrpcServerBuilderInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<ClassLoader> classLoaderOptimization() {
        return hasClassesNamed("io.grpc.ServerBuilder");
    }

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return extendsClass(named("io.grpc.ServerBuilder"));
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
            isMethod().and(isPublic()).and(named("build")).and(takesArguments(0)),
            DigmaGrpcServerBuilderInstrumentation.class.getName() + "$DigmaBuildAdvice");
    }

    @SuppressWarnings("unused")
    public static class DigmaBuildAdvice {

        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static void onEnter(
            @Advice.This ServerBuilder<?> serverBuilder) {

            serverBuilder.intercept(DigmaTracingServerInterceptor.create());
        }
    }
}
