package com.digma.otel.javaagent.extension.instrumentation.methods.test;

public class AnonymousTestClass {


    public void methodWithLambda(){

        new Thread(() -> System.out.print("test"));

    }

    public void methodWithAnonymousClass(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.printf("test");
            }
        });

    }

}
