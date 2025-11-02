plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktlint)
}

java {
    toolchain {
        // Align with the project's Java 17 requirement so that Gradle can reuse a
        // locally installed JDK without downloading Java 21.
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    api(libs.coroutinesCore)
    implementation(libs.javaxInject)
    testImplementation(testLibs.junit)
}
