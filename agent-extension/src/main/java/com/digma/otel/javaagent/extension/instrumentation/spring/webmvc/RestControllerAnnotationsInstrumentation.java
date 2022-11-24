package com.digma.otel.javaagent.extension.instrumentation.spring.webmvc;

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

public class RestControllerAnnotationsInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<ClassLoader> classLoaderOptimization() {
        return hasClassesNamed("org.springframework.web.bind.annotation.RestController");
    }

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return inheritsAnnotation(named("org.springframework.web.bind.annotation.RestController"));
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
