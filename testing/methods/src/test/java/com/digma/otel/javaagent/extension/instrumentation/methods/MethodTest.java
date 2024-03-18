/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.digma.otel.javaagent.extension.instrumentation.methods;

import com.digma.otel.javaagent.extension.instrumentation.methods.test.ConfigTracedCallable;
import com.digma.otel.javaagent.extension.version.DigmaExtensionVersion;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.testing.junit.AgentInstrumentationExtension;
import io.opentelemetry.instrumentation.testing.junit.InstrumentationExtension;
import io.opentelemetry.sdk.common.InstrumentationScopeInfo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.opentelemetry.sdk.testing.assertj.OpenTelemetryAssertions.equalTo;
import static io.opentelemetry.semconv.SemanticAttributes.CODE_FUNCTION;
import static io.opentelemetry.semconv.SemanticAttributes.CODE_NAMESPACE;

//@Disabled //todo: not working, need to check
class MethodTest {


    @RegisterExtension
    static final InstrumentationExtension testing = AgentInstrumentationExtension.create();

    @Test
    void methodTraced() {

        InstrumentationScopeInfo expectedScopeInfo = InstrumentationScopeInfo.builder(MethodSingletons.INSTRUMENTATION_NAME)
                .setVersion(DigmaExtensionVersion.VERSION)
                        .build();

        Assertions.assertThat(new ConfigTracedCallable().call()).isEqualTo("Hello!");
        testing.waitAndAssertTraces(
                trace ->
                        trace.hasSpansSatisfyingExactly(
                                span ->
                                        span.hasName("ConfigTracedCallable.call")
                                                .hasInstrumentationScopeInfo(expectedScopeInfo)
                                                .hasKind(SpanKind.INTERNAL)
                                                .hasAttributesSatisfyingExactly(
                                                        equalTo(CODE_NAMESPACE, ConfigTracedCallable.class.getName()),
                                                        equalTo(CODE_FUNCTION, "call"))));
    }


    @Test
    void methodTracedWithAsyncStop() throws Exception {
//        ConfigTracedCompletableFuture traced = new ConfigTracedCompletableFuture();
//        CompletableFuture<String> future = traced.getResult();
//
//        // span is ended when CompletableFuture is completed
//        // verify that span has not been ended yet
//        Assertions.assertThat(traced.span).isNotNull().satisfies(span -> Assertions.assertThat(span.isRecording()).isTrue());
//
//        traced.countDownLatch.countDown();
//        Assertions.assertThat(future.get(10, TimeUnit.SECONDS)).isEqualTo("Hello!");
//
//        testing.waitAndAssertTraces(
//                trace ->
//                        trace.hasSpansSatisfyingExactly(
//                                span ->
//                                        span.hasName("ConfigTracedCompletableFuture.getResult")
//                                                .hasKind(SpanKind.INTERNAL)
//                                                .hasAttributesSatisfyingExactly(
//                                                        equalTo(CODE_NAMESPACE, ConfigTracedCompletableFuture.class.getName()),
//                                                        equalTo(CODE_FUNCTION, "getResult"))));
    }

}
