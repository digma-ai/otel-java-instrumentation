package com.digma.otel.javaagent.extension.instrumentation.methods.test2;

public class MyClassInOtherPackage {


    public void test(){
        //this class is called from test to make sure its not instrumented because the configuration is
        // for package test and this class is in package test2
    }

}
