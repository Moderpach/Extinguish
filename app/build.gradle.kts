plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    id(libs.plugins.parcelize.get().pluginId)
}

android {
    namespace = "own.moderpach.extinguish"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
        aidl = true
    }
    defaultConfig {
        applicationId = "own.moderpach.extinguish"
        minSdk = 26
        targetSdk = 35
        versionCode = 33
        versionName = "0.9.4"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    flavorDimensions += "sign"
    productFlavors {
        create("debugSigned") {
            dimension = "sign"
            signingConfig = signingConfigs.getByName("debug")
        }
        create("unsigned") {
            dimension = "sign"
            signingConfig = null
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
    }
    androidResources {
        generateLocaleConfig = true
    }
    dependenciesInfo {
        // Disables dependency metadata when building APKs.
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles.
        includeInBundle = false
    }
    packaging {
        resources.excludes += "/META-INF/AL2.0"
        resources.excludes += "/META-INF/LGPL2.1"
    }
}

dependencies {
    //android basic
    implementation(libs.appcompat)
    implementation(libs.core.ktx)

    //data store
    implementation(libs.datastore.preferences)

    //room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    annotationProcessor(libs.room.compiler)
    ksp(libs.room.compiler)
    testImplementation(libs.room.testing)

    //coroutines
    implementation(platform(libs.coroutines.bom))
    implementation(libs.coroutines.android)

    //serialization
    implementation(libs.serialization.json)

    //lifecycle
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.service)
    implementation(libs.lifecycle.runtime.compose)

    //savedstate
    implementation(libs.savedstate.ktx)

    //compose
    implementation(platform(libs.compose.bom))
    androidTestImplementation(platform(libs.compose.bom))
    //>ui
    implementation(libs.ui)
    implementation(libs.ui.util)
    implementation(libs.ui.graphics)
    implementation(libs.material3)
    //>preview & test
    implementation(libs.ui.tooling.preview)
    debugImplementation(libs.ui.tooling)
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.test.manifest)
    //>activity
    implementation(libs.activity.compose)
    //>window size utils
    implementation(libs.material3.windowSizeClass)
    //>constraintlayout
    implementation(libs.constraintlayout.compose)
    //>navigation
    implementation(libs.navigation.compose)
    androidTestImplementation(libs.navigation.testing)

    //shizuku
    implementation(libs.shizuku.api)
    implementation(libs.shizuku.provider)

    //test
    testImplementation(libs.junit)
    testImplementation(libs.test.core)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.test.core)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.test.rules)
    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.test.ext.truth)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.espresso.contrib)
    androidTestImplementation(libs.espresso.intents)
    androidTestImplementation(libs.espresso.accessibility)
    androidTestImplementation(libs.espresso.web)
    androidTestImplementation(libs.espresso.idling.concurrent)

    //other module
    implementation(project(":hidden-api"))
    implementation(project(":shizuku-service"))
    implementation(project(":ipc"))

}