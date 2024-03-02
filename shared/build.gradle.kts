plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("com.google.devtools.ksp")
}

val lightningServerVersion: String by project
val kotlinVersion: String by project

kotlin {
    targetHierarchy.default()
    androidTarget()
    jvm()
    js(IR) {
        browser()
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("com.lightningkite.lightningserver:shared:$lightningServerVersion")
            }
            kotlin {
                srcDir(file("build/generated/ksp/common/commonMain/kotlin"))
            }
        }
    }
}

dependencies {
    configurations.filter { it.name.startsWith("ksp") && it.name != "ksp" }.forEach {
        add(it.name, "com.lightningkite.lightningserver:processor:$lightningServerVersion")
    }
}


android {
    namespace = "com.lightningkite.rock.template"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    dependencies {
        coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    }
}