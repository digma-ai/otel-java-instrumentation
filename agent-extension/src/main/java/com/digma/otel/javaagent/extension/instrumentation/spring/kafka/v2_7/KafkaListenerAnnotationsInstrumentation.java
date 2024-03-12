package com.digma.otel.javaagent.extension.instrumentation.spring.kafka.v2_7;

import com.digma.otel.javaagent.extension.instrumentation.common.DigmaCurrentSpanAdvice;
import com.digma.otel.javaagent.extension.instrumentation.common.DigmaTypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static io.opentelemetry.javaagent.extension.matcher.AgentElementMatchers.*;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class KafkaListenerAnnotationsInstrumentation extends DigmaTypeInstrumentation {

    @Override
    public ElementMatcher<ClassLoader> classLoaderOptimization() {
        return hasClassesNamed("org.springframework.kafka.annotation.KafkaListener");
    }

    @Override
    public ElementMatcher<TypeDescription> digmaTypeMatcher() {
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
