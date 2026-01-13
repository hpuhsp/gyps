# 依赖冲突修复实施指南

## 🎯 快速开始

按照以下步骤，逐步解决项目中的 57 个依赖冲突。

---

## 步骤 1：识别问题库

首先，我们需要找出哪些第三方库引入了旧版本依赖。

### 运行诊断命令

```bash
# 查看 app 模块的完整依赖树
./gradlew :app:dependencies --configuration debugRuntimeClasspath > app_dependencies.txt

# 查看 base 模块的依赖树
./gradlew :base:dependencies --configuration debugRuntimeClasspath > base_dependencies.txt

# 查看 swallow 模块的依赖树
./gradlew :swallow:dependencies --configuration debugRuntimeClasspath > swallow_dependencies.txt
```

### 查找问题依赖

```bash
# 查找引入旧版 OkHttp 的库
findstr /C:"okhttp:3" app_dependencies.txt

# 查找引入 Support Library 的库
findstr /C:"com.android.support" app_dependencies.txt

# 查找引入旧版 Kotlin 的库
findstr /C:"kotlin-stdlib:1" app_dependencies.txt
```

---

## 步骤 2：更新 Version Catalog

### 2.1 添加缺失的版本定义

在 `gradle/libs.versions.toml` 的 `[versions]` 部分添加：

```toml
# AndroidX 补充版本
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
androidx-camera = "1.1.0"

# 第三方库版本
okio = "3.6.0"
jetbrains-annotations = "23.0.0"
errorprone = "2.27.0"
```

---

## 步骤 3：修复 swallow 模块

### 3.1 排除问题依赖

编辑 `swallow/build.gradle.kts`：

```kotlin
dependencies {
    // ... 现有依赖 ...
    
    // Retrofit URL Manager - 排除旧版 OkHttp
    api(libs.retrofit.url.manager) {
        exclude(group = "com.squareup.okhttp3")
        exclude(group = "com.squareup.okio")
    }
    
    // ARouter - 可能包含旧版 Kotlin
    api(libs.arouter.api) {
        exclude(group = "org.jetbrains.kotlin")
    }
    
    // UtilCodeX - 可能包含旧依赖
    api(libs.utilcodex) {
        exclude(group = "com.android.support")
    }
    
    // EventBus - 可能包含旧依赖
    api(libs.eventbus) {
        exclude(group = "org.jetbrains.kotlin")
    }
}
```

### 3.2 移除遗留依赖

检查并移除不必要的遗留依赖：

```kotlin
dependencies {
    // 移除或更新这些遗留依赖
    // api("androidx.legacy:legacy-support-v4:1.0.0")  // 考虑移除
    // api("androidx.vectordrawable:vectordrawable:1.1.0")  // 考虑移除
    // api("androidx.viewpager:viewpager:1.0.0")  // 考虑移除
}
```

---

## 步骤 4：修复 base 模块

### 4.1 排除第三方库的旧依赖

编辑 `base/build.gradle.kts`：

```kotlin
dependencies {
    // ... 现有依赖 ...
    
    // PictureSelector - 排除旧依赖
    api(libs.pictureselector) {
        exclude(group = "com.squareup.okhttp3")
        exclude(group = "com.android.support")
        exclude(group = "androidx.exifinterface")  // 使用项目统一版本
    }
    
    // BaseRecyclerViewAdapterHelper - 排除旧依赖
    api(libs.baserecyclerviewadapterhelper) {
        exclude(group = "com.android.support")
        exclude(group = "androidx.recyclerview")  // 使用项目统一版本
    }
    
    // RecyclerView FlexibleDivider - 排除旧依赖
    api(libs.recyclerview.flexibledivider) {
        exclude(group = "com.android.support")
        exclude(group = "androidx.recyclerview")
    }
    
    // FlycoTabLayout - 排除旧依赖
    api(libs.flyco.tablayout) {
        exclude(group = "com.android.support")
    }
    
    // Material Dialogs - 排除旧依赖
    api(libs.material.dialogs.bottomsheets) {
        exclude(group = "org.jetbrains.kotlin")
    }
}
```

---

## 步骤 5：配置根项目

### 5.1 添加全局依赖约束

编辑根目录的 `build.gradle.kts`：

```kotlin
// 在文件末尾添加
subprojects {
    configurations.all {
        resolutionStrategy {
            // 1. 强制使用项目 Kotlin 版本
            force("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
            force("org.jetbrains.kotlin:kotlin-stdlib-common:2.0.21")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.0.21")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21")
            force("org.jetbrains.kotlin:kotlin-android-extensions-runtime:2.0.21")
            
            // 2. 强制使用项目 OkHttp 版本
            force("com.squareup.okhttp3:okhttp:4.12.0")
            force("com.squareup.okhttp3:logging-interceptor:4.12.0")
            force("com.squareup.okio:okio:3.6.0")
            
            // 3. 强制使用项目 Gson 版本
            force("com.google.code.gson:gson:2.11.0")
            
            // 4. 强制使用项目 Coroutines 版本
            force("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
            force("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
            
            // 5. 强制使用项目 Annotations 版本
            force("org.jetbrains:annotations:23.0.0")
            force("androidx.annotation:annotation:1.9.1")
            
            // 6. 禁止使用 Support Library
            eachDependency {
                if (requested.group == "com.android.support") {
                    throw GradleException(
                        "不允许使用 Support Library: ${requested.group}:${requested.name}:${requested.version}\n" +
                        "请使用 AndroidX 替代。如果是第三方库引入的，请在依赖声明中排除它。"
                    )
                }
            }
        }
    }
}
```

### 5.2 添加 Kotlin 配置

在 `gradle.properties` 中添加：

```properties
# 强制使用项目 Kotlin 版本
kotlin.stdlib.default.dependency=true

# 启用 Kotlin 增量编译
kotlin.incremental=true
kotlin.incremental.java=true
```

---

## 步骤 6：验证修复

### 6.1 清理并重新构建

```bash
# 清理所有构建产物
./gradlew clean

# 重新构建项目
./gradlew assembleDebug
```

### 6.2 检查依赖冲突

```bash
# 使用 MCP 工具检测冲突（在 Kiro 中）
检测 app 模块的依赖冲突

# 或使用命令行
./gradlew :app:dependencies --configuration debugRuntimeClasspath | findstr "FAILED"
```

### 6.3 查看依赖树

```bash
# 生成依赖树报告
./gradlew :app:dependencies --configuration debugRuntimeClasspath > dependencies_after_fix.txt

# 对比修复前后的差异
fc app_dependencies.txt dependencies_after_fix.txt
```

---

## 步骤 7：测试验证

### 7.1 运行单元测试

```bash
./gradlew test
```

### 7.2 运行 UI 测试

```bash
./gradlew connectedAndroidTest
```

### 7.3 手动测试清单

- [ ] 应用启动正常
- [ ] 网络请求正常（Retrofit + OkHttp）
- [ ] 图片加载正常（Glide）
- [ ] 数据库操作正常（Room）
- [ ] 导航功能正常（Navigation）
- [ ] 依赖注入正常（Hilt）
- [ ] 第三方 UI 组件正常（PictureSelector、BaseAdapter 等）

---

## 🔍 故障排查

### 问题 1：构建失败，提示找不到某个类

**原因**：排除依赖时可能移除了必要的传递依赖

**解决方案**：
```kotlin
// 显式添加缺失的依赖
implementation("androidx.xxx:xxx:version")
```

### 问题 2：运行时崩溃，ClassNotFoundException

**原因**：版本不兼容或缺少必要的依赖

**解决方案**：
1. 检查 ProGuard 规则
2. 检查是否需要添加 `api` 而不是 `implementation`
3. 查看崩溃日志，确定缺失的类

### 问题 3：某些功能异常

**原因**：强制版本可能导致 API 不兼容

**解决方案**：
1. 检查第三方库的兼容性
2. 考虑升级第三方库到支持新版本的版本
3. 如果无法升级，考虑替换该库

---

## 📊 预期结果

完成所有步骤后，预期：

| 冲突类型 | 修复前 | 修复后 | 改善 |
|---------|--------|--------|------|
| 🔴 主版本冲突 | 7 | 0-2 | 71-100% |
| 🟡 次版本冲突 | 49 | 10-15 | 69-80% |
| 🟢 补丁版本冲突 | 1 | 0-1 | 0-100% |
| **总计** | **57** | **10-18** | **68-82%** |

---

## 🎯 下一步

修复完成后，建议：

1. **文档化**：记录哪些库需要排除依赖，原因是什么
2. **自动化**：在 CI/CD 中添加依赖冲突检测
3. **定期审查**：每次升级依赖时重新检查冲突
4. **监控更新**：关注第三方库的更新，及时升级到支持 AndroidX 的版本

---

## 📞 需要帮助？

如果遇到问题：
1. 查看详细的错误日志
2. 使用 `./gradlew :app:dependencies` 查看依赖树
3. 在 Kiro 中使用 MCP 工具：`检测 app 模块的依赖冲突`
4. 参考 `DEPENDENCY_CONFLICTS_SOLUTION.md` 了解更多细节
