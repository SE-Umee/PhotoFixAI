plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.umeetech.photofixai"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.umeetech.photofixai"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }

        // -----------------------------------------------------------------
        // PRODUCTION NOTE:
        // The base URL for the background-removal backend/Firebase Function
        // is injected as a BuildConfig field so no secret keys ever live in
        // the app. Override this per build type / flavor or via a Gradle
        // property (e.g. -Pbackend.url=...) in CI. Never place API secrets
        // here — they must stay on your backend.
        // -----------------------------------------------------------------
        buildConfigField(
            "String",
            "BACKEND_BASE_URL",
            "\"${project.findProperty("backend.url") ?: "https://your-backend.example.com/"}\""
        )
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            // Enable shrinking/obfuscation for production Play Store builds.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core / lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.exifinterface)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Room (local history database)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // DataStore (onboarding + settings preferences)
    implementation(libs.androidx.datastore.preferences)

    // Coil (image loading)
    implementation(libs.coil.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Networking (structure only, for FUTURE production API integration)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)

    // ML Kit on-device segmentation (for LocalSegmentationBackgroundRemovalService)
    implementation(libs.mlkit.selfie.segmentation)

    // -----------------------------------------------------------------------
    // ADMOB (kept commented until monetization is enabled — see AdsManager):
    // implementation("com.google.android.gms:play-services-ads:23.6.0")
    //
    // GOOGLE PLAY BILLING (kept commented until IAP is enabled — see BillingManager):
    // implementation("com.android.billingclient:billing-ktx:7.1.1")
    // -----------------------------------------------------------------------

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.kotlin)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
