pluginManagement {
  plugins {
    id("com.bmuschko.docker-remote-api") version "7.3.0"
    id("com.github.jk1.dependency-license-report") version "2.1"
    id("com.google.cloud.tools.jib") version "3.2.1"
    id("com.gradle.plugin-publish") version "1.0.0"
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("org.jetbrains.kotlin.jvm") version "1.6.20"
    id("org.unbroken-dome.test-sets") version "4.0.0"
    id("org.xbib.gradle.plugin.jflex") version "1.6.0"
    id("org.unbroken-dome.xjc") version "2.0.0"
  }

  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

plugins {
  id("com.github.burrunan.s3-build-cache") version "1.3"
}

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    mavenLocal()
  }
}

rootProject.name = "digma-otel-java-instrumentation"

// agent projects

// misc
//include(":dependencyManagement")

// instrumentations
include(":instrumentation:common")
include(":instrumentation:spring:spring-boot-autoconfigure")
include(":instrumentation:grpc-16:library")
include(":agent-extension")

// benchmark
