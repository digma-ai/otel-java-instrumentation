package com.digma.otel.javaagent.extension;

import com.digma.otel.instrumentation.common.DigmaSemanticAttributes;
import com.google.auto.service.AutoService;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ResourceProvider;
import io.opentelemetry.sdk.resources.Resource;

@AutoService(ResourceProvider.class)
public class DigmaVersionResourceProvider implements ResourceProvider {

    public DigmaVersionResourceProvider() {
        DigmaVersionLogger.logVersion();
    }

    @Override
    public Resource createResource(ConfigProperties config) {
        Attributes attributes = Attributes.of(
            DigmaSemanticAttributes.DIGMA_AGENT_VERSION, AgentExtensionVersion.VERSION);

        return Resource.create(attributes);
    }
}
