import java.util.Properties

description = "Multi-module Pomodoro focus timer built with Jetpack Compose"

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kapt) apply false
}

val versionPropsFile = file("version.properties")
val versionProps = Properties().apply {
    if (versionPropsFile.exists()) {
        versionPropsFile.inputStream().use { load(it) }
    }
}

val versionCode: Int = versionProps.getProperty("VERSION_CODE", "1").toInt()
val versionName: String = versionProps.getProperty("VERSION_NAME", "1.0.0")

extra["versionCode"] = versionCode
extra["versionName"] = versionName

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

tasks.register("printVersion") {
    group = "versioning"
    description = "Prints the current version code and name."
    doLast {
        println("versionCode=$versionCode versionName=$versionName")
    }
}

tasks.register("bumpVersionCode") {
    group = "versioning"
    description = "Increments VERSION_CODE in version.properties."
    doLast {
        val nextCode = versionCode + 1
        versionProps.setProperty("VERSION_CODE", nextCode.toString())
        versionPropsFile.outputStream().use { versionProps.store(it, null) }
        println("VERSION_CODE bumped to $nextCode")
    }
}
