import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm") version "1.9.24"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

group = "kurasava.ep"
version = project.property("version").toString()

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainClass = "kurasava.ep.epmodpack.AppKt"
    applicationDefaultJvmArgs = listOf("-Xmx256m")
}

val javafxVersion = project.property("javafx_version").toString()

javafx {
    version = javafxVersion
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("com.github.hervegirod:fxsvgimage:1.1")
    implementation("org.openjfx:javafx-graphics:$javafxVersion:linux")
    implementation("org.json:json:20231013")
    implementation("com.github.Querz:NBT:6.1")
}

ktlint {
    version.set("0.50.0")
    debug.set(true)
    verbose.set(true)
    android.set(false)
    outputToConsole.set(true)
    outputColorName.set("RED")
    ignoreFailures.set(false)
    enableExperimentalRules.set(true)
    reporters {
        reporter(ReporterType.CHECKSTYLE)
    }
}

kotlin {
    jvmToolchain(17)
}
