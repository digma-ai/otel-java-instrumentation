package com.digma.otel.javaagent.extension.instrumentation.spring.webmvc;

import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.trace.v1.Span;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class TracesLogic {

    protected static Stream<Span> flattenToSpans(Collection<ExportTraceServiceRequest> traces) {
        return traces.stream()
            .flatMap(trace -> trace.getResourceSpansList().stream())
            .flatMap(resourceSpan -> resourceSpan.getScopeSpansList().stream())
            .flatMap(scopeSpan -> scopeSpan.getSpansList().stream());
    }

    @Nonnull
    public static Span findRootSpan(Collection<ExportTraceServiceRequest> traces) {
        List<Span> spans = flattenToSpans(traces)
            .filter(span -> span.getParentSpanId().isEmpty())
            .collect(Collectors.toList());

        if (spans.size() > 1) {
            System.err.println("too many root spans. traces = " + traces);
            throw new RuntimeException("too many root spans");
        }
        if (spans.size() < 1) {
            System.err.println("no root spans at all. traces = " + traces);
            throw new RuntimeException("no root spans at all");
        }
        return spans.iterator().next();
    }
}