plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
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
    buildFeatures {
        viewBinding = true
    }
    packagingOptions {
        resources.excludes.add("META-INF/*")
    }


}

dependencies {

    implementation("com.firebaseui:firebase-ui-auth:7.2.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation("com.airbnb.android:lottie:6.4.1")

    implementation ("com.kizitonwose.calendar:view:2.6.0")
    implementation ("com.github.dhaval2404:imagepicker:2.1")


    implementation(libs.generativeai)
    implementation(libs.firebase.auth)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.database)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.airbnb.android:lottie:4.2.2")
    dependencies {
        implementation ("com.google.code.gson:gson:2.10.1")
    }

    implementation("com.github.bumptech.glide:glide:4.15.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.0")

    //corutinse

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // LiveData KTX (for LiveData observables)


    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation ("com.google.mlkit:text-recognition:16.0.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.22.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")




}