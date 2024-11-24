package com.digma.otel.javaagent.extension.smoketest;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class AllJdksParameterizedTest extends IntegrationTest {


    //todo: add jdk 21 ,23 - images do not exist yet for these jdks
    //Idea complains not finding this method, but it compiles and runs.
    //see https://youtrack.jetbrains.com/issue/IDEA-349571/Idea-complains-about-local-annotation-with-ParameterizedTest-and-MethodSource-but-it-runs-with-gradle-and-from-run-configuration
    protected static Stream<Arguments> allJdks() {
        return IntStream.of(8, 11, 17).mapToObj(Arguments::of);
    }

    @ParameterizedTest(name = "jdk {0}")
    @MethodSource("allJdks")
    @Retention(RetentionPolicy.RUNTIME)
    protected @interface TestAllJdks {
    }


}
