package com.digma.otel.javaagent.extension.instrumentation.jaxrs.v2_0;

import com.digma.otel.javaagent.extension.instrumentation.common.DigmaServerAdvice;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;
import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasSuperMethod;
import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasSuperType;
import static net.bytebuddy.matcher.ElementMatchers.declaresMethod;
import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.namedOneOf;

public class JaxrsAnnotationsInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<ClassLoader> classLoaderOptimization() {
        return hasClassesNamed("javax.ws.rs.Path");
    }

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return hasSuperType(
            isAnnotatedWith(named("javax.ws.rs.Path"))
                .or(declaresMethod(isAnnotatedWith(named("javax.ws.rs.Path")))));
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
            isMethod()
                .and(
                    hasSuperMethod(
                        isAnnotatedWith(namedOneOf(
                            "javax.ws.rs.Path",
                            "javax.ws.rs.DELETE",
                            "javax.ws.rs.GET",
                            "javax.ws.rs.HEAD",
                            "javax.ws.rs.OPTIONS",
                            "javax.ws.rs.PATCH",
                            "javax.ws.rs.POST",
                            "javax.ws.rs.PUT"
                        )))),
            DigmaServerAdvice.class.getName());
    }
}
