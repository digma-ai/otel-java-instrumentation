package com.digma.otel.instrumentation.common;

public final class DigmaCommon {
    private DigmaCommon() {
    }

    public static final String ENV_VAR_DEPLOYMENT_ENVIRONMENT = "DEPLOYMENT_ENV";

    public static String evaluateEnvironment() {
        String valueFromEnv = CommonUtils.getEnvOrSystemProperty(ENV_VAR_DEPLOYMENT_ENVIRONMENT);
        if (valueFromEnv != null) {
            return valueFromEnv;
        }
        String localHostname = CommonUtils.getLocalHostname();
        return localHostname + "[local]";
    }
}
