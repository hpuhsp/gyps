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
    // 本地 JAR 文件
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Swallow 核心库
    api(project(":swallow"))

    // 图片选择库
    api(libs.pictureselector)

    // RecyclerView 相关
    api(libs.baserecyclerviewadapterhelper)
    api(libs.recyclerview.flexibledivider)

    // TabLayout
    api(libs.flyco.tablayout)

    // Material Dialogs
    api(libs.material.dialogs.bottomsheets)

    // 测试依赖
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}
