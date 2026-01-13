# Implementation Plan: swallow-dependency-optimization

## Overview

本实施计划将系统化地解决 Gyps 项目中 swallow 基础库的 57 个依赖冲突。我们将按照 6 个阶段逐步实施，每个阶段都包含验证步骤，确保变更的安全性和可回滚性。

## Tasks

- [ ] 1. 第一阶段：更新 Version Catalog
  - [x] 1.1 在 libs.versions.toml 中添加缺失的 AndroidX 版本定义
    - 添加 androidx-annotation, androidx-collection, androidx-savedstate 等版本
    - 添加 androidx-arch-core, androidx-concurrent, androidx-profileinstaller 等版本
    - 添加 androidx-startup, androidx-tracing, androidx-versionedparcelable 等版本
    - 添加 androidx-customview, androidx-drawerlayout, androidx-coordinatorlayout 等版本
    - 添加 androidx-viewpager2, androidx-transition, androidx-slidingpanelayout 等版本
    - 添加 androidx-exifinterface, androidx-swiperefreshlayout, androidx-emoji2, androidx-camera 版本
    - _Requirements: 1.1, 1.3_
  
  - [x] 1.2 在 libs.versions.toml 中添加第三方库版本定义
    - 添加 okio = "3.6.0"
    - 添加 jetbrains-annotations = "23.0.0"
    - 添加 errorprone = "2.27.0"
    - _Requirements: 1.3_
  
  - [x]* 1.3 验证 Version Catalog 配置正确性
    - 解析 libs.versions.toml 文件
    - 验证所有必需的版本键都存在
    - **Property 1: Version Catalog 完整性**
    - **Validates: Requirements 1.1**

- [ ] 2. 第二阶段：配置 Kotlin 编译选项
  - [x] 2.1 在 gradle.properties 中添加 Kotlin 配置
    - 添加 kotlin.stdlib.default.dependency=true
    - 添加 kotlin.incremental=true
    - 添加 kotlin.incremental.java=true
    - _Requirements: 2.2, 11.1, 11.2, 11.3_
  
  - [x]* 2.2 验证 Kotlin 配置正确性
    - 读取 gradle.properties 文件
    - 验证 kotlin.stdlib.default.dependency 为 true
    - 验证 kotlin.incremental 为 true
    - 验证 kotlin.incremental.java 为 true
    - _Requirements: 2.2, 11.1, 11.2, 11.3_

- [x] 3. 第三阶段：优化 swallow 模块依赖配置
  - [x] 3.1 在 swallow/build.gradle.kts 中配置 retrofit-url-manager 排除策略
    - 排除 com.squareup.okhttp3 组
    - 排除 com.squareup.okio 组
    - _Requirements: 3.2, 5.1_
  
  - [x] 3.2 在 swallow/build.gradle.kts 中配置 arouter-api 排除策略
    - 排除 org.jetbrains.kotlin 组
    - _Requirements: 5.2_
  
  - [x] 3.3 在 swallow/build.gradle.kts 中配置 utilcodex 排除策略
    - 排除 com.android.support 组
    - _Requirements: 5.3_
  
  - [x] 3.4 在 swallow/build.gradle.kts 中配置 eventbus 排除策略
    - 排除 org.jetbrains.kotlin 组
    - _Requirements: 5.4_
  
  - [x]* 3.5 验证 swallow 模块依赖排除配置
    - 解析 swallow/build.gradle.kts 文件
    - 验证 retrofit-url-manager 包含 OkHttp 和 OkIO 排除配置
    - 验证 arouter-api 包含 Kotlin 排除配置
    - 验证 utilcodex 包含 Support Library 排除配置
    - 验证 eventbus 包含 Kotlin 排除配置
    - **Property 5: 依赖排除配置正确性**
    - **Validates: Requirements 5.1, 5.2, 5.3, 5.4**
  
  - [x] 3.6 构建 swallow 模块验证配置正确
    - 执行 ./gradlew :swallow:assembleDebug
    - 验证构建成功
    - _Requirements: 5.5_

- [x] 4. Checkpoint - 验证 swallow 模块优化效果
  - 确保 swallow 模块构建成功
  - 检查是否有新的错误或警告
  - 如有问题，询问用户是否继续

- [x] 5. 第四阶段：优化 base 模块依赖配置
  - [x] 5.1 在 base/build.gradle.kts 中配置 pictureselector 排除策略
    - 排除 com.squareup.okhttp3 组
    - 排除 com.android.support 组
    - 排除 androidx.exifinterface 组
    - _Requirements: 6.1_
  
  - [x] 5.2 在 base/build.gradle.kts 中配置 baserecyclerviewadapterhelper 排除策略
    - 排除 com.android.support 组
    - 排除 androidx.recyclerview 组
    - _Requirements: 6.2_
  
  - [x] 5.3 在 base/build.gradle.kts 中配置 recyclerview-flexibledivider 排除策略
    - 排除 com.android.support 组
    - 排除 androidx.recyclerview 组
    - _Requirements: 6.3_
  
  - [x] 5.4 在 base/build.gradle.kts 中配置 flyco-tablayout 排除策略
    - 排除 com.android.support 组
    - _Requirements: 6.4_
  
  - [x] 5.5 在 base/build.gradle.kts 中配置 material-dialogs-bottomsheets 排除策略
    - 排除 org.jetbrains.kotlin 组
    - _Requirements: 6.5_
  
  - [ ]* 5.6 验证 base 模块依赖排除配置
    - 解析 base/build.gradle.kts 文件
    - 验证 pictureselector 包含正确的排除配置
    - 验证 baserecyclerviewadapterhelper 包含正确的排除配置
    - 验证 recyclerview-flexibledivider 包含正确的排除配置
    - 验证 flyco-tablayout 包含正确的排除配置
    - 验证 material-dialogs-bottomsheets 包含正确的排除配置
    - **Property 5: 依赖排除配置正确性**
    - **Validates: Requirements 6.1, 6.2, 6.3, 6.4, 6.5**
  
  - [x] 5.7 构建 base 模块验证配置正确
    - 执行 ./gradlew :base:assembleDebug
    - 验证构建成功
    - _Requirements: 6.6_

- [ ] 6. Checkpoint - 验证 base 模块优化效果
  - 确保 base 模块构建成功
  - 检查是否有新的错误或警告
  - 如有问题，询问用户是否继续

- [x] 7. 第五阶段：配置全局依赖解析策略
  - [x] 7.1 在根 build.gradle.kts 中添加 subprojects 配置块
    - 创建 subprojects 配置块
    - 创建 configurations.all 配置块
    - 创建 resolutionStrategy 配置块
    - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5, 7.6_
  
  - [x] 7.2 配置 Kotlin 版本强制策略
    - 强制 kotlin-stdlib 使用 2.0.21
    - 强制 kotlin-stdlib-common 使用 2.0.21
    - 强制 kotlin-stdlib-jdk7 使用 2.0.21
    - 强制 kotlin-stdlib-jdk8 使用 2.0.21
    - _Requirements: 2.1, 2.3, 7.1_
  
  - [x] 7.3 配置 OkHttp 和 OkIO 版本强制策略
    - 强制 okhttp 使用 4.12.0
    - 强制 logging-interceptor 使用 4.12.0
    - 强制 okio 使用 3.6.0
    - _Requirements: 3.1, 3.3, 7.2_
  
  - [x] 7.4 配置其他核心库版本强制策略
    - 强制 gson 使用 2.11.0
    - 强制 kotlinx-coroutines-core 使用 1.9.0
    - 强制 kotlinx-coroutines-android 使用 1.9.0
    - 强制 jetbrains annotations 使用 23.0.0
    - 强制 androidx.annotation 使用 1.9.1
    - _Requirements: 7.3, 7.4_
  
  - [x] 7.5 配置 Support Library 禁止策略
    - 添加 eachDependency 检查
    - 当检测到 com.android.support 组时抛出 GradleException
    - 提供清晰的错误消息和解决方案
    - _Requirements: 4.2, 4.3, 7.5_
  
  - [ ]* 7.6 验证 Resolution Strategy 配置完整性
    - 解析根 build.gradle.kts 文件
    - 验证包含 Kotlin 版本强制配置
    - 验证包含 OkHttp/OkIO 版本强制配置
    - 验证包含 Gson 版本强制配置
    - 验证包含 Coroutines 版本强制配置
    - 验证包含 Support Library 禁止配置
    - **Property 6: Resolution Strategy 配置完整性**
    - **Validates: Requirements 7.1, 7.2, 7.3, 7.4, 7.5**

- [ ] 8. 第六阶段：验证和测试
  - [ ] 8.1 清理并重新构建项目
    - 执行 ./gradlew clean
    - 执行 ./gradlew assembleDebug
    - 执行 ./gradlew assembleRelease
    - 验证所有构建成功
    - _Requirements: 10.1_
  
  - [ ]* 8.2 分析依赖树验证 Kotlin 版本统一
    - 执行 ./gradlew :app:dependencies --configuration debugRuntimeClasspath
    - 解析依赖树
    - 验证所有 Kotlin 相关依赖都解析为 2.0.21
    - **Property 2: Kotlin 版本统一性**
    - **Validates: Requirements 2.1, 2.4**
  
  - [ ]* 8.3 分析依赖树验证 OkHttp 版本统一
    - 解析依赖树
    - 验证所有 OkHttp 依赖都解析为 4.12.0
    - 验证不存在任何 OkHttp 3.x 版本
    - **Property 3: OkHttp 版本统一性**
    - **Validates: Requirements 3.1, 3.3**
  
  - [ ]* 8.4 分析依赖树验证 Support Library 完全消除
    - 解析依赖树
    - 验证不存在任何 com.android.support 组的依赖
    - **Property 4: Support Library 完全消除**
    - **Validates: Requirements 4.1, 4.4**
  
  - [ ]* 8.5 检测依赖冲突并验证优化效果
    - 使用 MCP 工具检测 app 模块的依赖冲突
    - 统计主版本冲突数量（应 ≤ 2）
    - 统计次版本冲突数量（应 ≤ 15）
    - 计算冲突减少百分比（应在 68-82%）
    - **Property 8: 冲突减少效果**
    - **Validates: Requirements 9.2, 9.3, 9.5**
  
  - [ ]* 8.6 运行单元测试
    - 执行 ./gradlew test
    - 验证所有测试通过
    - _Requirements: 10.2_
  
  - [ ]* 8.7 运行 UI 测试
    - 执行 ./gradlew connectedAndroidTest
    - 验证所有测试通过
    - _Requirements: 10.3_

- [ ] 9. Checkpoint - 最终验证
  - 确保所有构建成功
  - 确保所有测试通过
  - 确保依赖冲突显著减少
  - 如有问题，询问用户是否需要调整

- [ ] 10. 生成优化文档
  - [ ] 10.1 创建依赖优化总结文档
    - 记录优化前后的冲突对比数据
    - 记录哪些库需要排除依赖及原因
    - 记录验证测试的结果
    - 提供后续维护建议
    - 保存到 docs/DEPENDENCY_OPTIMIZATION_SUMMARY.md
    - _Requirements: 12.1, 12.2, 12.3, 12.4, 12.5_

## Notes

- 任务标记 `*` 的为可选测试任务，可以跳过以加快 MVP 开发
- 每个任务都引用了具体的需求，便于追溯
- Checkpoint 任务确保增量验证，及时发现问题
- 属性测试验证通用正确性属性
- 单元测试验证具体示例和边缘情况
