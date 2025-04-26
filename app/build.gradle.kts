plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.softec.lifeaiassistant"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.softec.lifeaiassistant"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures{
        viewBinding=true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.airbnb.android:lottie:4.2.2")
    implementation ("com.google.code.gson:gson:2.8.8")

    implementation ("com.github.bumptech.glide:glide:4.15.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.0")

    //corutinse

    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // ViewModel KTX (for viewModels() delegate)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // LiveData KTX (for LiveData observables)
    implementation(libs.androidx.lifecycle.livedata.ktx)


}