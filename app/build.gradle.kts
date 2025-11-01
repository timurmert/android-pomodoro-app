import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ktlint)
}

android {
    namespace = "com.hydrabon.pomodoro"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.hydrabon.pomodoro"
        minSdk = 24
        targetSdk = 35
        versionCode = rootProject.extra["versionCode"] as Int
        versionName = rootProject.extra["versionName"] as String
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
            isUniversalApk = true
        }
    }

    testOptions {
        animationsDisabled = true
    }
}

val keystorePropsFile = rootProject.file("signing/signing.properties")
val keystoreProps = Properties().apply {
    if (keystorePropsFile.exists()) {
        keystorePropsFile.inputStream().use { load(it) }
    }
}

android {
    signingConfigs {
        create("release") {
            if (keystorePropsFile.exists()) {
                storeFile = file(keystoreProps.getProperty("storeFile"))
                storePassword = keystoreProps.getProperty("storePassword")
                keyAlias = keystoreProps.getProperty("keyAlias")
                keyPassword = keystoreProps.getProperty("keyPassword")
            }
        }
    }
    buildTypes.getByName("release").signingConfig = signingConfigs.getByName("release")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(platform(libs.composeBom))
    implementation(libs.composeRuntime)
    implementation(libs.composeUi)
    implementation(libs.composeUiToolingPreview)
    implementation(libs.composeFoundation)
    implementation(libs.composeMaterial3)
    implementation(libs.composeActivity)
    implementation(libs.composeLifecycle)
    implementation(libs.lifecycleRuntime)
    implementation(libs.lifecycleViewModel)
    implementation(libs.navigationCompose)
    implementation(libs.hiltAndroid)
    implementation(libs.hiltNavigationCompose)
    implementation(libs.coreKtx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.coroutinesAndroid)
    implementation(libs.workRuntime)
    implementation(libs.workHilt)
    implementation(libs.glanceAppWidget)
    implementation(libs.glanceMaterial3)
    implementation(libs.splashscreen)
    implementation(libs.datastorePreferences)

    kapt(libs.hiltCompiler)
    kapt(libs.workCompiler)

    testImplementation(testLibs.junit)
    androidTestImplementation(platform(libs.composeBom))
    androidTestImplementation(testLibs.androidxJunit)
    androidTestImplementation(testLibs.espressoCore)
    androidTestImplementation(testLibs.composeUiTest)
    debugImplementation(libs.composeUiTooling)
    debugImplementation(testLibs.composeUiTestManifest)
}

kapt {
    correctErrorTypes = true
}

androidComponents {
    onVariants(selector().all()) { variant ->
        variant.packaging.resources.excludes.add("META-INF/DEPENDENCIES")
    }
}

val packageUniversal = tasks.register("assembleReleaseUniversalApk") {
    group = "distribution"
    description = "Produces a signed universal release APK for sideloading."
    dependsOn("packageReleaseUniversalApk")
    doLast {
        println("Universal APK: app/build/outputs/apk/release/app-release-universal.apk")
    }
}
