plugins {
    `java-library`
    `maven-publish`
    signing
}

val vArtifactId = "digma-otel-instr-spring-boot"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
    withJavadocJar()
    withSourcesJar()
}

base.archivesName.set(vArtifactId)
project.description = "Digma, Auto-configures OpenTelemetry instrumentation for SpringBoot"

val OPENTELEMETRY_VERSION = "1.21.0"
val OPENTELEMETRY_ALPHA_VERSION = "1.21.0-alpha"
val springBootVersion = "2.7.5"
val junitJupiterVersion = "5.9.1"

dependencies {
    implementation(project(":instrumentation:common"))

    api("io.opentelemetry:opentelemetry-api:${OPENTELEMETRY_VERSION}")
    api("io.opentelemetry:opentelemetry-sdk-extension-autoconfigure-spi:${OPENTELEMETRY_VERSION}")
    api("io.opentelemetry.instrumentation:opentelemetry-instrumentation-api-semconv:${OPENTELEMETRY_ALPHA_VERSION}")
    api("io.opentelemetry.instrumentation:opentelemetry-spring-boot:${OPENTELEMETRY_ALPHA_VERSION}")

    runtimeOnly("org.springframework.boot:spring-boot-autoconfigure:$springBootVersion")
    api("org.springframework.boot:spring-boot-starter-aop:$springBootVersion")
    api("org.springframework.boot:spring-boot-starter-web:$springBootVersion")
    api("javax.validation:validation-api:2.0.1.Final")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-aop:$springBootVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-web:$springBootVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion") {
        exclude("org.junit.vintage", "junit-vintage-engine")
    }
}

tasks.test {
    useJUnitPlatform {}
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

publishing {
    publications {
        create<MavenPublication>(vArtifactId) {
            artifactId = vArtifactId

            from(components["java"])

            pom {
                name.set(vArtifactId)
                description.set(project.description)
                url.set("https://github.com/digma-ai/otel-java-instrumentation")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("http://www.opensource.org/licenses/mit-license.php")
                    }
                }
                developers {
                    developer {
                        name.set("Arik Sher")
                        email.set("asher@digma.ai")
                        organization.set("digma.ai")
                        organizationUrl.set("http://digma.ai/")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/digma-ai/otel-java-instrumentation.git")
                    developerConnection.set("scm:git:ssh://github.com:digma-ai/otel-java-instrumentation.git")
                    url.set("https://github.com/digma-ai/otel-java-instrumentation")
                }
            }
        }
    }
    repositories {
        maven {
            if (version.toString().endsWith("SNAPSHOT")) {
                url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                mavenContent {
                    snapshotsOnly()
                }
            } else {
                url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                mavenContent {
                    releasesOnly()
                }
            }
            credentials {
                username = System.getenv("EV_ossrhUsername")
                password = System.getenv("EV_ossrhPassword")
            }
        }
    }
}

signing {
    setRequired({
        gradle.taskGraph.hasTask("publish")
    })

    sign(publishing.publications[vArtifactId])
}
