plugins {
    id("java")
}


val agentForTesting: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

val digmaExtension: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

dependencies{

    compileOnly(project(":agent-extension"))

    digmaExtension(project(mapOf(
        "path" to ":agent-extension",
        "configuration" to "shadow")))

    agentForTesting(libs.agentForTesting)

    implementation(platform(libs.otelInstBom))
    implementation(platform(libs.otelInstBomAlpha))


    testImplementation(libs.opentelemetryTestingCommon)
    testImplementation(libs.junitJupiter)
    testImplementation(libs.junitJupiterEngine)
    testImplementation(libs.logback)
}


tasks{
    withType<Test>(){
        useJUnitPlatform()

        logger.lifecycle("running test with otel agent ${agentForTesting.files.first().absolutePath}")
        logger.lifecycle("running test with digma extension ${digmaExtension.files.first().absolutePath}")

//        jvmArgs( "-Dotel.javaagent.debug=true",
        jvmArgs(
            "-javaagent:${agentForTesting.files.first().absolutePath}",
            "-Dotel.javaagent.testing.javaagent-jar-path=${agentForTesting.files.first().absolutePath}",
            "-Dotel.javaagent.testing.fail-on-context-leak=true",
            "-Dotel.javaagent.testing.additional-library-ignores.enabled=false",
            "-Dotel.javaagent.extensions=${digmaExtension.files.first().absolutePath}",
            "-Ddigma.autoinstrument.packages=com.digma.otel.javaagent.extension.instrumentation.methods.test"
            )
//        jvmArgs "-javaagent:${configurations.agentForTesting.files.first().absolutePath}"
//        jvmArgs "-Dotel.javaagent.experimental.initializer.jar=${shadowJar.archiveFile.get().asFile.absolutePath}"
//        jvmArgs "-Dotel.javaagent.testing.additional-library-ignores.enabled=false"
//        jvmArgs "-Dotel.javaagent.testing.fail-on-context-leak=true"
//        // prevent sporadic gradle deadlocks, see SafeLogger for more details
//        jvmArgs "-Dotel.javaagent.testing.transform-safe-logging.enabled=true"

    }
}