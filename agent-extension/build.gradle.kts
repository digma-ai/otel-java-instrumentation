import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/*
This build script is based on opentelemetry extension example , converted to kotlin
https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/0ca120d1b5e66dec6c5d2637fbb8c70b337718f8/examples/extension/build.gradle
 */

plugins {
    id("java")
    id("com.gradleup.shadow") version ("8.3.5")
    id("io.opentelemetry.instrumentation.muzzle-generation") version ("2.10.0-alpha")
    id("io.opentelemetry.instrumentation.muzzle-check") version ("2.10.0-alpha")
}

val shadowArtifactId = "digma-otel-agent-extension"
project.description = "Digma OpenTelemetry agent extension"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

/*
   We create a separate gradle configuration to grab a published Otel instrumentation agent.
   We don't need the agent during development of this extension module.
   This agent is used only during integration test.
   */
val otel: Configuration by configurations.creating

val junitVersion = "5.10.2"
val springFrameworkVersion = "5.3.24"
val springKafkaVersion = "2.7.1"
val grpcVersion = "1.63.0"

dependencies {
    implementation(platform(libs.otelSdkBom))
    implementation(platform(libs.otelSdkBomAlpha))
    implementation(platform(libs.otelInstBom))
    implementation(platform(libs.otelInstBomAlpha))

    implementation("io.opentelemetry.instrumentation:opentelemetry-jdbc")

    /*
    Interfaces and SPIs that we implement. We use `compileOnly` dependency because during
    runtime all necessary classes are provided by javaagent itself.
     */
    compileOnly("io.opentelemetry:opentelemetry-sdk-extension-autoconfigure-spi")
    compileOnly("io.opentelemetry.instrumentation:opentelemetry-instrumentation-api")
    compileOnly("io.opentelemetry.javaagent:opentelemetry-javaagent-extension-api")
    compileOnly("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations-support")
    implementation(libs.otelSemconv)

    //Provides @AutoService annotation that makes registration of our SPI implementations much easier
    compileOnly("com.google.auto.service:auto-service:1.1.1")
    annotationProcessor("com.google.auto.service:auto-service:1.1.1")

    /*
     Used by our demo instrumentation module to reference classes of the target instrumented library.
     We again use `compileOnly` here because during runtime these classes are provided by the
     actual application that we instrument.

     NB! Only Advice (and "helper") classes of instrumentation modules can access classes from application classpath.
     See https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/main/docs/contributing/writing-instrumentation-module.md#advice-classes
     */
    compileOnly("javax.servlet:javax.servlet-api:3.0.1")


    compileOnly("io.grpc:grpc-core:${grpcVersion}")

    /*
    This dependency is required for DemoSpanProcessor both during compile and runtime.
    Only dependencies added to `implementation` configuration will be picked up by Shadow plugin
    and added to the resulting jar for our extension's distribution.
     */
    implementation(project(":extension-version"))
    implementation(project(":instrumentation:common")) // <==> io.github.digma-ai:digma-otel-instr-common
    implementation(project(":instrumentation:grpc-16:library")) // <==> io.github.digma-ai:digma-otel-instr-grpc

    //datasource-proxy is packaged in digma-agent and in otel extension, fortunately
    // both are loaded by the system class loader so there is no duplicate classed
    //the shadowing must be the same
    implementation("net.ttddyy:datasource-proxy:1.10")

    //All dependencies below are only for tests
    testImplementation("org.testcontainers:testcontainers:1.19.7")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    testImplementation("com.google.protobuf:protobuf-java-util:3.25.3")
    testImplementation("com.squareup.okhttp3:okhttp:4.12.0")
    testImplementation("io.opentelemetry:opentelemetry-api")
    testImplementation("io.opentelemetry.proto:opentelemetry-proto:1.1.0-alpha")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    testRuntimeOnly("ch.qos.logback:logback-classic:1.5.3")

    //Otel Java instrumentation that we use and extend during integration tests
    otel(libs.otelJavaAgent)

    //Various libraries to test their instrumentation
    testImplementation("org.springframework:spring-context:${springFrameworkVersion}")
    testImplementation("org.springframework:spring-web:${springFrameworkVersion}")
    testImplementation("org.springframework.kafka:spring-kafka:${springKafkaVersion}")
    testImplementation("io.grpc:grpc-core:${grpcVersion}")
    testImplementation("io.grpc:grpc-stub:${grpcVersion}")
    testImplementation("io.grpc:grpc-okhttp:${grpcVersion}")
    testImplementation("io.grpc:grpc-protobuf:${grpcVersion}")


    //TODO remove when start using io.opentelemetry.instrumentation.javaagent-instrumentation plugin
    add(
        "codegen",
        "io.opentelemetry.javaagent:opentelemetry-javaagent-tooling:${libs.versions.opentelemetryJavaagentAlpha.get()}"
    )
    add(
        "muzzleBootstrap",
        "io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations-support:${libs.versions.opentelemetryJavaagentAlpha.get()}"
    )
    add(
        "muzzleTooling",
        "io.opentelemetry.javaagent:opentelemetry-javaagent-extension-api:${libs.versions.opentelemetryJavaagentAlpha.get()}"
    )
    add(
        "muzzleTooling",
        "io.opentelemetry.javaagent:opentelemetry-javaagent-tooling:${libs.versions.opentelemetryJavaagentAlpha.get()}"
    )
}


tasks {

    compileJava {
        options.release.set(8)
    }

    shadowJar {
        archiveBaseName.set(shadowArtifactId)
        archiveClassifier.set("")

        //build with no version in release workflow, so we can download latest without version
        if (project.hasProperty("NoArchiveVersion")) {
            archiveVersion.set("")
        } else {
            archiveVersion.set(version.toString())
        }

//        dependencies {
////            exclude(dependency("io.opentelemetry.instrumentation:opentelemetry-instrumentation-api-incubator:2.10.0-alpha"))
////            exclude(dependency("io.opentelemetry.instrumentation:opentelemetry-instrumentation-api:2.10.0-alpha"))
//            exclude(dependency("io.opentelemetry.*:.*:.*"))
//        }

        //should be the same relocation as in digma-agent
        relocate("net.ttddyy.dsproxy", "org.digma.net.ttddyy.dsproxy")

        manifest {
            attributes["Implementation-Title"] = "OTEL Agent extension for OpenTelemetry by Digma"
            attributes["Implementation-Vendor"] = "Digma"
            attributes["Implementation-Version"] = "${project.version}"
            attributes["Created-By"] = "Gradle ${gradle.gradleVersion}"
            attributes["Build-OS"] =
                "${System.getProperty("os.name")} ${System.getProperty("os.arch")} ${System.getProperty("os.version")}"
            attributes["Build-Jdk"] =
                "${System.getProperty("java.version")} ${System.getProperty("java.vendor")} ${System.getProperty("java.vm.version")}"
            attributes["Build-Timestamp"] =
                ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
        }

//    dependencies {
//
//        //just another safety not to include any otel dependencies in the shaded jar
//        exclude(dependency {
//            it.moduleGroup.contains('io.opentelemetry')
//        } as Spec<? super ResolvedDependency>)
//
//        exclude(dependency('io.opentelemetry:') as Spec<? super ResolvedDependency>)
//        exclude(dependency('io.opentelemetry.instrumentation:') as Spec<? super ResolvedDependency>)
//        exclude(dependency('io.opentelemetry.semconv:') as Spec<? super ResolvedDependency>)
//    }
    }


    assemble {
        dependsOn(shadowJar)
    }

    val extendedAgent = register("extendedAgent", Jar::class) {
        dependsOn(otel)
        dependsOn(jar)
        archiveFileName = "opentelemetry-javaagent.jar"
        from(zipTree(otel.singleFile))
        from(shadowJar.get().archiveFile) {
            into("extensions")
        }

        //Preserve MANIFEST.MF file from the upstream javaagent
        doFirst {
            manifest.from(
                zipTree(otel.singleFile).matching {
                    include("META-INF/MANIFEST.MF")
                }.singleFile
            )
        }
    }


    test {
        useJUnitPlatform()

        inputs.files(layout.files(shadowJar))
        inputs.files(layout.files(extendedAgent))

        systemProperty("io.opentelemetry.smoketest.agentPath", otel.singleFile.absolutePath)
        systemProperty(
            "io.opentelemetry.smoketest.extendedAgentPath",
            extendedAgent.get().archiveFile.get().asFile.absolutePath
        )
        systemProperty(
            "io.opentelemetry.smoketest.extensionPath",
            shadowJar.get().archiveFile.get().asFile.absolutePath
        )
    }



    jar {
        manifest {
            attributes["Implementation-Title"] = "OTEL Agent extension for OpenTelemetry by Digma"
            attributes["Implementation-Vendor"] = "Digma"
            attributes["Implementation-Version"] = "${project.version}"
            attributes["Created-By"] = "Gradle ${gradle.gradleVersion}"
            attributes["Build-OS"] =
                "${System.getProperty("os.name")} ${System.getProperty("os.arch")} ${System.getProperty("os.version")}"
            attributes["Build-Jdk"] =
                "${System.getProperty("java.version")} ${System.getProperty("java.vendor")} ${System.getProperty("java.vm.version")}"
            attributes["Build-Timestamp"] =
                ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
        }
    }

}