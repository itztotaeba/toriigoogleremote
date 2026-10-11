plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
    // Compiler plugin untuk Jetpack Compose (versi Kotlin harus cocok dengan AGP/Kotlin toolchain)
    alias(libs.plugins.kotlin.compose.compiler)
}

android {
    namespace = "com.toriigoogleremote"
    compileSdk {
        version = release(37)
    }

    buildFeatures {
        // UI dibangun sepenuhnya dengan Jetpack Compose — tidak perlu viewBinding lagi.
        compose = true
    }

    defaultConfig {
        applicationId = "com.toriigoogleremote"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose (versi tiap artefak dikelola oleh BOM)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    debugImplementation(libs.compose.ui.tooling)

    // Firebase (BoM menyesuaikan versi semua library firebase otomatis)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.config)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}