package com.digma.otel.instrumentation.common;

public final class DigmaSemanticConventions {
    private DigmaSemanticConventions() {
    }

    public static final String DIGMA_ENVIRONMENT = "digma.environment";
    // code.package.prefixes - value of it can be comma separated
    public static final String DIGMA_CODE_PACKAGE_PREFIXES = "code.package.prefixes";
    public static final String IS_TEST = "is.test";
    public static final String TESTING_FRAMEWORK = "testing.framework";
    public static final String TESTING_RESULT = "testing.result";


    public static final class TestingResultValues {
        public static final String SUCCESS = "success";
        public static final String FAIL = "fail";
        public static final String ERROR = "error";

        private TestingResultValues() {
        }
    }
}
