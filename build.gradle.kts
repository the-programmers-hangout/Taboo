import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "me.moeszyslak"
version = Versions.BOT

plugins {
    kotlin("jvm") version "1.4.0"
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

repositories {
    mavenCentral()
    jcenter()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation("me.jakejmattson:DiscordKt:${Versions.DISCORDKT}")
    implementation("org.apache.tika:tika-parsers:1.24.1")
    implementation("com.github.kittinunf.fuel:fuel:2.2.3")
    implementation("com.github.kittinunf.fuel:fuel-gson:2.2.3")
}


tasks {
    withType<KotlinCompile> {
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
    const val DISCORDKT = "0.21.1"
}