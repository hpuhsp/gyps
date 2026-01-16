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
    // ============================================================
    // Test Dependencies
    // ============================================================
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)

    // ============================================================
    // API Dependencies - 暴露给依赖方（base/app 模块需要直接使用）
    // ============================================================

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

    // Glide - 图片加载实现，通过 ImageLoader 工具类封装
    api(libs.glide)
    ksp(libs.glide.ksp)

    // Network - 网络层实现，通过 Repository 和 BaseRepository 封装
    api(libs.bundles.network)
    api(libs.retrofit.url.manager) {
        exclude(group = "com.squareup.okhttp3")
        exclude(group = "com.squareup.okio")
    }

    // TheRouter - 路由实现，通过路由工具类封装
    api(libs.therouter.router)
    ksp(libs.therouter.apt)

    // Multi-dex - 内部配置，应用层不需要直接访问
    api("androidx.multidex:multidex:2.0.1")

    // Utilities - 工具类（Timber、MMKV），通过封装使用
    api(libs.bundles.utilities)

    // Immersion Bar - 状态栏工具，通过 BaseActivity 封装
    api(libs.bundles.immersionbar)

    // Permissions - 权限工具，通过 BaseActivity/BaseFragment 封装
    api(libs.easypermissions)

    // UtilCodeX - Android 工具类，内部使用
    api(libs.utilcodex) {
        exclude(group = "com.android.support")
    }
}