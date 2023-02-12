package com.digma.otel.javaagent.extension.instrumentation.spring.kafka.v2_7;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;

import java.util.Collections;
import java.util.List;

@AutoService(InstrumentationModule.class)
public class DigmaSpringKafkaInstrumentationModule extends InstrumentationModule {

    public DigmaSpringKafkaInstrumentationModule() {
        super("digma-spring-kafka", "digma-spring-kafka-2.7");
    }

    @Override
    public int order() {
        return 111; // should be triggered after original instrumentations
    }

    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        return Collections.singletonList(new KafkaListenerAnnotationsInstrumentation());
    }
}
