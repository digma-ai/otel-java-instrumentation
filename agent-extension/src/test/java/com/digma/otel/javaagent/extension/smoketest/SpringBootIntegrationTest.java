package com.digma.otel.javaagent.extension.smoketest;

import com.digma.otel.javaagent.extension.AgentExtensionVersion;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Assertions;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

class SpringBootIntegrationTest extends AllJdksParameterizedTest {


    @Override
    protected String getTargetImage(int jdk) {
        return "ghcr.io/open-telemetry/opentelemetry-java-instrumentation/smoke-test-spring-boot:jdk"
                + jdk
                + "-20211213.1570880324";
    }

    @Override
    protected WaitStrategy getTargetWaitStrategy() {
        return Wait.forLogMessage(".*Started SpringbootApplication in.*", 1)
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
        String url = String.format("http://localhost:%d/greeting", target.getMappedPort(8080));
        Request request = new Request.Builder().url(url).get().build();

        String currentAgentVersion =
                (String)
                        new JarFile(agentPath)
                                .getManifest()
                                .getMainAttributes()
                                .get(Attributes.Name.IMPLEMENTATION_VERSION);

        Response response = client.newCall(request).execute();

        Collection<ExportTraceServiceRequest> traces = waitForTraces();

        Assertions.assertNotNull(response.body());
        Assertions.assertEquals("Hi!", response.body().string());
        Assertions.assertEquals(1, countSpansByName(traces, "GET /greeting"));
        Assertions.assertEquals(0, countSpansByName(traces, "WebController.greeting"));
        Assertions.assertEquals(1, countSpansByName(traces, "WebController.withSpan"));
        Assertions.assertEquals(1, countSpansByAttributeValue(traces, "code.function", "greeting"));
        Assertions.assertEquals(1, countSpansByAttributeValue(traces, "code.function", "withSpan"));
        Assertions.assertEquals(2, countSpansByAttributeValue(traces, "code.namespace", "io.opentelemetry.smoketest.springboot.controller.WebController"));

        Assertions.assertEquals(2, countResourcesByValue(traces, "digma.agent.version", AgentExtensionVersion.VERSION));
        Assertions.assertNotEquals(
                0, countResourcesByValue(traces, "telemetry.distro.version", currentAgentVersion));
    }
}
