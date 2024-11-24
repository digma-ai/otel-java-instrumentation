

package com.digma.otel.javaagent.extension.instrumentation.methods;

import com.digma.otel.extension.extension.version.BuildVersion;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.incubator.semconv.code.CodeAttributesExtractor;
import io.opentelemetry.instrumentation.api.incubator.semconv.code.CodeAttributesGetter;
import io.opentelemetry.instrumentation.api.incubator.semconv.code.CodeSpanNameExtractor;
import io.opentelemetry.instrumentation.api.incubator.semconv.util.ClassAndMethod;
import io.opentelemetry.instrumentation.api.instrumenter.ErrorCauseExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.SpanKindExtractor;

public final class MethodSingletons {
    public static final String INSTRUMENTATION_NAME = "digma.io.opentelemetry.methods";

    private static final Instrumenter<ClassAndMethod, Void> INSTRUMENTER;

    static {
        CodeAttributesGetter<ClassAndMethod> codeAttributesGetter =
                ClassAndMethod.codeAttributesGetter();

        INSTRUMENTER =
                Instrumenter.<ClassAndMethod, Void>builder(
                                GlobalOpenTelemetry.get(),
                                INSTRUMENTATION_NAME,
                                CodeSpanNameExtractor.create(codeAttributesGetter))
                        .addAttributesExtractor(CodeAttributesExtractor.create(codeAttributesGetter))
                        .setInstrumentationVersion(BuildVersion.getVersion())
                        .setErrorCauseExtractor(ErrorCauseExtractor.getDefault())
                        .addAttributesExtractor(PackageExtractor.create(codeAttributesGetter))
                        .buildInstrumenter(SpanKindExtractor.alwaysInternal());
    }

    public static Instrumenter<ClassAndMethod, Void> instrumenter() {
        return INSTRUMENTER;
    }

    private MethodSingletons() {
    }
}
