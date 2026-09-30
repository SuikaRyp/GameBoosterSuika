plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.devtools.ksp)
}

android {
    namespace = "com.example"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example"
        minSdk = 24
        targetSdk = 34
        versionCode = (findProperty("VERSION_CODE") as String?)?.toIntOrNull() ?: 2
        versionName = (findProperty("VERSION_NAME") as String?) ?: "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // Signing release: dibaca dari environment (GitHub Secrets) atau gradle.properties lokal.
    // Kalau tidak diisi, release ditandatangani dengan debug key supaya APK tetap bisa dipasang.
    val releaseStoreFile: String? = System.getenv("KEYSTORE_FILE") ?: (findProperty("KEYSTORE_FILE") as String?)
    val releaseStorePassword: String? = System.getenv("KEYSTORE_PASSWORD") ?: (findProperty("KEYSTORE_PASSWORD") as String?)
    val releaseKeyAlias: String? = System.getenv("KEY_ALIAS") ?: (findProperty("KEY_ALIAS") as String?)
    val releaseKeyPassword: String? = System.getenv("KEY_PASSWORD") ?: (findProperty("KEY_PASSWORD") as String?)
    val hasReleaseKeystore = !releaseStoreFile.isNullOrBlank() &&
        file(releaseStoreFile).exists() &&
        !releaseStorePassword.isNullOrBlank() &&
        !releaseKeyAlias.isNullOrBlank() &&
        !releaseKeyPassword.isNullOrBlank()

    signingConfigs {
        if (hasReleaseKeystore) {
            create("release") {
                storeFile = file(releaseStoreFile!!)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            signingConfig = if (hasReleaseKeystore) signingConfigs.getByName("release")
            else signingConfigs.getByName("debug")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    lint {
        // Build release di CI tidak boleh gagal hanya karena peringatan lint.
        abortOnError = false
        checkReleaseBuilds = false
    }
    buildFeatures {
        compose = true
        aidl = true
    }
    testOptions {
        // Tests JVM puros: android.util.Log → no-op en vez de "not mocked".
        // (Los asserts de F4 usan el logger inyectado, no android.util.Log.)
        unitTests.isReturnDefaultValues = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.process)
    
    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Shizuku
    implementation(libs.shizuku.api)
    implementation(libs.shizuku.provider)

    testImplementation(libs.junit)
    // Infra mínima para que la suite de tests compile (tests plantilla preexistentes
    // las requerían y no estaban declaradas — F3B/Parte 9)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.ui.test.junit4)
    // org.json real para tests JVM (el android.jar lo tiene stub — F3B/Parte 9)
    testImplementation(libs.json)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
