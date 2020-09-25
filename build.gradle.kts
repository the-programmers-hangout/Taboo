import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "me.moeszyslak"
version = "0.1.0"

plugins {
    kotlin("jvm") version "1.4.10"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("me.jakejmattson:DiscordKt:0.20.0")
    implementation("org.apache.tika:tika-parsers:1.24.1")
    implementation("com.github.kittinunf.fuel:fuel:2.2.3")
    implementation("com.github.kittinunf.fuel:fuel-gson:2.2.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}