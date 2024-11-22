plugins {
    id("idea")
}

apply(from = "version.gradle.kts")

description = "Digma OpenTelemetry instrumentations for Java"


tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
    version = "8.11"
}
