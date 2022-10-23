plugins {
    id("java")
    id("idea")
    id("maven-publish")
}

description = "Digma OpenTelemetry instrumentations for Java"
group = "com.digma"

allprojects {
    version = "0.0.9"
}

tasks.withType<JavaCompile>().configureEach {
    javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}
