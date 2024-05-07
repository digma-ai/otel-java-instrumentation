package com.digma.otel.javaagent.extension.smoketest;

import com.digma.otel.javaagent.extension.AgentExtensionVersion;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.collector.trace.v1.TraceServiceGrpc;
import org.junit.jupiter.api.Assertions;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

public class GrpcIntegrationTests extends AllJdksParameterizedTest{

    @Override
    protected String getTargetImage(int jdk) {
        return "ghcr.io/open-telemetry/opentelemetry-java-instrumentation/smoke-test-grpc:jdk"
                + jdk
                + "-20230228.4289437270";
    }

    @Override
    protected WaitStrategy getTargetWaitStrategy() {
        return Wait.forLogMessage(".*Server started.*", 1)
                .withStartupTimeout(Duration.ofMinutes(1));
    }

    @TestAllJdks
    public void extensionsAreLoadedFromJar(int jdk) throws IOException, InterruptedException {
        startTarget("/opentelemetry-extensions.jar", jdk);

        testAndVerify();

        stopTarget();
    }


    @TestAllJdks
    public void extensionsAreLoadedFromFolder(int jdk) throws IOException, InterruptedException {
        startTarget("/", jdk);

        testAndVerify();

        stopTarget();
    }

    @TestAllJdks
    public void extensionsAreLoadedFromJavaagent(int jdk) throws IOException, InterruptedException {
        startTargetWithExtendedAgent(jdk);

        testAndVerify();

        stopTarget();
    }




    private void testAndVerify() throws IOException, InterruptedException {

        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost",target.getMappedPort(8080))
                .usePlaintext()
                .build();
        TraceServiceGrpc.TraceServiceBlockingStub stub = TraceServiceGrpc.newBlockingStub(managedChannel);
        stub.export(ExportTraceServiceRequest.getDefaultInstance());

        String currentAgentVersion = (String) new JarFile(agentPath).getManifest().getMainAttributes().get(Attributes.Name.IMPLEMENTATION_VERSION);

        Collection<ExportTraceServiceRequest> traces = waitForTraces();


        Assertions.assertEquals(1, countSpansByName(traces, "opentelemetry.proto.collector.trace.v1.TraceService/Export"));
        Assertions.assertEquals(1, countSpansByName(traces, "TestService.withSpan"));

        Assertions.assertEquals(1, countSpansByAttributeValue(traces, "code.function", "export"));
        Assertions.assertEquals(2, countSpansByAttributeValue(traces, "code.namespace", "io.opentelemetry.smoketest.grpc.TestService"));

        Assertions.assertEquals(2, countResourcesByValue(traces, "digma.agent.version", AgentExtensionVersion.VERSION));
        Assertions.assertNotEquals(
                0, countResourcesByValue(traces, "telemetry.distro.version", currentAgentVersion));
    }
}
