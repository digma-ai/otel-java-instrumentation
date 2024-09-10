package com.digma.otel.javaagent.extension.instrumentation.jdbc;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;

import java.util.Collections;
import java.util.List;

@AutoService(InstrumentationModule.class)
public class DigmaJdbcInstrumentationModule extends InstrumentationModule {

    public DigmaJdbcInstrumentationModule() {
        super("digma-jdbc");
    }

    @Override
    public List<TypeInstrumentation> typeInstrumentations() {
        return Collections.singletonList(
                new DigmaPreparedStatementInstrumentation());
    }

    @Override
    public boolean defaultEnabled(ConfigProperties config) {
        //otel.instrumentation.jdbc.statement-sanitizer.enabled should be false for this module to be enabled
        return !config.getBoolean("otel.instrumentation.jdbc.statement-sanitizer.enabled", true);
    }

    @Override
    public int order() {
        return 1000;
    }
}
