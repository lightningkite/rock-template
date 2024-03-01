plugins {
    kotlin("jvm")
    id("com.google.gms.google-services") version "4.4.0" apply false
}

buildscript {
    val kotlinVersion:String by extra
    repositories {
        mavenLocal()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven(url = "https://s01.oss.sonatype.org/content/repositories/releases/")
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
        classpath("com.lightningkite:deploy-helpers:master-SNAPSHOT")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.9.0")
        classpath("com.android.tools.build:gradle:7.4.2")
    }
}

allprojects {
    repositories {
        group = "com.lightningkite.rock.template"
        mavenLocal()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven(url = "https://s01.oss.sonatype.org/content/repositories/releases/")
        google()
        mavenCentral()
    }
}
