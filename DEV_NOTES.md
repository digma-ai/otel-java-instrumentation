

## Build
./gradlew clean build<br>
or<br>
./gradlew clean build -x test


## When running tests from Idea
turn off your Digma plugin observability :)

## New instrumentation advices
every new advice should be tested with java 7 bytecode.
see Java7CompatibilityTests.
read the comment there.



### Using podman with testcontainers
https://github.com/eugene-khyst/podman-testcontainers?tab=readme-ov-file#5