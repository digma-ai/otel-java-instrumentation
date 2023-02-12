package com.digma.otel.javaagent.extension;

import com.digma.otel.javaagent.extension.version.DigmaExtensionVersion;

public final class AgentExtensionVersion {

    public static final String VERSION = DigmaExtensionVersion.VERSION;
    // getPackage().getImplementationVersion() doesn't work via OTEL agent extension
    //AgentExtensionVersion.class.getPackage().getImplementationVersion();

    private AgentExtensionVersion() {
    }
}
