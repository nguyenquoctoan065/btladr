plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.appdatdoan"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.appdatdoan"
        minSdk = 24
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
    implementation("de.hdodenhof:circleimageview:3.1.0")
    // Glide để tải và hiển thị hình ảnh từ URL
    implementation ("com.github.bumptech.glide:glide:4.14.2")
    implementation(libs.gridlayout)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.14.2")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}