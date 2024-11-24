
plugins {
    `groovy-gradle-plugin`
    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

kotlin {
    jvmToolchain {
        this.languageVersion.set(JavaLanguageVersion.of("17"))
    }
}

dependencies {
    //hack to use type safe accessors in script plugins from gradle/libs.versions.toml. see https://github.com/gradle/gradle/issues/28371
    // follow this post and if there is something better remove this hack
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation("com.glovoapp.gradle:versioning:1.1.10")
}