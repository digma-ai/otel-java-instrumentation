package com.digma.otel.javaagent.extension.instrumentation.methods.test;

import java.util.concurrent.Callable;

public class ConfigTracedCallable  implements Callable<String> {
    @Override
    public String call() {
        return "Hello!";
    }
}
