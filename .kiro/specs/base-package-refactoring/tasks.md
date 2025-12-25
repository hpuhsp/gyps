# 实施计划：Base 包重构

## 概述

本实施计划将 `com.swallow.fly.base` 包的重组分解为离散的编码步骤。每个任务都建立在前面的任务之上，确保增量进展和持续验证。重构将按架构层进行，从生命周期管理开始，然后是 UI 层、表示层，最后是数据层。

## 任务

- [x] 1. 准备工作和备份
  - 创建当前代码的 Git 分支用于重构
  - 运行现有测试套件，记录基线结果
  - 使用 `getDiagnostics` 验证当前代码无编译错误
  - _需求：7.2, 7.4_

- [x] 2. 重构生命周期管理包（app → lifecycle）
  - [x] 2.1 创建新的 lifecycle 包结构
    - 创建 `com.swallow.fly.base.lifecycle` 包
    - 创建 `com.swallow.fly.base.lifecycle.config` 子包
    - 创建 `com.swallow.fly.base.lifecycle.parse` 子包
    - _需求：2.1, 3.1_

  - [x] 2.2 移动应用生命周期类
    - 移动 `BaseApplication.kt` 到 `lifecycle/`
    - 移动 `AppDelegate.kt` 到 `lifecycle/`
    - 移动 `AppLifecycles.kt` 到 `lifecycle/`
    - 移动 `AppModule.kt` 到 `lifecycle/`
    - 移动 `ConfigModule.kt` 到 `lifecycle/`
    - 更新这些文件的 package 声明
    - _需求：2.2, 2.3_

  - [x] 2.3 移动配置类
    - 移动 `app/config/` 下所有文件到 `lifecycle/config/`
    - 移动 `app/parse/` 下所有文件到 `lifecycle/parse/`
    - 更新所有文件的 package 声明
    - _需求：3.2, 3.4_

  - [x] 2.4 更新 lifecycle 包的导入引用
    - 在整个项目中搜索 `com.swallow.fly.base.app` 的导入
    - 替换为 `com.swallow.fly.base.lifecycle`
    - 使用 `getDiagnostics` 验证无编译错误
    - _需求：1.4, 7.1, 7.2_

  - [ ]* 2.5 编写属性测试验证 lifecycle 重构
    - **属性 1：包导入一致性**
    - **属性 2：架构层分离正确性**
    - **验证：需求 1.4, 2.2**

- [x] 3. 检查点 - 验证生命周期重构
  - 运行 Gradle 编译：`./gradlew compileDebugKotlin`
  - 确保所有测试通过
  - 如有问题，询问用户

- [x] 4. 重构 UI 层包（view → ui）
  - [x] 4.1 创建新的 ui 包结构
    - 创建 `com.swallow.fly.base.ui` 包
    - 创建 `com.swallow.fly.base.ui.activity` 子包
    - 创建 `com.swallow.fly.base.ui.fragment` 子包
    - _需求：4.1_

  - [x] 4.2 移动 Activity 相关类
    - 移动 `IActivity.kt` 从根目录到 `ui/activity/`
    - 移动 `view/BaseActivity.kt` 到 `ui/activity/`
    - 移动 `view/FastBaseActivity.kt` 到 `ui/activity/`
    - 更新所有文件的 package 声明
    - _需求：1.2, 4.2, 4.3_

  - [x] 4.3 移动 Fragment 相关类
    - 移动 `IFragment.kt` 从根目录到 `ui/fragment/`
    - 移动 `view/BaseFragment.kt` 到 `ui/fragment/`
    - 移动 `view/BaseLazyFragment.kt` 到 `ui/fragment/`
    - 更新所有文件的 package 声明
    - _需求：1.2, 4.2, 4.3_

  - [x] 4.4 移动 UI 行为接口
    - 移动 `ViewBehavior.kt` 从根目录到 `ui/`
    - 更新 package 声明
    - _需求：1.3_

  - [x] 4.5 更新 ui 包的导入引用
    - 搜索 `com.swallow.fly.base.view` 的导入
    - 替换为 `com.swallow.fly.base.ui.activity` 或 `com.swallow.fly.base.ui.fragment`
    - 搜索根目录下 `IActivity`、`IFragment`、`ViewBehavior` 的导入
    - 替换为新的 ui 包路径
    - 使用 `getDiagnostics` 验证无编译错误
    - _需求：1.4, 7.1, 7.2_

  - [ ]* 4.6 编写属性测试验证 UI 重构
    - **属性 1：包导入一致性**
    - **属性 2：架构层分离正确性**
    - **验证：需求 4.2, 4.3**

- [ ] 5. 检查点 - 验证 UI 层重构
  - 运行 Gradle 编译：`./gradlew compileDebugKotlin`
  - 确保所有测试通过
  - 如有问题，询问用户

- [x] 6. 重构表示层包（viewmodel + viewstate + event → presentation）
  - [x] 6.1 创建新的 presentation 包结构
    - 创建 `com.swallow.fly.base.presentation` 包
    - 创建 `com.swallow.fly.base.presentation.state` 子包
    - _需求：5.1, 6.1_

  - [x] 6.2 移动 ViewModel 类
    - 移动 `viewmodel/IViewModel.kt` 到 `presentation/`
    - 移动 `viewmodel/BaseViewModel.kt` 到 `presentation/`
    - 更新所有文件的 package 声明
    - _需求：5.3_

  - [x] 6.3 移动状态类到 presentation/state
    - 移动 `viewmodel/UiState.kt` 到 `presentation/state/`
    - 移动 `viewmodel/UiEvent.kt` 到 `presentation/state/`
    - 移动 `viewstate/PageViewState.kt` 到 `presentation/state/`
    - 移动 `viewstate/PageListState.kt` 到 `presentation/state/`
    - 移动 `viewstate/PageStateEvent.kt` 到 `presentation/state/`
    - 移动 `event/BaseStateEvent.kt` 到 `presentation/state/`
    - 更新所有文件的 package 声明
    - _需求：6.2, 6.3, 6.5_

  - [x] 6.4 更新 presentation 包的导入引用
    - 搜索 `com.swallow.fly.base.viewmodel` 的导入
    - 替换为 `com.swallow.fly.base.presentation` 或 `com.swallow.fly.base.presentation.state`
    - 搜索 `com.swallow.fly.base.viewstate` 的导入
    - 替换为 `com.swallow.fly.base.presentation.state`
    - 搜索 `com.swallow.fly.base.event` 的导入
    - 替换为 `com.swallow.fly.base.presentation.state`
    - 使用 `getDiagnostics` 验证无编译错误
    - _需求：1.4, 7.1, 7.2_

  - [ ]* 6.5 编写属性测试验证 presentation 重构
    - **属性 1：包导入一致性**
    - **属性 2：架构层分离正确性**
    - **验证：需求 5.3, 6.2, 6.3**

- [ ] 7. 检查点 - 验证表示层重构
  - 运行 Gradle 编译：`./gradlew compileDebugKotlin`
  - 确保所有测试通过
  - 如有问题，询问用户

- [x] 8. 重构数据层包（repository → data）
  - [x] 8.1 创建新的 data 包结构
    - 创建 `com.swallow.fly.base.data` 包
    - _需求：5.1_

  - [x] 8.2 移动 Repository 类
    - 移动 `repository/IRepository.kt` 到 `data/`
    - 移动 `repository/BaseRepository.kt` 到 `data/`
    - 移动 `repository/Repository.kt` 到 `data/`
    - 更新所有文件的 package 声明
    - _需求：5.2_

  - [x] 8.3 更新 data 包的导入引用
    - 搜索 `com.swallow.fly.base.repository` 的导入
    - 替换为 `com.swallow.fly.base.data`
    - 使用 `getDiagnostics` 验证无编译错误
    - _需求：1.4, 7.1, 7.2_

  - [ ]* 8.4 编写属性测试验证 data 重构
    - **属性 1：包导入一致性**
    - **属性 2：架构层分离正确性**
    - **验证：需求 5.2**

- [ ] 9. 检查点 - 验证数据层重构
  - 运行 Gradle 编译：`./gradlew compileDebugKotlin`
  - 确保所有测试通过
  - 如有问题，询问用户

- [x] 10. 删除旧的空包目录
  - [x] 10.1 删除旧包目录
    - 删除 `base/app/` 目录（如果为空）
    - 删除 `base/view/` 目录（如果为空）
    - 删除 `base/viewmodel/` 目录（如果为空）
    - 删除 `base/viewstate/` 目录（如果为空）
    - 删除 `base/repository/` 目录（如果为空）
    - 删除 `base/event/` 目录（如果为空）
    - _需求：8.1_

  - [x] 10.2 验证清理
    - 使用 `getDiagnostics` 确认无编译错误
    - 运行完整测试套件
    - _需求：7.2_

- [-] 11. 验证公共 API 不变性
  - [ ]* 11.1 编写属性测试验证 API 签名
    - **属性 3：公共 API 不变性**
    - 验证所有公共类的方法签名未改变
    - 验证所有公共接口的方法签名未改变
    - **验证：需求 7.3**

  - [ ] 11.2 运行 API 兼容性检查
    - 如果项目有 API 兼容性检查工具，运行它
    - 确认没有破坏性变更
    - _需求：7.3_

- [ ] 12. 验证包命名规范
  - [ ]* 12.1 编写属性测试验证包命名
    - **属性 4：包命名规范性**
    - 验证所有新包名遵循命名约定
    - 验证包名具有描述性
    - **验证：需求 8.1, 8.3**

- [ ] 13. 更新项目文档
  - [x] 13.1 更新 structure.md
    - 更新 swallow 模块的包结构说明
    - 更新包职责描述
    - 添加新旧包名对照表
    - _需求：9.4_

  - [ ] 13.2 创建迁移指南
    - 创建 `docs/base-package-migration-guide.md`
    - 记录所有包名变更
    - 提供导入更新示例
    - 提供自动迁移脚本（如果适用）
    - _需求：9.1, 9.2, 9.3_

  - [ ] 13.3 更新 README.md
    - 更新快速开始指南中的导入示例
    - 更新架构说明图
    - _需求：9.1_

- [ ] 14. 最终验证和测试
  - [ ] 14.1 运行完整编译
    - 运行 `./gradlew clean build`
    - 确保所有模块编译成功
    - _需求：7.2_

  - [ ] 14.2 运行完整测试套件
    - 运行 `./gradlew test`
    - 运行 `./gradlew connectedAndroidTest`（如果有设备）
    - 对比测试结果与基线
    - _需求：7.4_

  - [ ]* 14.3 运行所有属性测试
    - **属性 5：编译成功性**
    - 验证所有重构步骤后代码可编译
    - **验证：需求 7.2**

  - [ ] 14.4 手动验证示例应用
    - 运行 app 模块
    - 验证基本功能正常工作
    - 确认没有运行时错误
    - _需求：7.4_

- [ ] 15. 最终检查点
  - 确认所有任务完成
  - 确认所有测试通过
  - 确认文档已更新
  - 准备合并到主分支

## 注意事项

- 标记 `*` 的任务是可选的测试任务，可以跳过以加快 MVP 进度
- 每个任务都引用了具体的需求编号以便追溯
- 检查点任务确保增量验证
- 属性测试验证通用正确性属性
- 单元测试验证特定示例和边缘情况
- 使用 IDE 的重构功能（Move Class、Rename）可以自动更新导入
- 使用 `getDiagnostics` 工具而不是手动编译来验证错误
- 如果遇到问题，在检查点处询问用户指导

## 实施策略

1. **增量进行**：每次只重构一个架构层
2. **持续验证**：每个主要步骤后都进行编译和测试
3. **使用 IDE 工具**：利用 Android Studio 的重构功能自动更新引用
4. **保持向后兼容**：不改变任何公共 API 签名
5. **文档同步**：代码变更的同时更新文档
