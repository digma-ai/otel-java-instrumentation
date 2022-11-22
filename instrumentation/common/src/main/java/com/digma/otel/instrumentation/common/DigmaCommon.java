package com.digma.otel.instrumentation.common;

public final class DigmaCommon {
    private DigmaCommon() {
    }

    public static final String ENV_VAR_DEPLOYMENT_ENVIRONMENT = "DEPLOYMENT_ENV";
    public static final String ENV_VAR_CODE_PACKAGE_PREFIXES = "CODE_PACKAGE_PREFIXES";

    public static String evaluateEnvironment() {
        String valueFromEnv = CommonUtils.getEnvOrSystemProperty(ENV_VAR_DEPLOYMENT_ENVIRONMENT);
        if (valueFromEnv != null) {
            return valueFromEnv;
        }
        return getLocalHostnameAsEnv();
    }

    public static String getLocalHostnameAsEnv() {
        String localHostname = CommonUtils.getLocalHostname();
        return localHostname + "[local]";
    }

    // can return null
    public static String evaluateCodePackagePrefixes() {
        String valueFromEnv = CommonUtils.getEnvOrSystemProperty(ENV_VAR_CODE_PACKAGE_PREFIXES);
        return valueFromEnv;
    }
}
