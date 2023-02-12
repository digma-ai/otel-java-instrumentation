package com.digma.otel.javaagent.extension.instrumentation.spring.kafka.v2_7;

import com.digma.otel.javaagent.extension.instrumentation.common.DigmaCurrentSpanAdvice;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.extendsClass;
import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasClassesNamed;
import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.hasSuperMethod;
import static net.bytebuddy.matcher.ElementMatchers.declaresMethod;
import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.namedOneOf;

public class KafkaListenerAnnotationsInstrumentation implements TypeInstrumentation {

    @Override
    public ElementMatcher<ClassLoader> classLoaderOptimization() {
        return hasClassesNamed("org.springframework.kafka.annotation.KafkaListener");
    }

    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        return declaresMethod(isAnnotatedWith(named("org.springframework.kafka.annotation.KafkaListener")))
            .or(
                extendsClass(
                    declaresMethod(isAnnotatedWith(named("org.springframework.kafka.annotation.KafkaListener")))));
    }

    @Override
    public void transform(TypeTransformer transformer) {
        transformer.applyAdviceToMethod(
            isMethod()
                .and(
                    hasSuperMethod(
                        isAnnotatedWith(
                            namedOneOf(
                                "org.springframework.kafka.annotation.KafkaListener"
                            )))),
            DigmaCurrentSpanAdvice.class.getName());
    }
}
