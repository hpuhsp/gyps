plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.hsp.msc"
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

    // 保持 native 库配置
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("libs")
        }
    }
}
kotlin {
    // Kotlin 工具链方式，自动选择并使用 JDK 17
    jvmToolchain(17)
}
dependencies {
    // 本地 JAR 文件（包括 Msc.jar - iFlytek 语音识别 SDK）
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Swallow 核心库（已包含所有必需的 AndroidX 依赖）
    api(project(":swallow"))

    // 注意：AndroidX 核心库已通过 swallow 模块传递依赖，无需重复声明
    // swallow 模块已包含：core-ktx 1.15.0, appcompat 1.7.0, material 1.12.0

    // 测试依赖
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}
