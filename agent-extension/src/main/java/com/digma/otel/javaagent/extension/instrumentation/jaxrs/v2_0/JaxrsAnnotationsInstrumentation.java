package com.digma.otel.javaagent.extension.instrumentation.jaxrs.v2_0;

import com.digma.otel.javaagent.extension.instrumentation.common.DigmaServerAdvice;
import com.digma.otel.javaagent.extension.instrumentation.common.DigmaTypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasSuperType;
import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.*;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class JaxrsAnnotationsInstrumentation extends DigmaTypeInstrumentation {

    @Override
    public ElementMatcher<ClassLoader> classLoaderOptimization() {
        return hasClassesNamed("javax.ws.rs.Path");
    }

    @Override
    public ElementMatcher<TypeDescription> digmaTypeMatcher() {
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
