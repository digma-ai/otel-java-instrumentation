plugins {
    `java-library`
    `maven-publish`
    signing
}

val vArtifactId = "digma-otel-instr-grpc"
project.description = "Digma, OpenTelemetry instrumentation for GRPC - library"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
    withJavadocJar()
    withSourcesJar()
}

base.archivesName.set(vArtifactId)

val OPENTELEMETRY_VERSION = "1.21.0"
val OPENTELEMETRY_ALPHA_VERSION = "1.21.0-alpha"
val junitJupiterVersion = "5.9.1"
val grpcVersion = "1.6.0"

dependencies {
    implementation(project(":instrumentation:common"))

    runtimeOnly("io.opentelemetry:opentelemetry-api:${OPENTELEMETRY_VERSION}")
    runtimeOnly("io.opentelemetry:opentelemetry-sdk-extension-autoconfigure-spi:${OPENTELEMETRY_VERSION}")
    runtimeOnly("io.opentelemetry.instrumentation:opentelemetry-instrumentation-api:${OPENTELEMETRY_VERSION}")
    runtimeOnly("io.opentelemetry.instrumentation:opentelemetry-instrumentation-api-semconv:${OPENTELEMETRY_ALPHA_VERSION}")
    compileOnly("io.opentelemetry.instrumentation:opentelemetry-grpc-1.6:${OPENTELEMETRY_ALPHA_VERSION}")
    compileOnly("io.grpc:grpc-core:$grpcVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")

    testImplementation("io.grpc:grpc-netty:$grpcVersion")
    testImplementation("io.grpc:grpc-protobuf:$grpcVersion")
    testImplementation("io.grpc:grpc-services:$grpcVersion")
    testImplementation("io.grpc:grpc-stub:$grpcVersion")
}

tasks.compileJava {
    options.release.set(8)
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
