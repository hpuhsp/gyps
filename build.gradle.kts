// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

//// 全局依赖解析策略
//subprojects {
//    afterEvaluate {
//        configurations.all {
//            resolutionStrategy {
//                // 辅助函数：从 Version Catalog 获取版本
//                fun version(key: String): String {
//                    return rootProject.extensions
//                        .getByType<VersionCatalogsExtension>()
//                        .named("libs")
//                        .findVersion(key)
//                        .get()
//                        .toString()
//                }
//
//                // 1. 强制使用项目 Kotlin 版本
//                val kotlin = version("kotlin")
//                force("org.jetbrains.kotlin:kotlin-stdlib:$kotlin")
//                force("org.jetbrains.kotlin:kotlin-stdlib-common:$kotlin")
//                force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin")
//                force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin")
//
//                // 2. 强制使用项目 OkHttp 和 OkIO 版本
//                val okhttp = version("okhttp")
//                val okio = version("okio")
//                force("com.squareup.okhttp3:okhttp:$okhttp")
//                force("com.squareup.okhttp3:logging-interceptor:$okhttp")
//                force("com.squareup.okio:okio:$okio")
//
//                // 3. 强制使用项目 Gson 版本
//                force("com.google.code.gson:gson:${version("gson")}")
//
//                // 4. 强制使用项目 Coroutines 版本
//                val coroutines = version("coroutines")
//                force("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines")
//                force("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines")
//
//                // 5. 强制使用项目 Annotations 版本
//                force("org.jetbrains:annotations:${version("jetbrains-annotations")}")
//                force("androidx.annotation:annotation:${version("androidx-annotation")}")
//
//                // 6. 禁止使用 Support Library
//                eachDependency {
//                    if (requested.group == "com.android.support") {
//                        throw GradleException(
//                            """
//                            |
//                            |❌ 不允许使用 Support Library: ${requested.group}:${requested.name}:${requested.version}
//                            |
//                            |请使用 AndroidX 替代。如果是第三方库引入的，请在依赖声明中排除它：
//                            |
//                            |implementation("library:name:version") {
//                            |    exclude(group = "com.android.support")
//                            |}
//                            |
//                            """.trimMargin()
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
