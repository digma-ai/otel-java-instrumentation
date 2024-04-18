package com.digma.otel.javaagent.extension.instrumentation.methods;

import com.digma.otel.instrumentation.common.DigmaSemanticAttributes;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.code.CodeAttributesGetter;
import io.opentelemetry.instrumentation.api.internal.AttributesExtractorUtil;


import javax.annotation.Nullable;

public final class PackageExtractor<REQUEST, RESPONSE> implements AttributesExtractor<REQUEST, RESPONSE>
{
    private final CodeAttributesGetter<REQUEST> getter;

    private PackageExtractor(CodeAttributesGetter<REQUEST> getter) {
        this.getter = getter;
    }

    public static <REQUEST, RESPONSE> PackageExtractor<REQUEST, RESPONSE> create(CodeAttributesGetter<REQUEST> getter) {
        return new PackageExtractor(getter);
    }

    @Override
    public void onStart(AttributesBuilder attributes, Context context, REQUEST request) {
        Class<?> cls = this.getter.codeClass(request);
        if (cls != null) {
            AttributesExtractorUtil.internalSet(attributes, AttributeKey.stringKey("digma.instrumentation.extended.package"), cls.getPackage().getName());
            AttributesExtractorUtil.internalSet(attributes, AttributeKey.stringKey("digma.instrumentation.extended.enabled"), "true");
        }
    }

    @Override
    public void onEnd(AttributesBuilder attributesBuilder, Context context, REQUEST request, @Nullable RESPONSE response, @Nullable Throwable throwable) {
    }
}