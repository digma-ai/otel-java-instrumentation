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
            //here digma-junit is not disabled because we don't run anything, just check the advices
            "-Dotel.instrumentation.digma-junit.enabled=true"
        )
    }
}