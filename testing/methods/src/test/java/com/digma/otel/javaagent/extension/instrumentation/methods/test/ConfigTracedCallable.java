package com.digma.otel.javaagent.extension.instrumentation.methods.test;

import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.concurrent.Callable;

public class ConfigTracedCallable  implements Callable<String> {
//    @WithSpan
    @Override
    public String call() {
        return "Hello!";
    }
}
