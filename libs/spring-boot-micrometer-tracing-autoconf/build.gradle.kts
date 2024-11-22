plugins {
    `java-library`
    `maven-publish`
    signing
}

val vArtifactId = "digma-spring-boot-micrometer-tracing-autoconf"
project.description = "Digma, Spring Boot, Micrometer Tracing - AutoConfig"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withJavadocJar()
    withSourcesJar()
}

base.archivesName.set(vArtifactId)

// spring boot + opentelemetry metrics
//  |-------------+--------------+
//  | spring boot | OTEL version |
//  |-------------+--------------+
//  |  3.0.x      |  1.19.0      |
//  |  3.1.x      |  1.25.0      |
//  |  3.2.x      |  1.31.0      |
//  |-------------+--------------+

val springBootVersion = "3.1.6"
val micrometerTracingVersion = "1.1.7"
val otelVersion = "1.44.1"
val junitJupiterVersion = "5.9.3"

dependencies {
    compileOnly("io.micrometer:micrometer-tracing:${micrometerTracingVersion}")
    compileOnly("org.springframework.boot:spring-boot-starter-actuator:${springBootVersion}")
    compileOnly("org.springframework.boot:spring-boot-actuator-autoconfigure:${springBootVersion}")
    implementation("io.opentelemetry:opentelemetry-sdk:${otelVersion}")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp:${otelVersion}")
    implementation("io.opentelemetry:opentelemetry-exporter-common:1.31.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
}

tasks.compileJava {
    options.release.set(17)
}

tasks.test {
    useJUnitPlatform {}
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
