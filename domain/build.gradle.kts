plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktlint)
}

java {
    toolchain {
        // Use the JDK available in the execution environment instead of forcing a
        // download of Java 17, while still targeting Java 17 bytecode.
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    api(libs.coroutinesCore)
    implementation(libs.javaxInject)
    testImplementation(testLibs.junit)
}
