group = "me.moeszyslak"
version = Versions.BOT

plugins {
    kotlin("jvm") version "1.5.21"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation("me.jakejmattson:DiscordKt:${Versions.DISCORDKT}")
    implementation("org.apache.tika:tika-parsers:1.24.1")
    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation("com.github.kittinunf.fuel:fuel-gson:2.3.1")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    shadowJar {
        archiveFileName.set("Taboo.jar")
        manifest {
            attributes(
                "Main-Class" to "me.moeszyslak.taboo.MainAppKt"
            )
        }
    }
}

object Versions {
    const val BOT = "1.0.0"
    const val DISCORDKT = "0.23.0-SNAPSHOT"
}