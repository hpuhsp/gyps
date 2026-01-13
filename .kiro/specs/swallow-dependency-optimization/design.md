# Design Document

## Overview

本设计文档描述了如何系统化地解决 Gyps 项目中 swallow 基础库的 57 个依赖冲突。我们采用分层解决策略，优先使用最优雅的方案：Version Catalog 统一管理 > 排除传递依赖 > 全局依赖约束 > Resolution Strategy。

设计目标：
- 将主版本冲突从 7 个减少到 0-2 个
- 将次版本冲突从 49 个减少到 10-15 个
- 完全消除 Support Library 依赖
- 确保所有功能正常工作
- 提供清晰的维护文档

## Architecture

### 依赖管理架构

```
┌─────────────────────────────────────────────────────────────┐
│                    Root Project                              │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  Resolution Strategy (全局依赖强制策略)                │  │
│  │  - 强制 Kotlin 版本                                    │  │
│  │  - 强制 OkHttp/OkIO 版本                              │  │
│  │  - 禁止 Support Library                               │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│              Version Catalog (libs.versions.toml)            │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  统一版本定义                                          │  │
│  │  - AndroidX 库版本                                     │  │
│  │  - 第三方库版本                                        │  │
│  │  - Kotlin/OkHttp/Gson 等核心库版本                    │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            │
            ┌───────────────┼───────────────┐
            ▼               ▼               ▼
    ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
    │   swallow    │ │     base     │ │     app      │
    │   模块       │ │    模块      │ │    模块      │
    │              │ │              │ │              │
    │  排除策略:   │ │  排除策略:   │ │              │
    │  - OkHttp    │ │  - OkHttp    │ │              │
    │  - Kotlin    │ │  - Support   │ │              │
    │  - Support   │ │  - Kotlin    │ │              │
    └──────────────┘ └──────────────┘ └──────────────┘
```

### 解决策略优先级

1. **Version Catalog 统一管理**（最优）
   - 在 libs.versions.toml 中定义所有版本
   - 通过 bundles 组织相关依赖
   - 易于维护和升级

2. **排除传递依赖**（推荐）
   - 在依赖声明时使用 exclude
   - 精确控制每个依赖的传递关系
   - 不影响其他模块

3. **全局依赖约束**（次选）
   - 在根项目中使用 Resolution Strategy
   - 作为最后的防线
   - 可能影响所有子项目

## Components and Interfaces

### 1. Version Catalog 配置组件

**文件**: `gradle/libs.versions.toml`

**职责**: 集中管理所有依赖版本

**新增版本定义**:
```toml
[versions]
# 现有版本...

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

### 2. Swallow 模块依赖配置组件

**文件**: `swallow/build.gradle.kts`

**职责**: 配置 swallow 模块的依赖，排除问题传递依赖

**排除策略**:
```kotlin
dependencies {
    // Retrofit URL Manager - 排除旧版 OkHttp
    api(libs.retrofit.url.manager) {
        exclude(group = "com.squareup.okhttp3")
        exclude(group = "com.squareup.okio")
    }
    
    // ARouter - 排除旧版 Kotlin
    api(libs.arouter.api) {
        exclude(group = "org.jetbrains.kotlin")
    }
    
    // UtilCodeX - 排除 Support Library
    api(libs.utilcodex) {
        exclude(group = "com.android.support")
    }
    
    // EventBus - 排除旧版 Kotlin
    api(libs.eventbus) {
        exclude(group = "org.jetbrains.kotlin")
    }
}
```

**遗留依赖评估**:
- `androidx.legacy:legacy-support-v4:1.0.0` - 评估后决定是否移除
- `androidx.vectordrawable:vectordrawable:1.1.0` - 评估后决定是否移除
- `androidx.viewpager:viewpager:1.0.0` - 评估后决定是否移除

### 3. Base 模块依赖配置组件

**文件**: `base/build.gradle.kts`

**职责**: 配置 base 模块的依赖，排除第三方 UI 库的问题传递依赖

**排除策略**:
```kotlin
dependencies {
    // PictureSelector - 排除旧依赖
    api(libs.pictureselector) {
        exclude(group = "com.squareup.okhttp3")
        exclude(group = "com.android.support")
        exclude(group = "androidx.exifinterface")
    }
    
    // BaseRecyclerViewAdapterHelper - 排除旧依赖
    api(libs.baserecyclerviewadapterhelper) {
        exclude(group = "com.android.support")
        exclude(group = "androidx.recyclerview")
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
    
    // Material Dialogs - 排除旧版 Kotlin
    api(libs.material.dialogs.bottomsheets) {
        exclude(group = "org.jetbrains.kotlin")
    }
}
```

### 4. 全局依赖解析策略组件

**文件**: `build.gradle.kts` (根项目)

**职责**: 配置全局依赖解析策略，作为最后的防线

**策略配置**:
```kotlin
subprojects {
    configurations.all {
        resolutionStrategy {
            // 1. 强制使用项目 Kotlin 版本
            force("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
            force("org.jetbrains.kotlin:kotlin-stdlib-common:2.0.21")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.0.21")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21")
            
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

### 5. Kotlin 配置组件

**文件**: `gradle.properties`

**职责**: 配置 Kotlin 编译选项

**配置项**:
```properties
# 强制使用项目 Kotlin 版本
kotlin.stdlib.default.dependency=true

# 启用 Kotlin 增量编译
kotlin.incremental=true
kotlin.incremental.java=true
```

### 6. 依赖验证组件

**职责**: 验证依赖优化效果

**验证步骤**:
1. 清理构建产物
2. 重新构建项目
3. 检测依赖冲突
4. 生成依赖树报告
5. 对比优化前后差异

**验证命令**:
```bash
# 清理
./gradlew clean

# 构建
./gradlew assembleDebug

# 检测冲突（使用 MCP 工具）
检测 app 模块的依赖冲突

# 生成依赖树
./gradlew :app:dependencies --configuration debugRuntimeClasspath > dependencies_after.txt
```

### 7. 功能测试组件

**职责**: 验证项目功能完整性

**测试清单**:
- [ ] 应用启动正常
- [ ] 网络请求正常（Retrofit + OkHttp）
- [ ] 图片加载正常（Glide）
- [ ] 数据库操作正常（Room）
- [ ] 导航功能正常（Navigation）
- [ ] 依赖注入正常（Hilt）
- [ ] 第三方 UI 组件正常（PictureSelector、BaseAdapter 等）

**测试命令**:
```bash
# 单元测试
./gradlew test

# UI 测试
./gradlew connectedAndroidTest
```

## Data Models

### 依赖冲突数据模型

```kotlin
data class DependencyConflict(
    val library: String,              // 库名称，如 "org.jetbrains.kotlin:kotlin-stdlib"
    val requestedVersions: List<String>, // 请求的版本列表
    val resolvedVersion: String,      // 解析后的版本
    val conflictType: ConflictType,   // 冲突类型
    val severity: Int                 // 严重程度 (0-100)
)

enum class ConflictType {
    MAJOR,    // 主版本冲突
    MINOR,    // 次版本冲突
    PATCH     // 补丁版本冲突
}
```

### 优化结果数据模型

```kotlin
data class OptimizationResult(
    val beforeConflicts: ConflictSummary,  // 优化前的冲突统计
    val afterConflicts: ConflictSummary,   // 优化后的冲突统计
    val improvement: Double,               // 改善百分比
    val testResults: TestResults           // 测试结果
)

data class ConflictSummary(
    val majorConflicts: Int,    // 主版本冲突数
    val minorConflicts: Int,    // 次版本冲突数
    val patchConflicts: Int,    // 补丁版本冲突数
    val totalConflicts: Int     // 总冲突数
)

data class TestResults(
    val buildSuccess: Boolean,        // 构建是否成功
    val unitTestsPassed: Boolean,     // 单元测试是否通过
    val uiTestsPassed: Boolean,       // UI 测试是否通过
    val functionalTests: Map<String, Boolean>  // 功能测试结果
)
```

### 依赖排除配置模型

```kotlin
data class DependencyExclusion(
    val library: String,              // 要配置的库
    val excludeGroups: List<String>,  // 要排除的组
    val excludeModules: List<String>, // 要排除的模块
    val reason: String                // 排除原因
)

// 示例
val exclusions = listOf(
    DependencyExclusion(
        library = "me.jessyan:retrofit-url-manager",
        excludeGroups = listOf("com.squareup.okhttp3", "com.squareup.okio"),
        excludeModules = emptyList(),
        reason = "引入了旧版 OkHttp 3.x，与项目使用的 4.12.0 冲突"
    ),
    DependencyExclusion(
        library = "com.alibaba:arouter-api",
        excludeGroups = listOf("org.jetbrains.kotlin"),
        excludeModules = emptyList(),
        reason = "引入了旧版 Kotlin 标准库，与项目使用的 2.0.21 冲突"
    )
)
```

## Correctness Properties

*属性是一个特征或行为，应该在系统的所有有效执行中保持为真——本质上是关于系统应该做什么的正式陈述。属性作为人类可读规范和机器可验证正确性保证之间的桥梁。*


### Property 1: Version Catalog 完整性

*For any* 必需的 AndroidX 库版本键（annotation, collection, savedstate 等），libs.versions.toml 文件应该包含该版本定义

**Validates: Requirements 1.1**

### Property 2: Kotlin 版本统一性

*For any* Kotlin 相关依赖（kotlin-stdlib, kotlin-stdlib-common, kotlin-stdlib-jdk7, kotlin-stdlib-jdk8），依赖树中解析的版本应该都是 2.0.21

**Validates: Requirements 2.1, 2.4**

### Property 3: OkHttp 版本统一性

*For any* OkHttp 相关依赖（okhttp, logging-interceptor），依赖树中解析的版本应该都是 4.12.0，且不应存在任何 3.x 版本

**Validates: Requirements 3.1, 3.3**

### Property 4: Support Library 完全消除

*For any* 依赖项，依赖树中不应包含任何 com.android.support 组的依赖

**Validates: Requirements 4.1, 4.4**

### Property 5: 依赖排除配置正确性

*For any* 需要排除传递依赖的库（retrofit-url-manager, arouter-api, utilcodex, eventbus, pictureselector, baserecyclerviewadapterhelper, recyclerview-flexibledivider, flyco-tablayout, material-dialogs-bottomsheets），对应的 build.gradle.kts 文件应该包含正确的 exclude 配置

**Validates: Requirements 5.1, 5.2, 5.3, 5.4, 6.1, 6.2, 6.3, 6.4, 6.5**

### Property 6: Resolution Strategy 配置完整性

*For any* 需要强制版本的核心库（Kotlin, OkHttp, OkIO, Gson, Coroutines, Annotations），根项目的 build.gradle.kts 应该包含相应的 force 配置

**Validates: Requirements 7.1, 7.2, 7.3, 7.4**

### Property 7: 全局配置作用域

*For any* 子项目（swallow, base, app），Resolution Strategy 配置应该在该项目的依赖解析中生效

**Validates: Requirements 7.6**

### Property 8: 冲突减少效果

*For any* 优化操作，优化后的总冲突数应该比优化前减少 68-82%

**Validates: Requirements 9.5**

## Error Handling

### 构建错误处理

**Support Library 检测错误**:
- 当检测到 Support Library 依赖时，Resolution Strategy 应该抛出 GradleException
- 错误消息应该清晰指出哪个库引入了 Support Library
- 错误消息应该提供解决方案（使用 AndroidX 或排除依赖）

**版本冲突错误**:
- 当无法解析依赖版本时，Gradle 会报告冲突
- 应该通过依赖树分析识别冲突源
- 应该通过排除或强制版本解决冲突

### 配置错误处理

**TOML 解析错误**:
- 当 libs.versions.toml 格式错误时，Gradle 会报告解析错误
- 应该确保 TOML 语法正确
- 应该确保版本号格式正确

**依赖声明错误**:
- 当依赖声明语法错误时，Gradle 会报告错误
- 应该确保 exclude 配置语法正确
- 应该确保库名称和组名称正确

### 测试错误处理

**构建失败**:
- 当构建失败时，应该检查错误日志
- 应该识别是依赖冲突还是代码错误
- 应该根据错误类型采取相应的修复措施

**测试失败**:
- 当测试失败时，应该检查是否是依赖变更导致的
- 应该验证依赖版本是否兼容
- 应该检查是否需要更新测试代码

## Testing Strategy

### 测试方法

本项目采用**双重测试方法**：

1. **单元测试（Unit Tests）**
   - 验证特定的配置示例
   - 验证边缘情况和错误条件
   - 验证构建脚本的正确性

2. **属性测试（Property-Based Tests）**
   - 验证通用属性在所有输入下成立
   - 通过随机化提供全面的输入覆盖
   - 验证依赖解析的正确性

两种测试方法是互补的，共同提供全面的覆盖：
- 单元测试捕获具体的错误
- 属性测试验证通用的正确性

### 配置文件验证测试

**测试目标**: 验证配置文件的正确性

**测试方法**: 单元测试

**测试用例**:
```kotlin
@Test
fun `libs versions toml should contain all required AndroidX versions`() {
    val toml = parseToml("gradle/libs.versions.toml")
    val requiredVersions = listOf(
        "androidx-annotation",
        "androidx-collection",
        "androidx-savedstate",
        // ... 其他必需版本
    )
    requiredVersions.forEach { version ->
        assertTrue(toml.versions.containsKey(version))
    }
}

@Test
fun `gradle properties should enable kotlin stdlib default dependency`() {
    val properties = loadProperties("gradle.properties")
    assertEquals("true", properties["kotlin.stdlib.default.dependency"])
}

@Test
fun `root build gradle should contain resolution strategy for kotlin`() {
    val buildScript = readFile("build.gradle.kts")
    assertTrue(buildScript.contains("force(\"org.jetbrains.kotlin:kotlin-stdlib:2.0.21\")"))
}
```

### 依赖解析验证测试

**测试目标**: 验证依赖解析的正确性

**测试方法**: 属性测试（通过分析依赖树）

**测试用例**:
```kotlin
@Test
fun `all kotlin dependencies should resolve to version 2_0_21`() {
    val dependencyTree = analyzeDependencyTree(":app", "debugRuntimeClasspath")
    val kotlinDeps = dependencyTree.filter { it.group == "org.jetbrains.kotlin" }
    
    kotlinDeps.forEach { dep ->
        assertEquals("2.0.21", dep.resolvedVersion)
    }
}

@Test
fun `no support library dependencies should exist`() {
    val dependencyTree = analyzeDependencyTree(":app", "debugRuntimeClasspath")
    val supportLibDeps = dependencyTree.filter { it.group == "com.android.support" }
    
    assertTrue(supportLibDeps.isEmpty())
}

@Test
fun `all okhttp dependencies should resolve to version 4_12_0`() {
    val dependencyTree = analyzeDependencyTree(":app", "debugRuntimeClasspath")
    val okhttpDeps = dependencyTree.filter { 
        it.group == "com.squareup.okhttp3" && it.name == "okhttp" 
    }
    
    okhttpDeps.forEach { dep ->
        assertEquals("4.12.0", dep.resolvedVersion)
        assertFalse(dep.resolvedVersion.startsWith("3."))
    }
}
```

### 构建验证测试

**测试目标**: 验证项目可以成功构建

**测试方法**: 单元测试（集成测试）

**测试用例**:
```kotlin
@Test
fun `swallow module should build successfully`() {
    val result = executeGradleTask(":swallow:assembleDebug")
    assertEquals(0, result.exitCode)
}

@Test
fun `base module should build successfully`() {
    val result = executeGradleTask(":base:assembleDebug")
    assertEquals(0, result.exitCode)
}

@Test
fun `app module should build debug and release successfully`() {
    val debugResult = executeGradleTask(":app:assembleDebug")
    assertEquals(0, debugResult.exitCode)
    
    val releaseResult = executeGradleTask(":app:assembleRelease")
    assertEquals(0, releaseResult.exitCode)
}
```

### 冲突减少效果验证测试

**测试目标**: 验证依赖优化的效果

**测试方法**: 单元测试

**测试用例**:
```kotlin
@Test
fun `major version conflicts should be reduced to 0-2`() {
    val conflicts = detectDependencyConflicts(":app")
    val majorConflicts = conflicts.filter { it.type == ConflictType.MAJOR }
    assertTrue(majorConflicts.size in 0..2)
}

@Test
fun `minor version conflicts should be reduced to 10-15`() {
    val conflicts = detectDependencyConflicts(":app")
    val minorConflicts = conflicts.filter { it.type == ConflictType.MINOR }
    assertTrue(minorConflicts.size in 10..15)
}

@Test
fun `total conflicts should be reduced by 68-82 percent`() {
    val beforeConflicts = 57 // 优化前的冲突数
    val afterConflicts = detectDependencyConflicts(":app").size
    val reduction = (beforeConflicts - afterConflicts).toDouble() / beforeConflicts * 100
    assertTrue(reduction in 68.0..82.0)
}
```

### 功能完整性验证测试

**测试目标**: 验证项目功能不受影响

**测试方法**: 单元测试 + UI 测试

**测试用例**:
```kotlin
@Test
fun `all unit tests should pass`() {
    val result = executeGradleTask("test")
    assertEquals(0, result.exitCode)
}

@Test
fun `all ui tests should pass`() {
    val result = executeGradleTask("connectedAndroidTest")
    assertEquals(0, result.exitCode)
}
```

### 属性测试配置

**测试库**: 由于这是 Gradle 构建配置优化，不涉及运行时代码，我们主要使用：
- JUnit 5 进行单元测试
- 自定义的依赖树分析工具
- Gradle TestKit 进行构建测试

**测试标签**: 每个测试应该使用注释标记其验证的属性

```kotlin
/**
 * Feature: swallow-dependency-optimization
 * Property 2: Kotlin 版本统一性
 * For any Kotlin 相关依赖，依赖树中解析的版本应该都是 2.0.21
 */
@Test
fun `all kotlin dependencies should resolve to version 2_0_21`() {
    // 测试实现
}
```

### 测试执行

**本地测试**:
```bash
# 运行所有测试
./gradlew test

# 运行特定模块的测试
./gradlew :swallow:test

# 运行 UI 测试
./gradlew connectedAndroidTest
```

**CI/CD 集成**:
- 在每次提交时运行依赖冲突检测
- 在每次 PR 时运行完整的测试套件
- 定期检查依赖更新

### 测试覆盖目标

- 配置文件验证: 100%
- 依赖解析验证: 核心依赖 100%
- 构建验证: 所有模块 100%
- 功能测试: 核心功能 100%

## Implementation Notes

### 实施顺序

1. **第一阶段**: 更新 Version Catalog
   - 风险: 低
   - 影响: 无
   - 可回滚: 是

2. **第二阶段**: 配置 Kotlin 选项
   - 风险: 低
   - 影响: 编译性能
   - 可回滚: 是

3. **第三阶段**: 优化 swallow 模块
   - 风险: 中
   - 影响: swallow 模块及依赖它的模块
   - 可回滚: 是

4. **第四阶段**: 优化 base 模块
   - 风险: 中
   - 影响: base 模块及依赖它的模块
   - 可回滚: 是

5. **第五阶段**: 配置全局 Resolution Strategy
   - 风险: 高
   - 影响: 所有模块
   - 可回滚: 是

6. **第六阶段**: 验证和测试
   - 风险: 低
   - 影响: 无
   - 可回滚: 不适用

### 回滚策略

每个阶段完成后应该：
1. 提交代码到版本控制
2. 运行完整的测试套件
3. 如果出现问题，可以回滚到上一个提交

### 监控和维护

**定期检查**:
- 每月检查依赖更新
- 每季度审查依赖冲突
- 每次升级 Kotlin/AGP 后重新验证

**文档维护**:
- 记录每次依赖变更的原因
- 更新排除依赖的文档
- 保持优化文档的时效性

### 潜在风险

1. **第三方库不兼容**: 某些旧库可能不兼容新版本的依赖
   - 缓解措施: 逐步升级，充分测试
   - 备选方案: 寻找替代库

2. **运行时错误**: 强制版本可能导致运行时不兼容
   - 缓解措施: 充分的功能测试
   - 备选方案: 调整强制版本策略

3. **构建时间增加**: 依赖解析可能增加构建时间
   - 缓解措施: 启用 Gradle 缓存和增量编译
   - 监控: 记录构建时间变化

### 成功标准

优化成功的标准：
- ✅ 主版本冲突 ≤ 2 个
- ✅ 次版本冲突 ≤ 15 个
- ✅ 无 Support Library 依赖
- ✅ 所有模块构建成功
- ✅ 所有测试通过
- ✅ 核心功能正常工作
