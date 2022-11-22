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

import java.util.logging.Logger;

import static java.util.logging.Level.FINE;

@AutoService(ResourceProvider.class)
public class DigmaAutoResourceProvider implements ResourceProvider {

    private static final Logger logger = Logger.getLogger(DigmaAutoResourceProvider.class.getName());

    public DigmaAutoResourceProvider() {
        if (logger.isLoggable(FINE)) {
            logger.log(FINE, "DigmaAutoResourceProvider been constructed");
        }
    }

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
        if (logger.isLoggable(FINE)) {
            logger.log(FINE, "DigmaAutoResourceProvider created resources with attributes: {0}", attributes);
        }
        return Resource.create(attributes);
    }
}
