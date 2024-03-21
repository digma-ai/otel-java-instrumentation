plugins {
    id("agent-for-testing.java-conventions")
}

tasks {

    withType<Test>() {
        jvmArgs(
            "-Ddigma.autoinstrument.packages=com.digma.otel.javaagent.extension.instrumentation.methods.test",
            "-Dotel.instrumentation.digma-junit.enabled=false", //disable digma junit because it interferes with the test
        )
    }

}
