package com.digma.otel.javaagent.extension.instrumentation.common.tests;

import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

public final class TestingUtils {

    public static String readVersion(String jarFilePath) {
        try (JarFile jarFile = new JarFile(jarFilePath)) {
            String theVersion = (String) jarFile
                .getManifest()
                .getMainAttributes()
                .get(Attributes.Name.IMPLEMENTATION_VERSION);
            return theVersion;
        } catch (IOException e) {
            throw new RuntimeException("unexpected", e);
        }
    }
}
