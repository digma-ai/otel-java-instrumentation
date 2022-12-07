package com.digma.otel.javaagent.extension.instrumentation.common.tests;

import io.opentelemetry.proto.trace.v1.Span;
import org.assertj.core.api.AbstractStringAssert;

import static com.digma.otel.javaagent.extension.instrumentation.common.tests.TracesLogic.attributeValue;
import static org.assertj.core.api.Assertions.assertThat;

public final class AssertionUtil {

    public static AbstractStringAssert<?> assertThatAttribute(Span span, String attributeName) {
        return assertThat(attributeValue(span, attributeName)).as("Attribute " + attributeName);
    }

}
