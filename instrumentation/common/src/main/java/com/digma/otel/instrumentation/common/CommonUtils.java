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

    /**
     * Check whether the given {@code String} contains actual <em>text</em>.
     * <p>More specifically, this method returns {@code true} if the
     * {@code String} is not {@code null}, its length is greater than 0,
     * and it contains at least one non-whitespace character.
     *
     * @param str - input
     * @return {@code true} if the {@code String} is not {@code null}, its
     */
    public static boolean hasText(CharSequence str) {
        return (str != null && containsText(str));
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
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
