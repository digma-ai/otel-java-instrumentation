plugins {
    id("semantic-version")
}

description = "Digma OpenTelemetry instrumentations for Java"

allprojects{
    group = "com.digma.otel.extension"
    version = common.semanticversion.getSemanticVersion(project)
}

tasks{
    wrapper {
        //to upgrade gradle change the version here and run:
        //./gradlew wrapper --gradle-version 8.11
        //check that gradle/wrapper/gradle-wrapper.properties was changed
        gradleVersion = "8.11"
        distributionType = Wrapper.DistributionType.ALL
        distributionBase = Wrapper.PathBase.GRADLE_USER_HOME
        distributionPath = "wrapper/dists"
        archiveBase = Wrapper.PathBase.GRADLE_USER_HOME
        archivePath = "wrapper/dists"
    }
}
