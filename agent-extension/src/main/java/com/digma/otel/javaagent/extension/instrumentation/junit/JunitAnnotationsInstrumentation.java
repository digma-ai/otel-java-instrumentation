package com.digma.otel.javaagent.extension.instrumentation.junit;

import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class JunitAnnotationsInstrumentation implements TypeInstrumentation {

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
        )))
                .and(not(isAnnotatedWith(namedOneOf("org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest"))));
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
