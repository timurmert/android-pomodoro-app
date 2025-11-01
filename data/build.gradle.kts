plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ktlint)
}

android {
    namespace = "com.hydrabon.pomodoro.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        buildConfig = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":domain"))

    implementation(libs.coreKtx)
    implementation(libs.coroutinesCore)
    implementation(libs.coroutinesAndroid)
    implementation(libs.hiltAndroid)
    implementation(libs.roomRuntime)
    implementation(libs.roomKtx)
    implementation(libs.datastorePreferences)
    implementation(libs.workRuntime)
    implementation(libs.workHilt)

    kapt(libs.hiltCompiler)
    kapt(libs.roomCompiler)
    kapt(libs.workCompiler)

    testImplementation(testLibs.junit)
    androidTestImplementation(testLibs.androidxJunit)
    androidTestImplementation(testLibs.espressoCore)
}

kapt {
    correctErrorTypes = true
}
