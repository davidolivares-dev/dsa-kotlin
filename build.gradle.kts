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

// ---- retention drills ------------------------------------------------
// `src/drill/kotlin` holds cold reimplementations written from a
// <TOPIC>_TASK.md stub, notes closed. `drillTest` runs the real test suite
// against them: drill output sits ahead of main's on the runtime classpath,
// so a drill class shadows its committed counterpart while every other class
// falls through to main. The committed implementation is never touched, so a
// drill can be diffed against it afterwards.
//
// Gitignored, and deliberately not part of `check` or CI — a failing drill is
// information, not a broken build.
val drill by sourceSets.creating {
    kotlin.srcDir("src/drill/kotlin")
    compileClasspath += sourceSets.main.get().output
}

tasks.register<Test>("drillTest") {
    description = "Runs the real tests against reimplementations in src/drill/kotlin."
    group = "verification"
    testClassesDirs =
        sourceSets.test
            .get()
            .output.classesDirs
    classpath = drill.output +
        sourceSets.test.get().output +
        configurations.testRuntimeClasspath.get() +
        sourceSets.main.get().output
    useJUnitPlatform()

    // Run only the suites being drilled, by convention:
    //   src/drill/kotlin/trees/Trie.kt  ->  trees.TrieTest
    val drillRoot = layout.projectDirectory.dir("src/drill/kotlin").asFile
    val suites =
        if (drillRoot.exists()) {
            drillRoot
                .walkTopDown()
                .filter { it.isFile && it.extension == "kt" }
                .map {
                    it
                        .relativeTo(drillRoot)
                        .path
                        .removeSuffix(".kt")
                        .replace(File.separatorChar, '.') + "Test"
                }.toList()
        } else {
            emptyList()
        }

    if (suites.isEmpty()) {
        doFirst {
            throw GradleException(
                "No drills found. Copy a stub from a <TOPIC>_TASK.md into " +
                    "src/drill/kotlin/<package>/ first.",
            )
        }
    } else {
        filter { suites.forEach { includeTestsMatching(it) } }
        doFirst { logger.lifecycle("Drilling: " + suites.joinToString(", ")) }
    }
}
