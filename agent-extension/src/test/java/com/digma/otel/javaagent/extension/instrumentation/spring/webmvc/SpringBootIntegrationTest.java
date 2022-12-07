package com.digma.otel.javaagent.extension.instrumentation.spring.webmvc;

import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.trace.v1.Span;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import static com.digma.otel.javaagent.extension.instrumentation.common.tests.AssertionUtil.assertThatAttribute;
import static com.digma.otel.javaagent.extension.instrumentation.common.tests.TracesLogic.findRootSpan;
import static org.assertj.core.api.Assertions.assertThat;

class SpringBootIntegrationTest extends IntegrationTest {

    @Override
    protected String getTargetImage(int jdk) {
        return "ghcr.io/open-telemetry/opentelemetry-java-instrumentation/smoke-test-spring-boot:jdk"
            + jdk
            + "-20211213.1570880324";
    }

    @Test
    public void extensionsAreLoadedFromJar() throws IOException, InterruptedException {
        startTarget("/opentelemetry-extensions.jar");

        testAndVerify();

        stopTarget();
    }

    //  @Test
    public void extensionsAreLoadedFromFolder() throws IOException, InterruptedException {
        startTarget("/");

        testAndVerify();

        stopTarget();
    }

    //  @Test
    public void extensionsAreLoadedFromJavaagent() throws IOException, InterruptedException {
        startTargetWithExtendedAgent();

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
        assertThat(response.body().string()).as("response body").isEqualTo("Hi!");

        Collection<ExportTraceServiceRequest> traces = waitForTraces();

        final Span rootSpan = findRootSpan(traces);
        assertThat(rootSpan.getName()).as("root span name").isEqualTo("/greeting");
        assertThatAttribute(rootSpan, "http.route").isEqualTo("/greeting");
    }
}
