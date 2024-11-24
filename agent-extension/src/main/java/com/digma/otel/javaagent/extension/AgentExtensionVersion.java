package com.digma.otel.javaagent.extension;


public final class AgentExtensionVersion {

    public static final String VERSION = com.digma.otel.extension.extension.version.BuildVersion.getVersion();
    // getPackage().getImplementationVersion() doesn't work via OTEL agent extension
    //AgentExtensionVersion.class.getPackage().getImplementationVersion();

    private AgentExtensionVersion() {
    }
}
