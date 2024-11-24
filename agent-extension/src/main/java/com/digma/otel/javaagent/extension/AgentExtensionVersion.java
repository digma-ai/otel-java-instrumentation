package com.digma.otel.javaagent.extension;

import org.digma.otel.extension.extension.version.BuildVersion;

public final class AgentExtensionVersion {

    public static final String VERSION = BuildVersion.getVersion();
    // getPackage().getImplementationVersion() doesn't work via OTEL agent extension
    //AgentExtensionVersion.class.getPackage().getImplementationVersion();

    private AgentExtensionVersion() {
    }
}
