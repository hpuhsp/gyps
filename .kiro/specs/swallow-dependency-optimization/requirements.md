# Requirements Document

## Introduction

本文档定义了 Gyps 项目中 swallow 基础库的依赖优化需求。项目当前存在 57 个依赖冲突（7 个主版本冲突、49 个次版本冲突、1 个补丁版本冲突），需要系统化地解决这些问题，确保依赖版本统一、兼容性良好，并消除潜在的运行时风险。

## Glossary

- **Swallow**: 项目的核心框架库，包含基础类、网络层、数据库和工具类
- **Base**: UI 资源模块，包含自定义组件、适配器和通用 UI 组件
- **Version_Catalog**: Gradle 的版本目录（libs.versions.toml），用于集中管理依赖版本
- **Dependency_Conflict**: 依赖冲突，指同一个库的不同版本被多个依赖引入
- **Transitive_Dependency**: 传递依赖，指通过第三方库间接引入的依赖
- **Resolution_Strategy**: Gradle 的依赖解析策略，用于强制指定依赖版本
- **AndroidX**: Android 的现代化支持库，替代了旧的 Support Library
- **Support_Library**: Android 的旧版支持库，已被 AndroidX 替代

## Requirements

### Requirement 1: 更新 Version Catalog 补充缺失版本

**User Story:** 作为开发者，我希望在 Version Catalog 中补充所有缺失的 AndroidX 和第三方库版本定义，以便统一管理项目依赖版本。

#### Acceptance Criteria

1. WHEN 更新 libs.versions.toml 文件 THEN THE System SHALL 添加所有缺失的 AndroidX 库版本定义
2. WHEN 添加版本定义 THEN THE System SHALL 使用最新稳定版本号
3. WHEN 定义第三方库版本 THEN THE System SHALL 包含 okio、jetbrains-annotations 和 errorprone 的版本
4. WHEN 完成版本定义 THEN THE System SHALL 确保所有版本号与项目现有依赖兼容

### Requirement 2: 解决 Kotlin 标准库版本冲突

**User Story:** 作为开发者，我希望统一项目中所有 Kotlin 标准库的版本，以避免运行时不兼容问题。

#### Acceptance Criteria

1. WHEN 检测到多个 Kotlin 标准库版本 THEN THE System SHALL 强制使用项目定义的 Kotlin 版本（2.0.21）
2. WHEN 配置 Kotlin 版本 THEN THE System SHALL 在 gradle.properties 中启用 kotlin.stdlib.default.dependency
3. WHEN 第三方库引入旧版 Kotlin THEN THE System SHALL 通过 Resolution Strategy 强制使用统一版本
4. WHEN 构建项目 THEN THE System SHALL 确保所有 Kotlin 相关依赖使用相同版本

### Requirement 3: 解决 OkHttp 和 OkIO 版本冲突

**User Story:** 作为开发者，我希望统一 OkHttp 和 OkIO 的版本，以确保网络层的稳定性和兼容性。

#### Acceptance Criteria

1. WHEN 检测到旧版 OkHttp（3.x）THEN THE System SHALL 排除这些传递依赖
2. WHEN retrofit-url-manager 引入旧版 OkHttp THEN THE System SHALL 在依赖声明中排除 OkHttp 和 OkIO
3. WHEN 排除旧版本后 THEN THE System SHALL 确保项目使用 OkHttp 4.12.0 和 OkIO 3.6.0
4. WHEN 构建项目 THEN THE System SHALL 验证所有网络请求功能正常工作

### Requirement 4: 消除 Support Library 依赖

**User Story:** 作为开发者，我希望完全移除项目中的 Support Library 依赖，确保项目完全迁移到 AndroidX。

#### Acceptance Criteria

1. WHEN 检测到 Support Library 依赖 THEN THE System SHALL 识别引入该依赖的第三方库
2. WHEN 第三方库引入 Support Library THEN THE System SHALL 在依赖声明中排除 com.android.support 组
3. WHEN 无法排除 Support Library THEN THE System SHALL 在 Resolution Strategy 中抛出异常阻止构建
4. WHEN 构建项目 THEN THE System SHALL 确保依赖树中不包含任何 Support Library

### Requirement 5: 优化 swallow 模块依赖配置

**User Story:** 作为开发者，我希望优化 swallow 模块的依赖配置，排除所有问题传递依赖。

#### Acceptance Criteria

1. WHEN 声明 retrofit-url-manager 依赖 THEN THE System SHALL 排除 OkHttp 和 OkIO
2. WHEN 声明 arouter-api 依赖 THEN THE System SHALL 排除 Kotlin 标准库
3. WHEN 声明 utilcodex 依赖 THEN THE System SHALL 排除 Support Library
4. WHEN 声明 eventbus 依赖 THEN THE System SHALL 排除 Kotlin 标准库
5. WHEN 完成配置 THEN THE System SHALL 确保 swallow 模块可以成功构建

### Requirement 6: 优化 base 模块依赖配置

**User Story:** 作为开发者，我希望优化 base 模块的依赖配置，排除第三方 UI 库的问题传递依赖。

#### Acceptance Criteria

1. WHEN 声明 pictureselector 依赖 THEN THE System SHALL 排除 OkHttp、Support Library 和 exifinterface
2. WHEN 声明 baserecyclerviewadapterhelper 依赖 THEN THE System SHALL 排除 Support Library 和 recyclerview
3. WHEN 声明 recyclerview-flexibledivider 依赖 THEN THE System SHALL 排除 Support Library 和 recyclerview
4. WHEN 声明 flyco-tablayout 依赖 THEN THE System SHALL 排除 Support Library
5. WHEN 声明 material-dialogs-bottomsheets 依赖 THEN THE System SHALL 排除 Kotlin 标准库
6. WHEN 完成配置 THEN THE System SHALL 确保 base 模块可以成功构建

### Requirement 7: 配置全局依赖解析策略

**User Story:** 作为开发者,我希望在根项目中配置全局依赖解析策略，作为最后的防线确保版本统一。

#### Acceptance Criteria

1. WHEN 配置 Resolution Strategy THEN THE System SHALL 强制使用项目定义的 Kotlin 版本
2. WHEN 配置 Resolution Strategy THEN THE System SHALL 强制使用项目定义的 OkHttp 和 OkIO 版本
3. WHEN 配置 Resolution Strategy THEN THE System SHALL 强制使用项目定义的 Gson 版本
4. WHEN 配置 Resolution Strategy THEN THE System SHALL 强制使用项目定义的 Coroutines 版本
5. WHEN 检测到 Support Library THEN THE System SHALL 抛出 GradleException 阻止构建
6. WHEN 配置完成 THEN THE System SHALL 在所有子项目中生效

### Requirement 8: 移除遗留依赖

**User Story:** 作为开发者，我希望移除 swallow 模块中不必要的遗留依赖，减少依赖冲突和包体积。

#### Acceptance Criteria

1. WHEN 审查 swallow 依赖 THEN THE System SHALL 识别不必要的遗留依赖
2. WHEN 发现 legacy-support-v4 THEN THE System SHALL 评估是否可以移除
3. WHEN 发现 vectordrawable THEN THE System SHALL 评估是否可以移除
4. WHEN 发现 viewpager THEN THE System SHALL 评估是否可以移除
5. WHEN 移除依赖后 THEN THE System SHALL 确保项目功能不受影响

### Requirement 9: 验证依赖冲突解决效果

**User Story:** 作为开发者，我希望验证依赖优化的效果，确保冲突数量显著减少。

#### Acceptance Criteria

1. WHEN 完成依赖优化 THEN THE System SHALL 重新检测依赖冲突
2. WHEN 检测冲突 THEN THE System SHALL 主版本冲突减少到 0-2 个
3. WHEN 检测冲突 THEN THE System SHALL 次版本冲突减少到 10-15 个
4. WHEN 生成依赖报告 THEN THE System SHALL 对比优化前后的差异
5. WHEN 验证完成 THEN THE System SHALL 确保冲突总数减少 68-82%

### Requirement 10: 验证项目功能完整性

**User Story:** 作为开发者，我希望验证依赖优化后项目的所有功能仍然正常工作。

#### Acceptance Criteria

1. WHEN 完成依赖优化 THEN THE System SHALL 成功构建 debug 和 release 版本
2. WHEN 运行单元测试 THEN THE System SHALL 所有测试通过
3. WHEN 运行 UI 测试 THEN THE System SHALL 所有测试通过
4. WHEN 手动测试 THEN THE System SHALL 验证网络请求、图片加载、数据库、导航、依赖注入功能正常
5. WHEN 手动测试 THEN THE System SHALL 验证第三方 UI 组件（PictureSelector、BaseAdapter 等）正常工作

### Requirement 11: 配置 Kotlin 编译选项

**User Story:** 作为开发者，我希望在 gradle.properties 中配置 Kotlin 相关选项，优化编译性能和版本管理。

#### Acceptance Criteria

1. WHEN 配置 gradle.properties THEN THE System SHALL 启用 kotlin.stdlib.default.dependency
2. WHEN 配置 gradle.properties THEN THE System SHALL 启用 kotlin.incremental 增量编译
3. WHEN 配置 gradle.properties THEN THE System SHALL 启用 kotlin.incremental.java 增量编译
4. WHEN 配置完成 THEN THE System SHALL 确保配置在所有模块中生效

### Requirement 12: 生成依赖优化文档

**User Story:** 作为开发者，我希望生成依赖优化的文档记录，便于团队理解和维护。

#### Acceptance Criteria

1. WHEN 完成依赖优化 THEN THE System SHALL 记录哪些库需要排除依赖及原因
2. WHEN 生成文档 THEN THE System SHALL 记录优化前后的冲突对比数据
3. WHEN 生成文档 THEN THE System SHALL 记录验证测试的结果
4. WHEN 生成文档 THEN THE System SHALL 提供后续维护建议
5. WHEN 文档完成 THEN THE System SHALL 保存到 docs 目录供团队参考
