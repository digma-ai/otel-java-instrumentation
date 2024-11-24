plugins {
    id("java-library")
    id("edu.sc.seis.version-class") version "1.3.0"
}



java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }

    sourceSets["main"].java {
        srcDir(layout.buildDirectory.dir("generated-src/version/java"))
    }

}

tasks.compileJava {
    options.release.set(8)
}