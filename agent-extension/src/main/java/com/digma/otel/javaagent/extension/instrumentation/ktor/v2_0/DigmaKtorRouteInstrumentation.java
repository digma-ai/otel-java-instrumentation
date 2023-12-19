package com.digma.otel.javaagent.extension.instrumentation.ktor.v2_0;

import io.ktor.server.application.ApplicationCall;
import io.ktor.util.pipeline.PipelineContext;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;
import static net.bytebuddy.matcher.ElementMatchers.any;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;

/**
 * DigmaKtorRouteInstrumentation.
 * Similar to io.opentelemetry.javaagent.instrumentation.grpc.v1_6.GrpcServerBuilderInstrumentation
 */
public class DigmaKtorRouteInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<ClassLoader> classLoaderOptimization() {
        return hasClassesNamed("io.ktor.server.routing.Route");
    }

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return any();
//        return extendsClass(named("io.ktor.server.routing.Route"));
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
                takesArgument(0, ElementMatchers.named("io.ktor.util.pipeline.PipelineContext")
                ),
//                isMethod()
//                        .and(
//                                namedOneOf("get", "postNew")
//                        ),
                DigmaKtorRouteInstrumentation.class.getName() + "$RouteMethodAdvice"
        );
    }

    @SuppressWarnings("unused")
    public static class RouteMethodAdvice {

        @Advice.OnMethodEnter(suppress = Throwable.class)
        public static void onEnter(
                @Advice.Argument(value = 0) PipelineContext<?, ApplicationCall> pipelineContext,
                @Advice.This Object target,
                @Advice.Origin Method method,
                @Advice.Origin String methodFqn) {

            Class<?> classOfTarget = target.getClass();
            String classNameOfTarget = classOfTarget.getName();
            if (classNameOfTarget.startsWith("io.ktor.server")) {
                // skip KTOR pipelines
                return;
            }

            System.out.println("DBG: RouteMethodAdvice.OnMethodEnter methodFqn=" + methodFqn);
            System.out.println("DBG: RouteMethodAdvice.OnMethodEnter classOfTarget=" + classNameOfTarget
                    + " methodName=" + method.getName());
            System.out.println("DBG: RouteMethodAdvice.OnMethodEnter pipelineContext=" + pipelineContext);
        }
    }
}
