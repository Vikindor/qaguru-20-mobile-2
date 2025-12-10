import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("java")
    id("io.qameta.allure") version "3.0.0" // allure-framework/allure-gradle
}

group = "io.github.vikindor"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.codeborne:selenide:7.12.2")
    testImplementation("io.qameta.allure:allure-selenide:2.31.0")
    testImplementation("io.rest-assured:rest-assured:5.5.6")
    testImplementation("io.qameta.allure:allure-rest-assured:2.31.0")
    allureRawResultElements(files(layout.buildDirectory.dir("allure-results")))
    testImplementation("org.aeonbits.owner:owner:1.0.12")
    testImplementation("io.appium:java-client:10.0.0")
    testImplementation("commons-io:commons-io:2.21.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.slf4j:slf4j-simple:2.0.17")
}

allure {
    report {
        version.set("2.35.1") // allure-framework/allure2
    }
    adapter {
        autoconfigure.set(true)
        autoconfigureListeners.set(true)
        frameworks {
            junit5 {
                adapterVersion.set("2.30.0") // Same as allure-framework/allure-java
            }
        }
    }
}

tasks.test {
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath

    useJUnitPlatform()
    systemProperties(
        System.getProperties()
            .entries
            .associate { (k, v) -> k.toString() to v }
    )

    systemProperty("file.encoding", "UTF-8")

    testLogging {
        events = setOf(
            TestLogEvent.STARTED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED,
            TestLogEvent.FAILED,
            TestLogEvent.STANDARD_OUT,
            TestLogEvent.STANDARD_ERROR
        )
        exceptionFormat = TestExceptionFormat.SHORT
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }

    jvmArgs("-Dfile.encoding=UTF-8","-Dorg.slf4j.simpleLogger.logFile=System.out")
    environment("SE_AVOID_STATS", "true")
}
