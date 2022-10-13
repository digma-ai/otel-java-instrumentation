plugins {
  id("java")
  id("maven-publish")
}

base.archivesName.set("digma-otel-spring-boot")
group = "com.digma"

val OPENTELEMETRY_VERSION = "1.18.0"
val OPENTELEMETRY_ALPHA_VERSION = "1.18.0-alpha"
val springBootVersion = "2.7.4"

dependencies {
  implementation("io.opentelemetry:opentelemetry-api:${OPENTELEMETRY_VERSION}")
  implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-api-semconv:${OPENTELEMETRY_ALPHA_VERSION}")

  runtimeOnly("org.springframework.boot:spring-boot-autoconfigure:$springBootVersion")
  compileOnly("org.springframework.boot:spring-boot-starter-aop:$springBootVersion")
  compileOnly("org.springframework.boot:spring-boot-starter-web:$springBootVersion")
  implementation("javax.validation:validation-api:2.0.1.Final")

  testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.test {
  useJUnitPlatform{}
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
