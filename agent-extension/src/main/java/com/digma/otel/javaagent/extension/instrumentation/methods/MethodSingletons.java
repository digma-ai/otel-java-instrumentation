

package com.digma.otel.javaagent.extension.instrumentation.methods;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.SpanKindExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.code.CodeAttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.code.CodeAttributesGetter;
import io.opentelemetry.instrumentation.api.instrumenter.code.CodeSpanNameExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.util.ClassAndMethod;

public final class MethodSingletons {
  private static final String INSTRUMENTATION_NAME = "digma.io.opentelemetry.methods";

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
            .buildInstrumenter(SpanKindExtractor.alwaysInternal());
  }

  public static Instrumenter<ClassAndMethod, Void> instrumenter() {
    return INSTRUMENTER;
  }

  private MethodSingletons() {}
}
