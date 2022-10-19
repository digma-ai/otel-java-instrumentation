package com.digma.otel.instrumentation.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class CommonUtils {
    private CommonUtils() {
    }

    public static String getEnvOrSystemProperty(String entryName) {
        String envVal = System.getenv(entryName);
        if (envVal != null) {
            return envVal;
        }
        String propertyVal = System.getProperty(entryName);
        return propertyVal;
    }

    public static String getLocalHostname() {
        String hostname;
        try {
            InetAddress localInetAddress = InetAddress.getLocalHost();
            hostname = localInetAddress.getHostName();
        } catch (UnknownHostException e) {
            hostname = hostnameByEnvVar();
        }
        return hostname;
    }

    private static String hostnameByEnvVar() {
        String hostname;
        hostname = System.getenv("COMPUTERNAME"); // windows
        if (hostname == null) {
            hostname = System.getenv("HOSTNAME"); // linux
        }
        if (hostname == null) {
            throw new RuntimeException("could not resolve hostname by environment variables");
        }
        return hostname;
    }

}
