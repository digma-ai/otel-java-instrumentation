package com.digma.otel.javaagent.extension;

public final class AgentExtensionVersion {

    public static final String VERSION =
        AgentExtensionVersion.class.getPackage().getImplementationVersion();

    private AgentExtensionVersion() {
    }
}
