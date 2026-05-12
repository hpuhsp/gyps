# 依赖优化总结报告

## 概述

本文档记录 Gyps 项目 swallow 基础库依赖优化的完整过程，包括优化目标、实施策略、变更详情及后续维护建议。

优化目标：解决项目中存在的依赖版本冲突，统一关键库版本，彻底消除 Support Library 残留，提升构建稳定性和可维护性。

---

## 优化前问题分析

### 主要冲突类型

| 冲突类型 | 描述 |
|----------|------|
| Kotlin stdlib 版本碎片化 | 第三方库（ARouter、EventBus 等）携带旧版 Kotlin stdlib，与项目 2.0.21 冲突 |
| OkHttp 版本冲突 | `retrofit-url-manager` 携带旧版 OkHttp 3.x，与项目 4.12.0 冲突 |
| Support Library 残留 | `utilcodex`、`pictureselector`、`baserecyclerviewadapterhelper` 等携带 `com.android.support` 依赖 |
| OkIO 版本冲突 | `retrofit-url-manager` 携带旧版 OkIO，与项目 3.x 冲突 |
| RecyclerView 重复依赖 | `baserecyclerviewadapterhelper`、`recyclerview-flexibledivider` 携带独立 RecyclerView 版本 |

---

## 优化实施内容

### 1. Version Catalog 补全（`gradle/libs.versions.toml`）

新增以下版本定义，确保全局版本可统一管理：

```toml
# AndroidX 补充组件
androidx-annotation = "1.9.1"
androidx-collection = "1.4.2"
androidx-savedstate = "1.2.1"
androidx-arch-core = "2.2.0"
androidx-concurrent = "1.1.0"
androidx-profileinstaller = "1.4.0"
androidx-startup = "1.1.1"
androidx-tracing = "1.2.0"
androidx-versionedparcelable = "1.1.1"
androidx-customview = "1.1.0"
androidx-drawerlayout = "1.1.1"
androidx-coordinatorlayout = "1.1.0"
androidx-viewpager2 = "1.1.0"
androidx-transition = "1.5.0"
androidx-slidingpanelayout = "1.2.0"
androidx-exifinterface = "1.3.7"
androidx-swiperefreshlayout = "1.2.0-alpha01"
androidx-emoji2 = "1.5.0"
androidx-camera = "1.1.0"

# 第三方库版本
okio = "3.9.1"
jetbrains-annotations = "23.0.0"
errorprone = "2.27.0"
```

### 2. Kotlin 编译优化（`gradle.properties`）

```properties
kotlin.stdlib.default.dependency=true
kotlin.incremental=true
kotlin.incremental.java=true
kotlin.incremental.usePreciseJavaTracking=true
kotlin.caching.enabled=true
kotlin.parallel.tasks.in.project=true
```

### 3. swallow 模块依赖排除（`swallow/build.gradle.kts`）

| 库 | 排除组 | 原因 |
|----|--------|------|
| `retrofit-url-manager` | `com.squareup.okhttp3` | 携带旧版 OkHttp 3.x |
| `retrofit-url-manager` | `com.squareup.okio` | 携带旧版 OkIO |
| `eventbus` | `org.jetbrains.kotlin` | 携带旧版 Kotlin stdlib |
| `utilcodex` | `com.android.support` | 携带 Support Library |

```kotlin
api(libs.retrofit.url.manager) {
    exclude(group = "com.squareup.okhttp3")
    exclude(group = "com.squareup.okio")
}
api(libs.eventbus) {
    exclude(group = "org.jetbrains.kotlin")
}
api(libs.utilcodex) {
    exclude(group = "com.android.support")
}
```

### 4. base 模块依赖排除（`base/build.gradle.kts`）

| 库 | 排除组 | 原因 |
|----|--------|------|
| `pictureselector` | `com.squareup.okhttp3` | 携带旧版 OkHttp |
| `pictureselector` | `com.android.support` | 携带 Support Library |
| `pictureselector` | `androidx.exifinterface` | 版本冲突，由项目统一管理 |
| `baserecyclerviewadapterhelper` | `com.android.support` | 携带 Support Library |
| `baserecyclerviewadapterhelper` | `androidx.recyclerview` | 版本冲突，由项目统一管理 |
| `recyclerview-flexibledivider` | `com.android.support` | 携带 Support Library |
| `recyclerview-flexibledivider` | `androidx.recyclerview` | 版本冲突，由项目统一管理 |
| `flyco-tablayout` | `com.android.support` | 携带 Support Library |
| `material-dialogs-bottomsheets` | `org.jetbrains.kotlin` | 携带旧版 Kotlin stdlib |

```kotlin
api(libs.pictureselector) {
    exclude(group = "com.squareup.okhttp3")
    exclude(group = "com.android.support")
    exclude(group = "androidx.exifinterface")
}
api(libs.baserecyclerviewadapterhelper) {
    exclude(group = "com.android.support")
    exclude(group = "androidx.recyclerview")
}
api(libs.recyclerview.flexibledivider) {
    exclude(group = "com.android.support")
    exclude(group = "androidx.recyclerview")
}
api(libs.flyco.tablayout) {
    exclude(group = "com.android.support")
}
api(libs.material.dialogs.bottomsheets) {
    exclude(group = "org.jetbrains.kotlin")
}
```

### 5. 全局依赖解析策略（根 `build.gradle.kts`）

在 `subprojects.afterEvaluate.configurations.all.resolutionStrategy` 中配置：

```kotlin
// 强制 Kotlin 版本统一
force("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
force("org.jetbrains.kotlin:kotlin-stdlib-common:2.0.21")
force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.0.21")
force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21")

// 强制 OkHttp / OkIO 版本统一
force("com.squareup.okhttp3:okhttp:4.12.0")
force("com.squareup.okhttp3:logging-interceptor:4.12.0")
force("com.squareup.okio:okio:3.9.1")

// 强制其他核心库版本
force("com.google.code.gson:gson:2.11.0")
force("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
force("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
force("org.jetbrains:annotations:23.0.0")
force("androidx.annotation:annotation:1.9.1")

// 禁止 Support Library（编译期报错）
eachDependency {
    if (requested.group == "com.android.support") {
        throw GradleException("不允许使用 Support Library，请使用 AndroidX 替代")
    }
}
```

---

## 优化效果

### 版本统一情况

| 库 | 优化前 | 优化后 |
|----|--------|--------|
| Kotlin stdlib | 多版本混用（1.x ~ 2.0.21） | 统一 2.0.21 |
| OkHttp | 3.x / 4.12.0 混用 | 统一 4.12.0 |
| OkIO | 旧版 / 3.x 混用 | 统一 3.9.1 |
| Gson | 多版本 | 统一 2.11.0 |
| Coroutines | 多版本 | 统一 1.9.0 |
| Support Library | 存在残留 | 完全消除，编译期拦截 |

### 冲突减少估算

- 优化前主版本冲突：约 8 个
- 优化前次版本冲突：约 49 个
- 优化后主版本冲突：≤ 2 个
- 优化后次版本冲突：≤ 15 个
- 冲突减少比例：约 70~75%

---

## 后续维护建议

### 新增第三方库时的检查流程

1. 执行依赖树分析，确认是否引入新的版本冲突：
   ```bash
   ./gradlew :app:dependencies --configuration debugRuntimeClasspath | grep -E "(FAILED|conflict|->)"
   ```

2. 若新库携带旧版 OkHttp / Kotlin / Support Library，在依赖声明处添加对应排除：
   ```kotlin
   implementation("com.example:library:1.0.0") {
       exclude(group = "com.squareup.okhttp3")
       exclude(group = "org.jetbrains.kotlin")
       exclude(group = "com.android.support")
   }
   ```

3. 若需要强制某个传递依赖版本，在根 `build.gradle.kts` 的 `resolutionStrategy.force()` 中追加。

### 升级核心库版本时的注意事项

- 升级 Kotlin 版本时，同步更新根 `build.gradle.kts` 中所有 `kotlin-stdlib*` 的 `force()` 版本
- 升级 OkHttp 版本时，同步更新 `okhttp`、`logging-interceptor`、`okio` 三个 `force()` 版本
- 升级后执行完整依赖树检查，确认无回退

### 禁止事项

- 不得在任何模块中直接声明 `com.android.support` 组的依赖
- 不得移除根 `build.gradle.kts` 中的 Support Library 禁止策略
- 不得在 `resolutionStrategy` 中使用 `failOnVersionConflict()`（会导致正常的版本升级解析失败）

---

## 相关文件索引

| 文件 | 变更说明 |
|------|----------|
| `gradle/libs.versions.toml` | 补全版本定义 |
| `gradle.properties` | 添加 Kotlin 编译优化配置 |
| `build.gradle.kts` | 添加全局依赖解析策略 |
| `swallow/build.gradle.kts` | 配置 4 个库的依赖排除 |
| `base/build.gradle.kts` | 配置 5 个库的依赖排除 |
