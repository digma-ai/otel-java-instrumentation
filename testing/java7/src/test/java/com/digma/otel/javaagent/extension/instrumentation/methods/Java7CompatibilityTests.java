
package com.digma.otel.javaagent.extension.instrumentation.methods;

import io.opentelemetry.instrumentation.testing.junit.AgentInstrumentationExtension;
import io.opentelemetry.instrumentation.testing.junit.InstrumentationExtension;
import org.digma.otel.test.jaxrs.JaxrsDemo;
import org.digma.otel.test.junit.Junit4DemoTests;
import org.digma.otel.test.junit.Junit5DemoTests;
import org.digma.otel.test.simple.SimpleClass;
import org.digma.otel.test.spring.KafkaDemo;
import org.digma.otel.test.spring.SpringDemo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class Java7CompatibilityTests {


    /*
    The problem:
    our instrumentation injects code into classes.
    these classes may be compiled with java 7,the code that our advices inject must be java 7 compatible.
    after the code is injected the jvm verifies the class byte code and if the injected code is not
    compatible with the java version that compiled the class, a verify error will be thrown by the jvm
    and the instrumentation will fail.
    usually the problem will happen when using otel classes that are java 8 byte code, for example
    Context.current() or Span.currentSpan(), these are static methods on interface that were added in java 8.
    Otel has a bridge class called Java8BytecodeBridge that has the same methods and is java 7 compatible.
    AttributeKey.stringKey() is also not compatible with java 7 but it's not necessary , we can use
    Span.setAttribute(java.lang.String, java.lang.String).
    So we need to use Java8BytecodeBridge whenever possible. there are only a few methods involved.

    This test checks that our instrumentation injects code that is java 7 compatible.
    it uses classes compiled with java 7. these classes have annotations that should trigger our instrumentation,
    we don't need traces, we only need our instrumentation to inject its code and if the code is not valid
    the test will fail on verify error.

    to test that the test really works:
    in DigmaTypeInstrumentation.typeMatcher, make sure it limits to java 7 and above.and not java 8.
    in DigmaServerAdvice change at least in one place Java8BytecodeBridge.currentContext() to Context.current()
    and run the test, it will fail on verify error.

    All this has nothing to do with the running jvm, it may be 8 or above. it only depends on the byte code
    version of the instrumented class.
     */

    //todo: create an instrumentation advice that inject java 8 bytecode and add a test method that should fail.
    // this is a test that the test really works

    //todo: add assertions that instrumentation was triggered

    //todo: currently our advices are matched only for java 8 and above so this test is not really necessary.
    // its too risky to match java 7 because we need to make sure that all our advices are tested.
    // see DigmaTypeInstrumentation

    @RegisterExtension
    static final InstrumentationExtension testing = AgentInstrumentationExtension.create();

    @Test
    void methodsByPackageTest() {
        new SimpleClass().hello();
    }

    @Test
    void springTest() {
        new SpringDemo().hello();
    }

    @Test
    void kafkaTest() {
        new KafkaDemo().myListener();
    }

    @Test
    void jaxrsTest() {
        new JaxrsDemo().hello();
    }

    @Test
    void junit4Test() {
        new Junit4DemoTests().test();
    }

    @Test
    void junit5Test() {
        new Junit5DemoTests().test();
    }

}
