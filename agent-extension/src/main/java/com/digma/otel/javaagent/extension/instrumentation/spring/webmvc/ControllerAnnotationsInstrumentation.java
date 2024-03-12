package com.digma.otel.javaagent.extension.instrumentation.spring.webmvc;

import com.digma.otel.javaagent.extension.instrumentation.common.DigmaServerAdvice;
import com.digma.otel.javaagent.extension.instrumentation.common.DigmaTypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class ControllerAnnotationsInstrumentation extends DigmaTypeInstrumentation {

    @Override
    public ElementMatcher<ClassLoader> classLoaderOptimization() {
        return hasClassesNamed("org.springframework.stereotype.Controller");
    }

    @Override
    public ElementMatcher<TypeDescription> digmaTypeMatcher() {
        return inheritsAnnotation(named("org.springframework.stereotype.Controller"));
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
            isMethod().and(
                isAnnotatedWith(namedOneOf(
                    "org.springframework.web.bind.annotation.RequestMapping",
                    "org.springframework.web.bind.annotation.GetMapping",
                    "org.springframework.web.bind.annotation.PostMapping",
                    "org.springframework.web.bind.annotation.DeleteMapping",
                    "org.springframework.web.bind.annotation.PutMapping",
                    "org.springframework.web.bind.annotation.PatchMapping"
                ))),
            DigmaServerAdvice.class.getName());
    }
}
