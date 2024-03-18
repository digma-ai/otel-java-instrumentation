
package com.digma.otel.javaagent.extension.instrumentation.methods;

import com.digma.otel.javaagent.extension.instrumentation.methods.test.*;
import com.digma.otel.javaagent.extension.instrumentation.methods.test2.MyClassInOtherPackage;
import com.digma.otel.javaagent.extension.version.DigmaExtensionVersion;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.testing.junit.AgentInstrumentationExtension;
import io.opentelemetry.instrumentation.testing.junit.InstrumentationExtension;
import io.opentelemetry.sdk.common.InstrumentationScopeInfo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static io.opentelemetry.sdk.testing.assertj.OpenTelemetryAssertions.equalTo;
import static io.opentelemetry.semconv.SemanticAttributes.CODE_FUNCTION;
import static io.opentelemetry.semconv.SemanticAttributes.CODE_NAMESPACE;

class MethodsInstrumentationTests {


    @RegisterExtension
    static final InstrumentationExtension testing = AgentInstrumentationExtension.create();

    @Test
    void methodTraced() {

        //this should not be instrumented
        new MyClassInOtherPackage().test();

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

        Assertions.assertThat(testing.spans()).size().isEqualTo(1);
        testing.spans().forEach(spanData -> Assertions.assertThat(spanData.getInstrumentationScopeInfo().getName()).isEqualTo("digma.io.opentelemetry.methods"));

    }


    @Test
    void methodTracedWithAsyncStop() throws Exception {

        //this should not be instrumented
        new MyClassInOtherPackage().test();

        String result = "Hello";
        ConfigTracedCompletableFuture traced = new ConfigTracedCompletableFuture();
        CompletableFuture<String> future = traced.getResult(result);

        // span is ended when CompletableFuture is completed
        // verify that span has not been ended yet
        Assertions.assertThat(traced.span).isNotNull().satisfies(span -> Assertions.assertThat(span.isRecording()).isTrue());

        traced.countDownLatch.countDown();
        Assertions.assertThat(future.get(10, TimeUnit.SECONDS)).isEqualTo(result);

        testing.waitAndAssertTraces(
                trace ->
                        trace.hasSpansSatisfyingExactly(
                                span ->
                                        span.hasName("ConfigTracedCompletableFuture.getResult")
                                                .hasKind(SpanKind.INTERNAL)
                                                .hasAttributesSatisfyingExactly(
                                                        equalTo(CODE_NAMESPACE, ConfigTracedCompletableFuture.class.getName()),
                                                        equalTo(CODE_FUNCTION, "getResult"))));

        Assertions.assertThat(testing.spans()).size().isEqualTo(1);
        testing.spans().forEach(spanData -> Assertions.assertThat(spanData.getInstrumentationScopeInfo().getName()).isEqualTo("digma.io.opentelemetry.methods"));
    }


    @Test
    void methodTracedAnonymousAndLambda() {

        //this should not be instrumented
        new MyClassInOtherPackage().test();

        //test that anonymous and classes and lambdas are not instrumented
        AnonymousTestClass anonymousTestClass = new AnonymousTestClass();
        anonymousTestClass.methodWithAnonymousClass();
        anonymousTestClass.methodWithLambda();
        testing.waitForTraces(2); //should have only 2 traces
        testing.waitAndAssertTraces(
                trace -> trace.hasSpansSatisfyingExactly(
                        span ->
                                span.hasName("AnonymousTestClass.methodWithAnonymousClass")
                                        .hasKind(SpanKind.INTERNAL)
                                        .hasAttributesSatisfyingExactly(
                                                equalTo(CODE_NAMESPACE, AnonymousTestClass.class.getName()),
                                                equalTo(CODE_FUNCTION, "methodWithAnonymousClass"))),
                trace -> trace.hasSpansSatisfyingExactly(
                        span ->
                                span.hasName("AnonymousTestClass.methodWithLambda")
                                        .hasKind(SpanKind.INTERNAL)
                                        .hasAttributesSatisfyingExactly(
                                                equalTo(CODE_NAMESPACE, AnonymousTestClass.class.getName()),
                                                equalTo(CODE_FUNCTION, "methodWithLambda"))));


        Assertions.assertThat(testing.spans()).size().isEqualTo(2);
        testing.spans().forEach(spanData -> Assertions.assertThat(spanData.getInstrumentationScopeInfo().getName()).isEqualTo("digma.io.opentelemetry.methods"));
    }


    @Test
    void methodDontInstrumentGetterSetter() {

        //this should not be instrumented
        new MyClassInOtherPackage().test();

        //test that setter and getter are not instrumented
        MyJavaBean myJavaBean = new MyJavaBean();
        myJavaBean.setMyProp("test");
        String p = myJavaBean.getMyProp();
        Assertions.assertThat(testing.spans()).size().isEqualTo(0);
    }

    @Test
    void methodWithWithSpanAnnotation() {

        //this that methods with @WithSpan are not instrumented by us
        new MyClassWIthSpan().myTestWithSpan();

        testing.waitAndAssertTraces(
                trace ->
                        trace.hasSpansSatisfyingExactly(
                                span -> span.hasName("MyClassWIthSpan.myTestWithSpan")
                                        .hasKind(SpanKind.INTERNAL)
                                        .hasAttributesSatisfyingExactly(
                                                equalTo(CODE_NAMESPACE, MyClassWIthSpan.class.getName()),
                                                equalTo(CODE_FUNCTION, "myTestWithSpan"))));

        Assertions.assertThat(testing.spans()).size().isEqualTo(1);
        //test its not our span but otel @WithSpan span
        Assertions.assertThat(testing.spans().get(0).getInstrumentationScopeInfo().getName()).startsWith("io.opentelemetry.opentelemetry-instrumentation-annotations");

    }

}
