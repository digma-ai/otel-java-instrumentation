package com.digma.otel.javaagent.extension;

import com.digma.otel.instrumentation.common.CommonUtils;
import com.digma.otel.instrumentation.common.DigmaCommon;
import com.digma.otel.instrumentation.common.DigmaSemanticAttributes;
import com.google.auto.service.AutoService;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ResourceProvider;
import io.opentelemetry.sdk.resources.Resource;

@AutoService(ResourceProvider.class)
public class DigmaAutoResourceProvider implements ResourceProvider {

    @Override
    public Resource createResource(ConfigProperties config) {
        AttributesBuilder attributesBuilder = Attributes.builder();

        String envVal = DigmaCommon.evaluateEnvironment();
        attributesBuilder.put(DigmaSemanticAttributes.DIGMA_ENVIRONMENT, envVal);

        String codePackagePrefixes = DigmaCommon.evaluateCodePackagePrefixes();
        if (CommonUtils.hasText(codePackagePrefixes)) {
            attributesBuilder.put(DigmaSemanticAttributes.DIGMA_CODE_PACKAGE_PREFIXES, codePackagePrefixes.trim());
        }

        Attributes attributes = attributesBuilder.build();
        return Resource.create(attributes);
    }
}
