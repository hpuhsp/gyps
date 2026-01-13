# 依赖冲突解决方案

## 概述

项目中检测到 **57 个依赖冲突**，包括：
- 🔴 **7 个主版本冲突**（严重程度：90/100）
- 🟡 **49 个次版本冲突**（严重程度：60/100）
- 🟢 **1 个补丁版本冲突**（严重程度：30/100）

## 解决策略

我们采用**分层解决策略**，优先使用最优雅的方案：

1. ✅ **Version Catalog 统一管理**（推荐）
2. ✅ **移除传递依赖**
3. ✅ **升级第三方库**
4. ⚠️ **Dependency Constraints**（次选）
5. ❌ **Resolution Strategy**（最后手段）

---

## 🔴 严重冲突（主版本冲突）

### 1. ViewBinding 版本冲突

**问题**：
```
androidx.databinding:viewbinding
请求版本: 3.6.3, 8.7.3
解析版本: 8.7.3
```

**原因**：某些旧的第三方库依赖了旧版本的 ViewBinding

**解决方案**：
```kotlin
// 在 swallow/build.gradle.kts 中
dependencies {
    // 如果发现是某个第三方库引入的旧版本，排除它
    api("com.example:some-library:x.x.x") {
        exclude(group = "androidx.databinding", module = "viewbinding")
    }
}
```

### 2. Kotlin 标准库版本冲突 ⭐ 重要

**问题**：
```
org.jetbrains.kotlin:kotlin-stdlib
请求版本: 1.4.32, 1.5.21, 1.6.0, 1.6.21, 1.7.10, 1.8.10, 1.8.22, 1.9.10, 1.9.22, 1.9.24, 2.0.0, 2.0.21, 2.1.21
解析版本: 2.1.21
```

**原因**：多个第三方库依赖了不同版本的 Kotlin 标准库

**解决方案 1（推荐）**：在 `gradle.properties` 中强制使用项目 Kotlin 版本
```properties
# gradle.properties
kotlin.stdlib.default.dependency=true
```

**解决方案 2**：在根 `build.gradle.kts` 中添加 Dependency Constraints
```kotlin
// build.gradle.kts (root)
subprojects {
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin") {
                useVersion("2.0.21")
                because("统一使用项目 Kotlin 版本")
            }
        }
    }
}
```

### 3. Kotlin Annotations 版本冲突

**问题**：
```
org.jetbrains:annotations
请求版本: 13.0, 20.1.0, 23.0.0
解析版本: 23.0.0
```

**解决方案**：在 `libs.versions.toml` 中添加版本约束
```toml
[versions]
jetbrains-annotations = "23.0.0"

[libraries]
jetbrains-annotations = { group = "org.jetbrains", name = "annotations", version.ref = "jetbrains-annotations" }
```

然后在根 `build.gradle.kts` 中：
```kotlin
subprojects {
    dependencies {
        constraints {
            implementation("org.jetbrains:annotations:23.0.0")
        }
    }
}
```

### 4. OkHttp 版本冲突 ⭐ 重要

**问题**：
```
com.squareup.okhttp3:okhttp
请求版本: 3.10.0, 3.14.9, 4.12.0
解析版本: 4.12.0
```

**原因**：某些旧的第三方库（如 `retrofit-url-manager` 或 `pictureselector`）可能依赖旧版本

**解决方案**：排除旧版本依赖
```kotlin
// swallow/build.gradle.kts
dependencies {
    api(libs.retrofit.url.manager) {
        exclude(group = "com.squareup.okhttp3", module = "okhttp")
    }
}

// base/build.gradle.kts
dependencies {
    api(libs.pictureselector) {
        exclude(group = "com.squareup.okhttp3", module = "okhttp")
    }
}
```

### 5. Android Support Library 冲突 ⚠️ 危险

**问题**：
```
com.android.support:support-v4
请求版本: 25.1.0, 28.0.0
解析版本: 28.0.0
```

**原因**：某些旧库仍在使用 Support Library（已废弃），而项目使用 AndroidX

**解决方案**：
1. 检查哪些库引入了 Support Library：
```bash
./gradlew :app:dependencies | findstr "com.android.support"
```

2. 排除这些依赖：
```kotlin
// 在引入旧库的地方
api("com.example:old-library:x.x.x") {
    exclude(group = "com.android.support")
}
```

3. 如果库无法排除，考虑替换为支持 AndroidX 的版本

---

## 🟡 中等冲突（次版本冲突）

### AndroidX 库版本冲突

大部分次版本冲突来自 AndroidX 库。这些冲突通常不会导致运行时问题，但建议统一管理。

**解决方案**：在 `libs.versions.toml` 中添加缺失的版本定义

```toml
[versions]
# 现有版本...
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
androidx-exifinterface = "1.3.6"
androidx-swiperefreshlayout = "1.1.0"
androidx-emoji2 = "1.3.0"
androidx-camera = "1.1.0-alpha04"

# 第三方库
okio = "3.6.0"
errorprone = "2.27.0"

[libraries]
# 添加这些库的定义（如果需要显式声明）
androidx-annotation = { group = "androidx.annotation", name = "annotation", version.ref = "androidx-annotation" }
# ... 其他库
```

### 关键 AndroidX 库冲突处理

#### Lifecycle 系列（已在 Version Catalog 中统一）
✅ 当前版本：2.8.7（已统一）

#### Core 系列
✅ 当前版本：1.15.0（已统一）

#### Activity & Fragment
✅ 当前版本：Activity 1.9.3, Fragment 1.8.5（已统一）

---

## 🎯 推荐的实施步骤

### 第一步：更新 Version Catalog

在 `gradle/libs.versions.toml` 中添加缺失的版本定义：

```toml
[versions]
# ... 现有版本 ...

# 补充 AndroidX 版本
androidx-annotation = "1.9.1"
androidx-collection = "1.4.2"
androidx-savedstate = "1.2.1"
androidx-emoji2 = "1.3.0"
androidx-camera = "1.1.0"

# 第三方库版本
okio = "3.6.0"
```

### 第二步：排除问题依赖

在 `swallow/build.gradle.kts` 中：

```kotlin
dependencies {
    // ... 现有依赖 ...
    
    // Retrofit URL Manager - 排除旧版 OkHttp
    api(libs.retrofit.url.manager) {
        exclude(group = "com.squareup.okhttp3")
    }
    
    // 如果有其他旧库，也进行排除
}
```

在 `base/build.gradle.kts` 中：

```kotlin
dependencies {
    // ... 现有依赖 ...
    
    // PictureSelector - 可能包含旧依赖
    api(libs.pictureselector) {
        exclude(group = "com.squareup.okhttp3")
        exclude(group = "com.android.support")
    }
    
    // BaseRecyclerViewAdapterHelper
    api(libs.baserecyclerviewadapterhelper) {
        exclude(group = "com.android.support")
    }
    
    // FlycoTabLayout
    api(libs.flyco.tablayout) {
        exclude(group = "com.android.support")
    }
}
```

### 第三步：添加 Dependency Constraints（可选）

在根 `build.gradle.kts` 中添加：

```kotlin
subprojects {
    configurations.all {
        resolutionStrategy {
            // 强制使用项目 Kotlin 版本
            force("org.jetbrains.kotlin:kotlin-stdlib:${libs.versions.kotlin.get()}")
            force("org.jetbrains.kotlin:kotlin-stdlib-common:${libs.versions.kotlin.get()}")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${libs.versions.kotlin.get()}")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${libs.versions.kotlin.get()}")
            
            // 强制使用项目 OkHttp 版本
            force("com.squareup.okhttp3:okhttp:${libs.versions.okhttp.get()}")
            force("com.squareup.okio:okio:3.6.0")
            
            // 强制使用项目 Gson 版本
            force("com.google.code.gson:gson:${libs.versions.gson.get()}")
        }
    }
}
```

### 第四步：验证修复

运行以下命令验证冲突是否解决：

```bash
# 清理构建
./gradlew clean

# 重新检测冲突
./gradlew :app:dependencies --configuration debugRuntimeClasspath > dependencies.txt

# 构建项目
./gradlew assembleDebug
```

---

## 📊 预期结果

实施上述方案后，预期：

1. ✅ **主版本冲突**：从 7 个减少到 0-2 个
2. ✅ **次版本冲突**：从 49 个减少到 10-15 个（可接受范围）
3. ✅ **补丁版本冲突**：可以忽略（Gradle 自动选择最新版本）

---

## 🔍 持续监控

### 定期检查依赖冲突

在 CI/CD 中添加检查：

```bash
# 在 .github/workflows 或 .gitlab-ci.yml 中
./gradlew :app:dependencies --configuration debugRuntimeClasspath | grep "FAILED"
```

### 使用 Gradle 插件

考虑使用以下插件：

1. **Dependency Analysis Plugin**
```kotlin
// build.gradle.kts (root)
plugins {
    id("com.autonomousapps.dependency-analysis") version "1.20.0"
}
```

2. **Versions Plugin**（检查版本更新）
```kotlin
plugins {
    id("com.github.ben-manes.versions") version "0.51.0"
}
```

---

## ⚠️ 注意事项

### 不要过度使用 Resolution Strategy

`resolutionStrategy.force()` 是最后的手段，因为：
- 可能导致运行时不兼容
- 绕过了库的版本约束
- 难以维护

### 优先级顺序

1. **Version Catalog 统一** > 2. **排除依赖** > 3. **Dependency Constraints** > 4. **Resolution Strategy**

### 测试覆盖

修复冲突后，务必进行：
- ✅ 单元测试
- ✅ UI 测试
- ✅ 集成测试
- ✅ 手动回归测试

---

## 📚 参考资源

- [Gradle 依赖管理文档](https://docs.gradle.org/current/userguide/dependency_management.html)
- [AndroidX 迁移指南](https://developer.android.com/jetpack/androidx/migrate)
- [Kotlin 版本兼容性](https://kotlinlang.org/docs/compatibility-guide.html)

---

## 🤝 需要帮助？

如果在实施过程中遇到问题，可以：
1. 查看具体的依赖树：`./gradlew :app:dependencies`
2. 使用 MCP 工具检测冲突：`检测 app 模块的依赖冲突`
3. 查看构建日志中的警告信息
