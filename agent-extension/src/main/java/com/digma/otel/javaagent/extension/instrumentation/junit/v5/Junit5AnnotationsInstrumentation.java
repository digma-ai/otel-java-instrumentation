package com.digma.otel.javaagent.extension.instrumentation.junit.v5;

import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;
import static net.bytebuddy.matcher.ElementMatchers.declaresMethod;
import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.namedOneOf;

public class Junit5AnnotationsInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<ClassLoader> classLoaderOptimization() {
        return hasClassesNamed("org.junit.jupiter.api.Test") // junit 5
                .or(hasClassesNamed("org.junit.Test") // junit 4 and below
                );
    }

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return declaresMethod(isAnnotatedWith(namedOneOf(
                "org.junit.jupiter.api.Test", // junit 5
                "org.junit.Test" // junit 4 and below
        )));
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
                isMethod()
                        .and(
                                isAnnotatedWith(namedOneOf(
                                        "org.junit.jupiter.api.Test", // junit 5
                                        "org.junit.Test" // junit 4 and below
                                ))),
                JunitTestAdvice.class.getName());
    }
}
