pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version("0.7.0")
}

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    mavenLocal()
  }
}

rootProject.name = "digma-otel-java-instrumentation"


include(":libs:spring-boot-micrometer-tracing-autoconf")


include(":instrumentation:common")
include(":instrumentation:spring:spring-boot-autoconfigure")
include(":instrumentation:grpc-16:library")
include(":extension-version")
include(":agent-extension")
include(":testing:methods")
include(":testing:java7")


includeBuild("test-materials/java-7-classes") {
  dependencySubstitution {
    substitute(module("org.digma.otel.test:java-7-classes")).using(project(":"))
  }
}