plugins {
    java
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks{
    withType<JavaCompile>(){
        options.release.set(7)
    }
}

dependencies{
    implementation("org.springframework:spring-context:5.0.20.RELEASE"){
        isTransitive = false
    }
    implementation("org.springframework:spring-web:5.0.20.RELEASE"){
        isTransitive = false
    }
    implementation("org.springframework.kafka:spring-kafka:2.7.1"){
        isTransitive = false
    }
    implementation("io.grpc:grpc-core:1.6.0"){
        isTransitive = false
    }
    implementation("javax.ws.rs:javax.ws.rs-api:2.1.1"){
        isTransitive = false
    }
    implementation("junit:junit:4.13.2"){
        isTransitive = false
    }
    implementation("org.junit.jupiter:junit-jupiter-api:5.5.2"){
        isTransitive = false
    }
}
