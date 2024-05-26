
plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id ("org.jetbrains.kotlin.jvm") version "1.9.24"
}

group = "kurasava.ep"
version = project.property("version").toString()


repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainModule = "kurasava.ep.epmodpack"
    mainClass = "${mainModule.get()}.AppKt"
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}

kotlin {
    jvmToolchain(17)
}
