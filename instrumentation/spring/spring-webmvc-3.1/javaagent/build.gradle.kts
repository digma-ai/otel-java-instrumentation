plugins {
    `java-library`
    `maven-publish`
    signing
}

val vArtifactId = "digma-otel-instr-spring-webmvc-31-javaagent"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
    withJavadocJar()
    withSourcesJar()
}

base.archivesName.set(vArtifactId)
project.description = "Digma, OpenTelemetry javaagent instrumentation for Spring Webmvc 3.1"

val OPENTELEMETRY_VERSION = "1.19.0"
val OPENTELEMETRY_ALPHA_VERSION = "1.19.2-alpha"
val autoServiceVersion = "1.0.1"
val junitJupiterVersion = "5.9.1"

dependencies {
    implementation(project(":instrumentation:common"))
    annotationProcessor("com.google.auto.service:auto-service:${autoServiceVersion}")
    compileOnly("com.google.auto.service:auto-service:${autoServiceVersion}")
    compileOnly("com.google.auto.service:auto-service-annotations:${autoServiceVersion}")
    implementation("io.opentelemetry:opentelemetry-api:${OPENTELEMETRY_VERSION}")
    compileOnly("io.opentelemetry:opentelemetry-sdk-extension-autoconfigure-spi:${OPENTELEMETRY_VERSION}")
    compileOnly("io.opentelemetry.instrumentation:opentelemetry-instrumentation-api-semconv:${OPENTELEMETRY_ALPHA_VERSION}")
    implementation("io.opentelemetry.javaagent:opentelemetry-javaagent-extension-api:${OPENTELEMETRY_ALPHA_VERSION}")

    implementation("javax.validation:validation-api:2.0.1.Final")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
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
