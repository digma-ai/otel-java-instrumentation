package com.digma.otel.javaagent.instrumentation.spring.webmvc;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.api.instrumenter.LocalRootSpan;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.reflect.Method;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;
import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.namedOneOf;

public class ControllerAnnotationsInstrumentation implements TypeInstrumentation {
    @Override
    public ElementMatcher<ClassLoader> classLoaderOptimization() {
        return hasClassesNamed("org.springframework.stereotype.Controller");
    }

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return isAnnotatedWith(named("org.springframework.stereotype.Controller"));
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
                isMethod().and(
                        isAnnotatedWith(namedOneOf(
                                "org.springframework.web.bind.annotation.GetMapping",
                                "org.springframework.web.bind.annotation.PostMapping",
                                "org.springframework.web.bind.annotation.DeleteMapping",
                                "org.springframework.web.bind.annotation.PutMapping",
                                "org.springframework.web.bind.annotation.PatchMapping"
                        ))),
                ControllerAnnotationsInstrumentation.class.getName() + "$ControllerAnnotationsAdvice");
    }

    @SuppressWarnings("unused")
    public static class ControllerAnnotationsAdvice {

        @Advice.OnMethodEnter(
                // suppress = Throwable.class
        )
        public static void methodEnter(
                @Advice.This Object target,
                @Advice.Origin Method method,
                @Advice.Origin String methodFqn) {

            Class<?> classOfTarget = target.getClass();
            // taking local root span (servlet of tomcat or jetty) and set the code attributes on it
            Span localRootSpan = LocalRootSpan.current();

            localRootSpan.setAttribute(SemanticAttributes.CODE_NAMESPACE, classOfTarget.getName());
            localRootSpan.setAttribute(SemanticAttributes.CODE_FUNCTION, method.getName());
        }

        @Advice.OnMethodExit(
                // suppress = Throwable.class
        )
        public static void methodExit() {
        }
    }
}
