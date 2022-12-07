package com.digma.otel.javaagent.extension.instrumentation.common.tests;

import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import io.opentelemetry.proto.common.v1.KeyValue;
import io.opentelemetry.proto.trace.v1.Span;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
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

    @Nullable
    public static String attributeValue(Span span, String attributeName) {
        Optional<KeyValue> attributeOpt = span.getAttributesList().stream()
            .filter(kv -> kv.getKey().equals(attributeName))
            .findFirst();
        if (attributeOpt.isPresent()) {
            return attributeOpt.get().getValue().getStringValue();
        }
        return null;
    }

}