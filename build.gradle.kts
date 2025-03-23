plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // ✅ Firebase Google Services plugin
}

android {
    namespace = "com.example.quizapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.quizapp"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")

    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // ✅ Firebase BoM (Recommended)
    implementation(platform("com.google.firebase:firebase-bom:33.11.0"))

    // ✅ Firebase Dependencies (No need to specify versions)
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
}
