import org.gradle.accessors.dm.LibrariesForLibs

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

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

//hack to use type safe accessors. see https://github.com/gradle/gradle/issues/28371
val libs = the<LibrariesForLibs>()
//can also be use like that but its not type safe accessors. see https://docs.gradle.org/8.5/release-notes.html#catalog-precompiled
//val libs = versionCatalogs.named("libs")
//implementation(platform(libs.findLibrary("otelInstBom")))

dependencies {


    implementation(platform(libs.otelInstBom))
    implementation(platform(libs.otelInstBomAlpha))

    testCompileOnly(project(":extension-version"))
    testCompileOnly(project(":agent-extension"))

    testImplementation(libs.otelInstApi)
    testImplementation(libs.opentelemetryTestingCommon)
    testImplementation(libs.junitJupiter)
    testImplementation(libs.junitJupiterEngine)
    testImplementation(libs.logback)


    digmaExtension(
        project(
            mapOf(
                "path" to ":agent-extension",
                "configuration" to "shadow"
            )
        )
    )

    agentForTesting(libs.agentForTesting)

}


tasks {

    //because the project depends on agent-extension for test compile and agent-extension has byteBuddyJava task
    // that produces an artifact then needs this dependency.
    //not the best way to depend on task in other project because it creates coupling between the projects but
    // probably good enough in this case.
    compileTestJava{
        dependsOn(":agent-extension:byteBuddyJava")
    }


    withType<Test>() {

        dependsOn(digmaExtension)
        mustRunAfter(":agent-extension:test")

        useJUnitPlatform()

        logger.lifecycle("running test with otel agent ${agentForTesting.files.first().absolutePath}")
        logger.lifecycle("running test with digma extension ${digmaExtension.files.first().absolutePath}")

        jvmArgs(
            "-Dotel.javaagent.debug=true",
            "-javaagent:${agentForTesting.files.first().absolutePath}",
            "-Dotel.javaagent.extensions=${digmaExtension.files.first().absolutePath}",
            "-Dotel.javaagent.testing.javaagent-jar-path=${agentForTesting.files.first().absolutePath}",
            "-Dotel.javaagent.testing.fail-on-context-leak=true",
            "-Dotel.javaagent.testing.additional-library-ignores.enabled=false",
            "-Dio.opentelemetry.context.enableStrictContext=true",
            "-Dio.opentelemetry.javaagent.shaded.io.opentelemetry.context.enableStrictContext=true",
            "-Dotel.javaagent.testing.transform-safe-logging.enabled=true",
            "-Dotel.javaagent.add-thread-details=false",
            "-Dotel.java.disabled.resource.providers=io.opentelemetry.sdk.extension.resources.HostResourceProvider,io.opentelemetry.sdk.extension.resources.OsResourceProvider,io.opentelemetry.sdk.extension.resources.ProcessResourceProvider,io.opentelemetry.sdk.extension.resources.ProcessRuntimeResourceProvider"
        )
    }
}