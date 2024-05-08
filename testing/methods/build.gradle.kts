plugins {
    id("agent-for-testing.java-conventions")
}

tasks {

    withType<Test>() {
        jvmArgs(
            "-Ddigma.autoinstrument.packages=com.digma.otel.javaagent.extension.instrumentation.methods.test",
            //disable digma junit instrumentation module because it interferes with the test,
            // the test will fail on timeout because the digma junit spans don't end.
            // this is otel property convention to disable a module by name, digma-junit
            "-Dotel.instrumentation.digma-junit.enabled=false",
        )
    }

}
