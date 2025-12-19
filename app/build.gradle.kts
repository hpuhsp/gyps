import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    kotlin("kapt")
}

// 加载签名配置
val debugPropFile = file("debugsigning.properties")
val debugProps = if (debugPropFile.exists()) {
    Properties().apply { load(FileInputStream(debugPropFile)) }
} else {
    null
}

android {
    namespace = "com.swallow.gyps"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.swallow.gyps"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()

        versionName = "2.0.1"
        versionCode = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true

        ndk {
            abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a")) // "arm64-v8a", "x86", "mips"
        }
    }

    signingConfigs {
        // 从 debugsigning.properties 加载签名配置
        if (debugProps != null &&
            debugProps.containsKey("STORE_FILE") &&
            debugProps.containsKey("STORE_PASSWORD") &&
            debugProps.containsKey("KEY_ALIAS") &&
            debugProps.containsKey("KEY_PASSWORD")
        ) {
            getByName("debug") {
                storeFile = file(debugProps["STORE_FILE"] as String)
                storePassword = debugProps["STORE_PASSWORD"] as String
                keyAlias = debugProps["KEY_ALIAS"] as String
                keyPassword = debugProps["KEY_PASSWORD"] as String
            }
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            // 如果签名配置存在则使用，否则使用默认签名
            if (debugProps != null) {
                signingConfig = signingConfigs.findByName("debug")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = false
            // Release 也使用 debug 签名（如果存在）
            if (debugProps != null) {
                signingConfig = signingConfigs.findByName("debug")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    lint {
        disable.addAll(listOf("InvalidPackage", "ResourceType"))
        abortOnError = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf("-Xopt-in=kotlin.RequiresOptIn")
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}
kotlin {
    // Kotlin 工具链方式，自动选择并使用 JDK 17
    jvmToolchain(17)
}
hilt {
    enableAggregatingTask = false
}
kapt {
    arguments {
        arg("AROUTER_MODULE_NAME", project.getName())
    }
}
dependencies {
    // 本地 JAR 文件
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // 测试依赖
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)

    // ARouter（使用 kapt）
//    implementation(libs.arouter.api)
    kapt(libs.arouter.compiler)

    // Room（使用 KSP）
    ksp(libs.androidx.room.compiler)

    // Glide（使用 KSP）
//    implementation(libs.glide.okhttp3.integration)
    ksp(libs.glide.ksp)

    // Hilt（使用 kapt，Hilt 不支持 KSP）
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // 项目模块
    implementation(project(":msc"))
    implementation(project(":base"))
}
