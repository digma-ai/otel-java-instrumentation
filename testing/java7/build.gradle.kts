plugins {
    id("agent-for-testing.java-conventions")
}

dependencies {
    testImplementation("org.digma.otel.test:java-7-classes")
}


tasks {

    clean{
        dependsOn(gradle.includedBuild("java-7-classes").task(":clean"))
    }

    compileTestJava{
        dependsOn(gradle.includedBuild("java-7-classes").task(":build"))
    }


    withType<Test>() {
        jvmArgs(
            "-Ddigma.autoinstrument.packages=org.digma.otel.test.simple",
            //todo: maybe should be false ?
            "-Dotel.instrumentation.digma-junit.enabled=true" //disable digma junit because it interferes with the test
        )
    }
}