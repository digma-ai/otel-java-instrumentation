package com.digma.otel.instrumentation.common;

import io.opentelemetry.api.common.AttributeKey;

import static io.opentelemetry.api.common.AttributeKey.stringKey;

public final class DigmaSemanticAttributes {
    private DigmaSemanticAttributes() {
    }

    public static final AttributeKey<String> DIGMA_AGENT_VERSION = stringKey("digma.agent.version");
    public static final AttributeKey<String> DIGMA_ENVIRONMENT = stringKey(DigmaSemanticConventions.DIGMA_ENVIRONMENT);
    public static final AttributeKey<String> DIGMA_CODE_PACKAGE_PREFIXES = stringKey(DigmaSemanticConventions.DIGMA_CODE_PACKAGE_PREFIXES);
    public static final AttributeKey<Boolean> IS_TEST = AttributeKey.booleanKey(DigmaSemanticConventions.IS_TEST);
    public static final AttributeKey<String> TESTING_FRAMEWORK = AttributeKey.stringKey(DigmaSemanticConventions.TESTING_FRAMEWORK);
    public static final AttributeKey<String> TESTING_RESULT = AttributeKey.stringKey(DigmaSemanticConventions.TESTING_RESULT);
}
