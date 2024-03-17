package com.digma.otel.javaagent.extension.instrumentation.methods.test2;

public class MyTestClass {


    public void test(){
        //todo: test should not be instrumented when package supplied is
        // com.digma.otel.javaagent.extension.instrumentation.methods.test
        // this package is test2
    }

}
