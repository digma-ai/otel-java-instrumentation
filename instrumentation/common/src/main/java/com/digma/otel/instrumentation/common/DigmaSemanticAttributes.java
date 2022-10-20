package com.digma.otel.instrumentation.common;

import io.opentelemetry.api.common.AttributeKey;

import static io.opentelemetry.api.common.AttributeKey.stringKey;

public final class DigmaSemanticAttributes {
    private DigmaSemanticAttributes() {
    }

    public static final AttributeKey<String> DIGMA_ENVIRONMENT = stringKey(DigmaSemanticConventions.DIGMA_ENVIRONMENT);

}
