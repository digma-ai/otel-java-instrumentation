plugins {
    id("java")
    id("maven-publish")
    signing
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
    withJavadocJar()
    withSourcesJar()
}

signing {
    setRequired({
        gradle.taskGraph.hasTask("publish")
    })
    sign(configurations.archives.get())
}

base.archivesName.set("digma-otel-instr-common")

val OPENTELEMETRY_VERSION = "1.18.0"
val OPENTELEMETRY_ALPHA_VERSION = "1.18.0-alpha"

dependencies {
    implementation("io.opentelemetry:opentelemetry-api:${OPENTELEMETRY_VERSION}")
    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-api-semconv:${OPENTELEMETRY_ALPHA_VERSION}")
    implementation("javax.validation:validation-api:2.0.1.Final")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.test {
    useJUnitPlatform {}
}
