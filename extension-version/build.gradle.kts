plugins {
    `java-library`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }

    sourceSets["main"].java {
        srcDir("$buildDir/generated/java")
    }
}

tasks.register<Copy>("generateJavaWithVersion") {
    from("src/template/java")
    into("$buildDir/generated/java")
    expand("version_val" to version)
}

tasks.compileJava {
    options.release.set(8)
    dependsOn("generateJavaWithVersion")
}