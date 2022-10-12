import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.exclude

plugins {
  id("java")
//id("otel.library-instrumentation")
}

base.archivesName.set("digma-otel-spring-boot")
group = "digma.otel.instrumentation"

val OPENTELEMETRY_VERSION = "1.18.0"
val OPENTELEMETRY_ALPHA_VERSION = "1.18.0-alpha"
val springBootVersion = "2.7.4"

dependencies {
  runtimeOnly("org.springframework.boot:spring-boot-autoconfigure:$springBootVersion")
  annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor:$springBootVersion")
  implementation("javax.validation:validation-api:2.0.1.Final")

  compileOnly("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:${OPENTELEMETRY_ALPHA_VERSION}")
  runtimeOnly("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations-support:${OPENTELEMETRY_ALPHA_VERSION}")
  implementation("io.opentelemetry.instrumentation:opentelemetry-spring-webmvc-5.3:${OPENTELEMETRY_ALPHA_VERSION}")

  runtimeOnly("org.springframework.boot:spring-boot-starter-web:$springBootVersion")
}

tasks.compileTestJava {
  options.compilerArgs.add("-parameters")
}

tasks.withType<Test>().configureEach {
  // required on jdk17
  jvmArgs("--add-opens=java.base/java.lang=ALL-UNNAMED")
  jvmArgs("-XX:+IgnoreUnrecognizedVMOptions")

  // disable tests on openj9 18 because they often crash JIT compiler
  val testJavaVersion = gradle.startParameter.projectProperties["testJavaVersion"]?.let(JavaVersion::toVersion)
  val testOnOpenJ9 = gradle.startParameter.projectProperties["testJavaVM"]?.run { this == "openj9" }
    ?: false
  if (testOnOpenJ9 && testJavaVersion?.majorVersion == "18") {
    enabled = false
  }
}
