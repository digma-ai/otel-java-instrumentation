package com.digma.otel.javaagent.extension.instrumentation;

import com.digma.otel.javaagent.extension.instrumentation.common.DigmaServerAdvice;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;
import static net.bytebuddy.matcher.ElementMatchers.inheritsAnnotation;
import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.namedOneOf;

public class ControllerAnnotationsInstrumentation implements TypeInstrumentation {
    @Override
    public ElementMatcher<ClassLoader> classLoaderOptimization() {
        return hasClassesNamed("org.springframework.stereotype.Controller");
    }

    /**
     * typeMatcher.
     * inheritsAnnotation("Controller") should catch and support "org.springframework.web.bind.annotation.RestController" as well
     */
    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return isAnnotatedWith(named("org.springframework.stereotype.Controller"))
            .or(inheritsAnnotation(named("org.springframework.stereotype.Controller")));
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
            DigmaServerAdvice.class.getName());
    }
}
