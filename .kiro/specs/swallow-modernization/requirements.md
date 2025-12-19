# Swallow 核心库现代化升级需求文档

## 简介

本文档定义了 Swallow 核心库的全面现代化升级需求。Swallow 是一个基于 MVVM 架构的 Android 开发框架核心库，需要升级到最新的技术栈，以支持现代 Android 开发实践，并为未来的 Compose 和 KMM 跨平台开发做好准备。

## 术语表

- **Swallow**: 项目的核心 MVVM 框架库模块
- **AGP**: Android Gradle Plugin，Android 构建插件
- **KSP**: Kotlin Symbol Processing，Kotlin 符号处理器，kapt 的替代品
- **Compose**: Jetpack Compose，Android 现代声明式 UI 框架
- **KMM**: Kotlin Multiplatform Mobile，Kotlin 跨平台移动开发
- **JDK**: Java Development Kit，Java 开发工具包
- **Jetpack**: Android Jetpack 组件库集合
- **Maven**: 依赖管理和发布仓库

## 需求

### 需求 1: 构建工具升级

**用户故事**: 作为框架维护者，我希望升级构建工具到最新稳定版本，以便获得更好的构建性能和新特性支持。

#### 验收标准

1. WHEN 项目构建时 THEN Swallow 模块 SHALL 使用 AGP 8.12.3 或更高稳定版本
2. WHEN 项目构建时 THEN Swallow 模块 SHALL 使用 Gradle 8.14.3 或更高版本
3. WHEN 项目构建时 THEN Swallow 模块 SHALL 使用 JDK 17 作为编译和运行环境
4. WHEN 项目构建时 THEN Swallow 模块 SHALL 使用 Kotlin 2.2.20 或更高稳定版本
5. WHEN 构建脚本执行时 THEN 所有 Gradle 构建文件 SHALL 使用 Kotlin DSL (.gradle.kts) 格式

### 需求 2: SDK 版本升级

**用户故事**: 作为框架维护者，我希望支持最新的 Android SDK 版本，以便应用能够在最新的 Android 系统上运行并使用新特性。

#### 验收标准

1. WHEN 应用编译时 THEN Swallow 模块 SHALL 设置 targetSdk 为 35
2. WHEN 应用编译时 THEN Swallow 模块 SHALL 设置 compileSdk 为 35
3. WHEN 应用运行时 THEN Swallow 模块 SHALL 保持 minSdk 为 24 以确保广泛兼容性
4. WHEN 应用构建时 THEN Swallow 模块 SHALL 处理 Android 14 和 15 的新权限和行为变更

### 需求 3: Jetpack 组件升级

**用户故事**: 作为框架维护者，我希望升级所有 Jetpack 组件到最新稳定版本，以便获得性能改进和新功能。

#### 验收标准

1. WHEN Swallow 模块构建时 THEN 系统 SHALL 使用 Lifecycle 2.9.4 或更高版本
2. WHEN Swallow 模块构建时 THEN 系统 SHALL 使用 Room 2.8.3 或更高版本
3. WHEN Swallow 模块构建时 THEN 系统 SHALL 使用 Navigation 2.9.5 或更高版本
4. WHEN Swallow 模块构建时 THEN 系统 SHALL 使用 Paging 3.3.x 或更高版本
5. WHEN Swallow 模块构建时 THEN 系统 SHALL 使用 Material Components 1.13.x 或更高版本
6. WHEN Swallow 模块构建时 THEN 系统 SHALL 使用 ConstraintLayout 2.2.1 或更高版本
7. WHEN Swallow 模块构建时 THEN 系统 SHALL 使用 Core-KTX 1.16.x 或更高版本

### 需求 4: 依赖注入迁移到 KSP

**用户故事**: 作为框架维护者，我希望从 kapt 迁移到 KSP，以便获得更快的编译速度和更好的 Kotlin 支持。

#### 验收标准

1. WHEN Swallow 模块构建时 THEN 系统 SHALL 使用 Hilt 2.57.2 或更高版本并配置 KSP 支持
2. WHEN Swallow 模块构建时 THEN 系统 SHALL 使用 Room KSP 处理器替代 kapt
3. WHEN Swallow 模块构建时 THEN 系统 SHALL 移除所有 kapt 配置
4. WHEN 编译注解处理时 THEN 构建时间 SHALL 比使用 kapt 时减少至少 25%

### 需求 5: 网络库升级

**用户故事**: 作为框架维护者，我希望升级网络相关库到最新版本，以便获得安全修复和性能改进。

#### 验收标准

1. WHEN Swallow 模块构建时 THEN 系统 SHALL 使用 Retrofit 3.0.x 或更高版本
2. WHEN Swallow 模块构建时 THEN 系统 SHALL 使用 OkHttp 4.12.x 或更高版本
3. WHEN Swallow 模块构建时 THEN 系统 SHALL 使用 Gson 2.11.x 或更高版本
4. WHEN 网络请求执行时 THEN 系统 SHALL 支持 Kotlin 协程的原生集成
5. WHEN 网络请求失败时 THEN 系统 SHALL 正确处理所有 HTTP 状态码和异常

### 需求 6: 图片加载库升级

**用户故事**: 作为框架维护者，我希望升级图片加载库，以便获得更好的性能和现代化的 API。

#### 验收标准

1. WHEN Swallow 模块构建时 THEN 系统 SHALL 使用 Glide 5.0.5 或更高版本
2. WHEN Glide 注解处理时 THEN 系统 SHALL 使用 KSP 替代 kapt
3. WHEN 图片加载时 THEN 系统 SHALL 支持 Kotlin 协程和 Flow
4. WHEN 图片加载时 THEN 系统 SHALL 正确处理内存缓存和磁盘缓存

### 需求 7: 协程库升级

**用户故事**: 作为框架维护者，我希望升级协程库到最新版本，以便使用新的协程特性和性能改进。

#### 验收标准

1. WHEN Swallow 模块构建时 THEN 系统 SHALL 使用 Kotlinx Coroutines 1.10.2 或更高版本
2. WHEN 协程执行时 THEN 系统 SHALL 支持结构化并发
3. WHEN 协程取消时 THEN 系统 SHALL 正确传播取消信号到所有子协程
4. WHEN 使用 Flow 时 THEN 系统 SHALL 支持所有最新的 Flow 操作符


### 需求 8: 工具类库升级

**用户故事**: 作为框架维护者，我希望升级或替换工具类库，以便使用更现代化和维护良好的库。

#### 验收标准

1. WHEN Swallow 模块构建时 THEN 系统 SHALL 使用 Timber 5.x 或更高版本
2. WHEN Swallow 模块构建时 THEN 系统 SHALL 评估 ImmersionBar 的替代方案或升级到最新版本
3. WHEN Swallow 模块构建时 THEN 系统 SHALL 使用支持 Android 14+ 的权限处理库
4. WHEN Swallow 模块构建时 THEN 系统 SHALL 使用 MMKV 2.x 或更高版本
5. WHEN 工具类使用时 THEN 系统 SHALL 优先使用 Kotlin 标准库和 AndroidX 提供的功能

### 需求 9: Gradle Kotlin DSL 迁移

**用户故事**: 作为框架维护者，我希望将所有 Gradle 构建脚本迁移到 Kotlin DSL，以便获得类型安全和更好的 IDE 支持。

#### 验收标准

1. WHEN 项目构建时 THEN 根目录 build.gradle SHALL 转换为 build.gradle.kts
2. WHEN 项目构建时 THEN settings.gradle SHALL 转换为 settings.gradle.kts
3. WHEN 项目构建时 THEN Swallow 模块的 build.gradle SHALL 转换为 build.gradle.kts
4. WHEN 项目构建时 THEN app、base、msc 模块的 build.gradle SHALL 转换为 build.gradle.kts
5. WHEN 构建脚本编辑时 THEN IDE SHALL 提供完整的代码补全和类型检查
6. WHEN 项目构建时 THEN 所有依赖版本 SHALL 使用版本目录 (Version Catalog) 统一管理

### 需求 10: Compose 支持准备

**用户故事**: 作为框架维护者，我希望为未来的 Compose 迁移做好准备，以便能够逐步引入 Compose UI。

#### 验收标准

1. WHEN Swallow 模块配置时 THEN 系统 SHALL 添加 Compose BOM 依赖配置
2. WHEN Swallow 模块配置时 THEN 系统 SHALL 启用 Compose 编译器支持
3. WHEN 使用 ViewModel 时 THEN 系统 SHALL 支持 Compose 的 ViewModel 集成
4. WHEN 使用 Navigation 时 THEN 系统 SHALL 支持 Compose Navigation
5. WHEN 使用 Hilt 时 THEN 系统 SHALL 支持 Compose 的依赖注入

### 需求 11: KMM 跨平台支持准备

**用户故事**: 作为框架维护者，我希望为未来的 KMM 跨平台开发做好架构准备，以便能够共享业务逻辑代码。

#### 验收标准

1. WHEN Swallow 模块架构设计时 THEN 系统 SHALL 将平台无关的业务逻辑与 Android 特定代码分离
2. WHEN Swallow 模块架构设计时 THEN Repository 层 SHALL 使用接口定义，便于跨平台实现
3. WHEN Swallow 模块架构设计时 THEN ViewModel 层 SHALL 最小化对 Android 特定 API 的依赖
4. WHEN Swallow 模块配置时 THEN 项目结构 SHALL 支持未来添加 KMM 共享模块
5. WHEN 使用协程时 THEN 系统 SHALL 使用 Kotlin 标准库的协程 API，避免 Android 特定实现

### 需求 12: 代码质量改进

**用户故事**: 作为框架维护者，我希望修正现有代码中的不合理设计和过时写法，以便提供更优秀的 MVVM 框架。

#### 验收标准

1. WHEN BaseActivity 初始化时 THEN 系统 SHALL 使用 by viewModels() 委托替代手动 ViewModelProvider
2. WHEN 使用权限请求时 THEN 系统 SHALL 使用 Activity Result API 替代已废弃的 onRequestPermissionsResult
3. WHEN 使用 ProgressDialog 时 THEN 系统 SHALL 提供现代化的替代方案（Material Design 进度指示器）
4. WHEN 观察 LiveData 时 THEN 系统 SHALL 使用 Kotlin 扩展函数简化代码
5. WHEN 处理生命周期时 THEN 系统 SHALL 使用 lifecycleScope 和 repeatOnLifecycle
6. WHEN 代码编写时 THEN Kotlin 代码比例 SHALL 达到 95% 以上


### 需求 13: 依赖冲突解决

**用户故事**: 作为框架维护者，我希望解决升级过程中的所有依赖冲突，以便项目能够成功编译和运行。

#### 验收标准

1. WHEN 项目构建时 THEN 系统 SHALL 解决所有传递依赖冲突
2. WHEN 项目构建时 THEN 系统 SHALL 确保没有重复的类定义
3. WHEN 项目构建时 THEN 系统 SHALL 使用依赖约束统一版本
4. WHEN 依赖更新时 THEN 系统 SHALL 验证所有模块间的兼容性
5. WHEN 构建完成时 THEN 系统 SHALL 生成无警告的构建报告

### 需求 14: 测试和验证

**用户故事**: 作为框架维护者，我希望完整测试升级后的框架，以便确保所有功能正常工作。

#### 验收标准

1. WHEN 升级完成后 THEN app 测试模块 SHALL 成功编译
2. WHEN 升级完成后 THEN app 测试模块 SHALL 成功运行在 Android 设备上
3. WHEN 测试 MVVM 功能时 THEN 所有 Activity、ViewModel、Repository 交互 SHALL 正常工作
4. WHEN 测试网络功能时 THEN Retrofit 网络请求 SHALL 正常执行
5. WHEN 测试数据库功能时 THEN Room 数据库操作 SHALL 正常执行
6. WHEN 测试依赖注入时 THEN Hilt 注入 SHALL 在所有场景下正常工作
7. WHEN 测试图片加载时 THEN Glide 图片加载 SHALL 正常显示
8. WHEN 测试生命周期时 THEN 所有生命周期感知组件 SHALL 正确响应生命周期变化

### 需求 15: 文档更新

**用户故事**: 作为框架维护者，我希望更新所有相关文档，以便用户了解如何使用升级后的框架。

#### 验收标准

1. WHEN 升级完成后 THEN README.md SHALL 更新所有版本号和依赖信息
2. WHEN 升级完成后 THEN README.md SHALL 包含 KSP 配置说明
3. WHEN 升级完成后 THEN README.md SHALL 包含 Kotlin DSL 配置示例
4. WHEN 升级完成后 THEN README.md SHALL 包含迁移指南
5. WHEN 升级完成后 THEN 技术栈文档 SHALL 更新到最新版本信息

### 需求 16: 向后兼容性

**用户故事**: 作为框架使用者，我希望了解升级的破坏性变更，以便能够顺利迁移现有项目。

#### 验收标准

1. WHEN 发布新版本时 THEN 系统 SHALL 提供详细的破坏性变更列表
2. WHEN 发布新版本时 THEN 系统 SHALL 提供迁移指南文档
3. WHEN API 变更时 THEN 系统 SHALL 标记已废弃的 API 并提供替代方案
4. WHEN 配置变更时 THEN 系统 SHALL 提供新旧配置的对比示例
5. WHEN 依赖变更时 THEN 系统 SHALL 说明最低支持的 Android 版本和 JDK 版本

### 需求 17: 性能优化

**用户故事**: 作为框架维护者，我希望优化框架性能，以便提供更好的用户体验。

#### 验收标准

1. WHEN 使用 KSP 时 THEN 编译时间 SHALL 比使用 kapt 减少至少 25%
2. WHEN 应用启动时 THEN 框架初始化时间 SHALL 不超过 100ms
3. WHEN 使用协程时 THEN 系统 SHALL 避免不必要的线程切换
4. WHEN 使用 Room 时 THEN 数据库查询 SHALL 在后台线程执行
5. WHEN 使用 Glide 时 THEN 图片加载 SHALL 使用适当的缓存策略
