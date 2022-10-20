package com.digma.otel.instrumentation.spring.autoconfigure.resources;

import com.digma.otel.instrumentation.common.DigmaCommon;
import com.digma.otel.instrumentation.common.DigmaSemanticAttributes;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ResourceProvider;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import org.springframework.util.StringUtils;

public class DigmaResourceProvider implements ResourceProvider {

    private final DigmaResourceProperties digmaResourceProperties;

    public DigmaResourceProvider(DigmaResourceProperties digmaResourceProperties) {
        this.digmaResourceProperties = digmaResourceProperties;
    }

    @Override
    public Resource createResource(ConfigProperties config) {
        String envVal = evaluateEnvironment();
        Attributes attrs = Attributes.of(
                DigmaSemanticAttributes.DIGMA_ENVIRONMENT, envVal);

        return Resource.create(attrs, ResourceAttributes.SCHEMA_URL);
    }

    private String evaluateEnvironment() {
        String cfgValue = digmaResourceProperties.getEnvironment();
        if (StringUtils.hasText(cfgValue)) {
            return cfgValue;
        }
        return DigmaCommon.evaluateEnvironment();
    }

}
