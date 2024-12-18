plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services") // Plugin para Firebase
}

android {
    namespace = "com.example.gpsemergency"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.gpsemergency"
        minSdk = 33
        targetSdk = 34
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

    kotlinOptions {
        jvmTarget = "11"
    }

    // Habilita View Binding
    viewBinding {
        var enabled = true
    }
}

dependencies {
    // Firebase BoM (gestiona versiones compatibles de las bibliotecas de Firebase)
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))

    // Firebase Analytics
    implementation("com.google.firebase:firebase-analytics")

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // AndroidX Libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
