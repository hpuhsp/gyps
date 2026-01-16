plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    kotlin("kapt")
}

android {
    namespace = "com.hsp.resource"
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
            isMinifyEnabled = true
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
    }

    buildFeatures {
        viewBinding = true
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
    // API Dependencies - 暴露给 app 模块使用
    // ============================================================

    // 本地 JAR 文件 - 如果 app 需要使用，则暴露
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Swallow 核心库 - 必须暴露，app 需要使用 BaseActivity/BaseViewModel 等
    api(project(":swallow"))

    // 图片选择库 - UI 组件，app 模块会直接使用
    api(libs.pictureselector) {
        exclude(group = "com.squareup.okhttp3")
        exclude(group = "com.android.support")
        exclude(group = "androidx.exifinterface")
    }

    // RecyclerView Adapter - app 模块会继承这些 Adapter
    api(libs.baserecyclerviewadapterhelper) {
        exclude(group = "com.android.support")
        exclude(group = "androidx.recyclerview")
    }

    // RecyclerView Divider - app 模块会直接使用
    api(libs.recyclerview.flexibledivider) {
        exclude(group = "com.android.support")
        exclude(group = "androidx.recyclerview")
    }

    // TabLayout - app 模块会直接使用
    api(libs.flyco.tablayout) {
        exclude(group = "com.android.support")
    }

    // Material Dialogs - app 模块会直接使用
    api(libs.material.dialogs.bottomsheets) {
        exclude(group = "org.jetbrains.kotlin")
    }

    // ============================================================
    // Implementation Dependencies - base 模块内部使用
    // ============================================================

    // Legacy support - 内部兼容性支持，app 不需要直接访问
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.viewpager:viewpager:1.0.0")
}
