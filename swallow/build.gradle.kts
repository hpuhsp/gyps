plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    kotlin("kapt")
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
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
        release {
            isMinifyEnabled = false
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
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.FlowPreview"
        )
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
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
    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)

    // AndroidX Core Bundle
    api(libs.bundles.androidx.core)

    // Lifecycle Bundle
    api(libs.bundles.lifecycle)

    // Navigation Bundle
    api(libs.bundles.navigation)

    // Paging
    api(libs.bundles.paging)

    // Coroutines Bundle
    api(libs.bundles.coroutines)

    // EventBus
    api(libs.eventbus)

    // Room Bundle
    api(libs.bundles.room)
    ksp(libs.androidx.room.compiler)

    // Glide Bundle
    api(libs.bundles.glide)
    ksp(libs.glide.ksp)

    // Network Bundle
    api(libs.bundles.network)
    api(libs.retrofit.url.manager)

    // Hilt（使用 kapt，Hilt 不支持 KSP）
    api(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Multi-dex
    api("androidx.multidex:multidex:2.0.1")

    api(libs.arouter.api)

    // Utilities Bundle
    api(libs.bundles.utilities)

    // Immersion Bar Bundle
    api(libs.bundles.immersionbar)

    // Permissions
    api(libs.easypermissions)

    // UtilCodeX
    api(libs.utilcodex)

    // Compose (optional support)
    api(platform(libs.compose.bom))
    api(libs.bundles.compose)
    debugApi(libs.bundles.compose.debug)

    // Compose Integration
    api(libs.androidx.lifecycle.viewmodel.compose)
    api(libs.androidx.navigation.compose)
    api(libs.hilt.navigation.compose)

    // Legacy support
    api("androidx.legacy:legacy-support-v4:1.0.0")
    api("androidx.vectordrawable:vectordrawable:1.1.0")
    api("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    api("androidx.viewpager:viewpager:1.0.0")
}