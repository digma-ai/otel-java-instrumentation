package com.digma.otel.javaagent.extension;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class DigmaVersionLogger {

    private static final Logger logger = Logger.getLogger(DigmaVersionLogger.class.getName());

    public static void logVersion() {
        logger.log(Level.INFO, "Digma-Agent-Extension - version: {0}", AgentExtensionVersion.VERSION);
    }
}
