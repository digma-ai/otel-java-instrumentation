plugins {
    `java-library`
    `maven-publish`
    signing
}

val vArtifactId = "digma-otel-instr-common"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
    withJavadocJar()
    withSourcesJar()
}

base.archivesName.set(vArtifactId)

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

publishing {
    publications {
        create<MavenPublication>(vArtifactId) {
            artifactId = vArtifactId

            from(components["java"])

            pom {
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("http://www.opensource.org/licenses/mit-license.php")
                    }
                }
                developers {
                    developer {
                        id.set("asher")
                        name.set("Arik Sher")
                        email.set("asher@digma.ai")
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
