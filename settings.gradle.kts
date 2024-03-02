
rootProject.name = "rock-template"

pluginManagement {
    val kotlinVersion: String by settings
    val kspVersion: String by settings
    val rockVersion: String by settings
    repositories {
        mavenLocal()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven(url = "https://s01.oss.sonatype.org/content/repositories/releases/")
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("multiplatform") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        id("com.google.devtools.ksp") version kspVersion
        id("com.lightningkite.rock") version rockVersion
    }
}

include(":apps")
include(":shared")
include(":server")
