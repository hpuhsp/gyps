plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.swallow.fly"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true  // 启用 BuildConfig 生成
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.FlowPreview"
        )
    }

    lint {
        checkReleaseBuilds = true
        abortOnError = false
    }
}

kotlin {
    // Kotlin 工具链方式，自动选择并使用 JDK 17
    jvmToolchain(17)
}

dependencies {
    // Unit Testing
    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation("app.cash.turbine:turbine:1.2.0")
    testImplementation("io.mockk:mockk:1.13.13")
    
    // Android Testing
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)

    // AndroidX Core
    api(libs.bundles.androidx.core)

    // Lifecycle
    api(libs.bundles.lifecycle)

    // Navigation
    api(libs.bundles.navigation)

    // Paging
    api(libs.bundles.paging)

    // Coroutines
    api(libs.bundles.coroutines)

    // Hilt
    api(libs.hilt.android)
    ksp(libs.hilt.compiler)

    api(libs.eventbus) {
        exclude(group = "org.jetbrains.kotlin")
    }

    // Room
    api(libs.bundles.room)
    ksp(libs.androidx.room.compiler)

    // Glide
    api(libs.glide)
    ksp(libs.glide.ksp)

    // Network
    api(libs.bundles.network)
    api(libs.retrofit.url.manager) {
        exclude(group = "com.squareup.okhttp3")
        exclude(group = "com.squareup.okio")
    }

    // TheRouter
    api(libs.therouter.router)
    ksp(libs.therouter.apt)

    // Multi-dex
    api("androidx.multidex:multidex:2.0.1")

    // Utilities
    api(libs.bundles.utilities)

    // Immersion Bar
    api(libs.bundles.immersionbar)

    // Permissions
    api(libs.easypermissions)

    // UtilCodeX
    api(libs.utilcodex) {
        exclude(group = "com.android.support")
    }
}