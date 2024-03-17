package com.digma.otel.javaagent.extension.instrumentation.methods.test;

import io.opentelemetry.api.trace.Span;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

public class ConfigTracedCompletableFuture {
    public final CountDownLatch countDownLatch = new CountDownLatch(1);
    public Span span;

    public CompletableFuture<String> getResult() {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        span = Span.current();
        new Thread(
                () -> {
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException exception) {
                        // ignore
                    }
                    completableFuture.complete("Hello!");
                })
                .start();
        return completableFuture;
    }
}
