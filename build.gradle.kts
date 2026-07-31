plugins {
    kotlin("jvm") version "2.4.0"
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    testImplementation("io.kotest:kotest-runner-junit5-jvm:6.2.3")
    testImplementation("io.kotest:kotest-assertions-core-jvm:6.2.3")
    testImplementation("io.kotest:kotest-property-jvm:6.2.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
