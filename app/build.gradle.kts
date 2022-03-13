plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    compileSdk = ProjectConfig.compileSdk
    defaultConfig {
        applicationId = ProjectConfig.appId
        minSdk = ProjectConfig.minSdk
        targetSdk = ProjectConfig.targetSdk
        versionCode = ProjectConfig.versionCode
        versionName = ProjectConfig.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        multiDexEnabled = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "$project.rootDir/tools/proguard-rules.pro")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Compose.version
    }
    packagingOptions {
        jniLibs.excludes.add("META-INF/{AL2.0,LGPL2.1}")
    }

//    configurations {
//        implementation.get().exclude(mapOf("group" to "org.jetbrains", "module" to "annotations"))
//    }
}

dependencies {

    implementation(AndroidX.coreKtx)
    implementation(AndroidX.appCompat)
    implementation(Google.material)

    implementation(Compose.compiler)
    implementation(Compose.ui)
    implementation(Compose.material)
    implementation(Compose.uiToolingPreview)
    implementation(Compose.uiUtil)
    implementation(Compose.activityCompose)
    implementation(Compose.constraintLayoutCompose)
    implementation(Compose.navigation)
    implementation(Compose.hiltNavigationCompose)
    implementation(Compose.viewModelCompose)

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.1.1")

    debugImplementation(Compose.uiTooling)
    debugImplementation(LeakCanary.version)

    implementation(Google.playCore)
    implementation(Google.playCoreKtx)

    implementation(DaggerHilt.hiltAndroid)
    kapt(DaggerHilt.hiltCompiler)
    kapt(DaggerHilt.hiltAndroid)

    implementation(project(Modules.core))
    implementation(project(Modules.coreUi))
//    implementation(project(Modules.accountDomain))
//    implementation(project(Modules.accountPresentation))
//    implementation(project(Modules.audioData))
//    implementation(project(Modules.audioDomain))
//    implementation(project(Modules.audioPresentation))

    implementation(Coroutines.coroutines)
    implementation(Coroutines.coroutinesAndroid)

    implementation(ExoPlayer.exoPlayer)
    implementation(ExoPlayer.mediaSession)
    implementation(ExoPlayer.cast)

    implementation(WorkManager.runtime)
    implementation(WorkManager.test)

    implementation(Coil.compose)

    implementation(Accompanist.pager)
    implementation(Accompanist.pagerIndicators)
    implementation(Accompanist.navigationMaterial)
    implementation(Accompanist.systemUiController)
    implementation(Accompanist.insets)

    implementation(Media.version)

    implementation(Datastore.version)

    kapt(Room.compiler)
    implementation(Room.runtime)
    annotationProcessor(Room.compiler)
    implementation(Room.ktx)
    implementation(Room.paging)
    //testImplementation("androidx.room:room-testing:$room_version")

    implementation(KtorClient.core)
    implementation(KtorClient.cio)
    implementation(KtorClient.serialization)
    implementation(KtorClient.logging)
    implementation(KtorClient.logback)

    // Support for Java 8 features
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    implementation(Amplify.core)
    implementation(Amplify.coreKtx)
    implementation(Amplify.sdkMobileClient)
    implementation(Amplify.sdkAuthUserpools)
    implementation(Amplify.sdkAuthUi)
    implementation(Amplify.sdkCognito)
    implementation(Amplify.authCognito)

    implementation(Palette.version)
}