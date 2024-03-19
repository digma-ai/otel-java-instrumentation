package com.digma.otel.javaagent.extension.smoketest;

import org.testcontainers.containers.wait.strategy.WaitStrategy;

public class GrpcIntegrationTests extends AllJdksParameterizedTest{

    //todo : complete ,
    // see https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/5df8a5a0a0e90fc7a867f8f013bd050f132a090b/smoke-tests

    @Override
    protected String getTargetImage(int jdk) {
        //todo: find the tag
        return "ghcr.io/open-telemetry/opentelemetry-java-instrumentation/smoke-test-grpc:jdk";
    }

    @Override
    protected WaitStrategy getTargetWaitStrategy() {
        return null;
    }
}
