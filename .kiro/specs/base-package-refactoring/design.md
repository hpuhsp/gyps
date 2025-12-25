# 设计文档

## 概述

本文档描述了 `com.swallow.fly.base` 包重组的详细设计方案。重组的目标是提高代码的可维护性、可发现性，并更清晰地体现 MVVM 架构的层次结构。

## 架构

### 当前结构问题

当前的 `com.swallow.fly.base` 包结构存在以下问题：

1. **接口分散**：核心接口（IActivity、IFragment、ViewBehavior）直接放在 base 包根目录，与实现类分离
2. **命名不清晰**：`app` 子包名称过于通用，不能清楚表达其包含应用生命周期和配置的职责
3. **层次混乱**：UI 层、数据层、状态管理的类分散在不同位置，架构层次不够清晰
4. **状态类分散**：UiState/UiEvent 在 viewmodel 包中，而 PageViewState 在 viewstate 包中，缺乏统一性

### 目标架构

重组后的架构将遵循以下原则：

1. **按架构层分组**：清晰区分 UI 层、表示层、数据层
2. **接口与实现就近**：接口与其实现类放在同一包中
3. **职责明确**：每个包的命名清楚表达其职责
4. **易于扩展**：新增类时能快速找到合适的位置

## 组件和接口

### 新包结构

```
com.swallow.fly.base/
├── lifecycle/              # 应用生命周期管理（原 app/）
│   ├── BaseApplication.kt
│   ├── AppDelegate.kt
│   ├── AppLifecycles.kt
│   ├── AppModule.kt
│   ├── ConfigModule.kt
│   ├── config/            # 配置相关
│   │   ├── FrameworkConfig.kt
│   │   ├── FrameworkConfigHolder.kt
│   │   ├── FrameworkConfigProvider.kt
│   │   └── GlobalConfiguration.kt
│   └── parse/             # 清单解析
│       └── ManifestParser.kt
│
├── ui/                    # UI 层组件（原 view/）
│   ├── activity/          # Activity 相关
│   │   ├── IActivity.kt
│   │   ├── BaseActivity.kt
│   │   └── FastBaseActivity.kt
│   ├── fragment/          # Fragment 相关
│   │   ├── IFragment.kt
│   │   ├── BaseFragment.kt
│   │   └── BaseLazyFragment.kt
│   └── ViewBehavior.kt    # UI 行为接口
│
├── presentation/          # 表示层（原 viewmodel/）
│   ├── IViewModel.kt
│   ├── BaseViewModel.kt
│   └── state/             # 状态管理（合并 viewmodel、viewstate 和 event）
│       ├── UiState.kt
│       ├── UiEvent.kt
│       ├── PageViewState.kt
│       ├── PageListState.kt
│       ├── PageStateEvent.kt
│       ├── BaseStateEvent.kt    # @Deprecated 旧的事件机制
│       └── EventArgs.kt         # @Deprecated 事件类型枚举
│
└── data/                  # 数据层（原 repository/）
    ├── IRepository.kt
    ├── BaseRepository.kt
    └── Repository.kt
```

### 包职责说明

#### 1. lifecycle 包（应用生命周期管理）
**职责**：管理应用的生命周期、初始化流程和框架配置

**包含类**：
- `BaseApplication`：基础 Application 类
- `AppDelegate`：应用生命周期代理
- `AppLifecycles`：生命周期接口
- `AppModule`：Hilt 模块
- `ConfigModule`：配置模块接口
- `config/`：框架配置相关类
- `parse/`：清单解析工具

**命名理由**：`lifecycle` 比 `app` 更准确地表达了这个包的核心职责——管理应用生命周期

#### 2. ui 包（UI 层组件）
**职责**：提供 Activity 和 Fragment 的基类及 UI 行为定义

**包含类**：
- `activity/`：Activity 相关的接口和基类
  - `IActivity`：Activity 契约接口
  - `BaseActivity`：完整功能的 Activity 基类
  - `FastBaseActivity`：简化版 Activity 基类
- `fragment/`：Fragment 相关的接口和基类
  - `IFragment`：Fragment 契约接口
  - `BaseFragment`：Fragment 基类
  - `BaseLazyFragment`：懒加载 Fragment 基类
- `ViewBehavior`：定义 UI 通用行为的接口

**命名理由**：`ui` 清晰表明这是 UI 层，子包 `activity` 和 `fragment` 进一步细分

#### 3. presentation 包（表示层）
**职责**：管理 UI 状态和业务逻辑，连接 UI 层和数据层

**包含类**：
- `IViewModel`：ViewModel 契约接口
- `BaseViewModel`：ViewModel 基类
- `state/`：状态管理（统一管理所有状态和事件）
  - `UiState`：UI 状态密封类（现代化方式）
  - `UiEvent`：UI 事件密封类（现代化方式）
  - `PageViewState`：页面视图状态
  - `PageListState`：列表页面状态
  - `PageStateEvent`：页面状态事件
  - `BaseStateEvent`：基础状态事件（@Deprecated，旧的 LiveData 方式）
  - `EventArgs`：事件类型枚举（@Deprecated，配合 BaseStateEvent 使用）

**命名理由**：`presentation` 是 MVVM 中表示层的标准命名，比 `viewmodel` 更准确地表达了这一层的职责。将 `BaseStateEvent` 放在这里是因为它是 ViewModel 和 View 之间通信的机制，属于表示层的一部分。

**设计说明**：
- `BaseStateEvent` 和 `EventArgs` 是旧的事件通信机制，已被标记为 @Deprecated
- 新代码应使用 `UiState` 和 `UiEvent`（基于 StateFlow/SharedFlow）
- 保留旧机制是为了向后兼容，避免破坏现有代码
- 所有状态相关的类统一放在 `state/` 子包中，便于管理

#### 4. data 包（数据层）
**职责**：提供数据访问的抽象和实现

**包含类**：
- `IRepository`：Repository 契约接口
- `BaseRepository`：Repository 基类
- `Repository`：Repository 标记接口

**命名理由**：`data` 是 Clean Architecture 中数据层的标准命名

### 包结构变更总结

**移除的包**：
- `base/event/` - 该包只包含 `BaseStateEvent`，它实际上是表示层的一部分，应该与其他状态类放在一起

**新增的包**：
- `base/lifecycle/` - 替代 `base/app/`，更清晰地表达应用生命周期管理职责
- `base/ui/activity/` - Activity 相关类的专属包
- `base/ui/fragment/` - Fragment 相关类的专属包
- `base/presentation/` - 替代 `base/viewmodel/`，标准的 MVVM 表示层命名
- `base/presentation/state/` - 统一管理所有状态类（合并了 viewmodel、viewstate 和 event）
- `base/data/` - 替代 `base/repository/`，标准的数据层命名

**设计理念**：
1. **状态集中管理**：所有与状态相关的类（UiState、UiEvent、PageViewState、BaseStateEvent）都放在 `presentation/state/` 中
2. **废弃代码保留**：`BaseStateEvent` 虽然已废弃，但仍保留以支持现有代码，避免破坏性变更
3. **架构清晰**：UI → Presentation → Data 三层架构一目了然

## 数据模型

### 文件迁移映射表

| 原路径 | 新路径 | 说明 |
|--------|--------|------|
| `base/BaseApplication.kt` | `base/lifecycle/BaseApplication.kt` | 应用基类 |
| `base/IActivity.kt` | `base/ui/activity/IActivity.kt` | Activity 接口 |
| `base/IFragment.kt` | `base/ui/fragment/IFragment.kt` | Fragment 接口 |
| `base/ViewBehavior.kt` | `base/ui/ViewBehavior.kt` | UI 行为接口 |
| `base/app/AppDelegate.kt` | `base/lifecycle/AppDelegate.kt` | 生命周期代理 |
| `base/app/AppLifecycles.kt` | `base/lifecycle/AppLifecycles.kt` | 生命周期接口 |
| `base/app/AppModule.kt` | `base/lifecycle/AppModule.kt` | Hilt 模块 |
| `base/app/ConfigModule.kt` | `base/lifecycle/ConfigModule.kt` | 配置模块 |
| `base/app/config/*` | `base/lifecycle/config/*` | 配置类 |
| `base/app/parse/*` | `base/lifecycle/parse/*` | 解析工具 |
| `base/view/BaseActivity.kt` | `base/ui/activity/BaseActivity.kt` | Activity 基类 |
| `base/view/BaseFragment.kt` | `base/ui/fragment/BaseFragment.kt` | Fragment 基类 |
| `base/view/BaseLazyFragment.kt` | `base/ui/fragment/BaseLazyFragment.kt` | 懒加载 Fragment |
| `base/view/FastBaseActivity.kt` | `base/ui/activity/FastBaseActivity.kt` | 快速 Activity |
| `base/viewmodel/BaseViewModel.kt` | `base/presentation/BaseViewModel.kt` | ViewModel 基类 |
| `base/viewmodel/IViewModel.kt` | `base/presentation/IViewModel.kt` | ViewModel 接口 |
| `base/viewmodel/UiState.kt` | `base/presentation/state/UiState.kt` | UI 状态 |
| `base/viewmodel/UiEvent.kt` | `base/presentation/state/UiEvent.kt` | UI 事件 |
| `base/viewstate/PageViewState.kt` | `base/presentation/state/PageViewState.kt` | 页面状态 |
| `base/viewstate/PageListState.kt` | `base/presentation/state/PageListState.kt` | 列表状态 |
| `base/viewstate/PageStateEvent.kt` | `base/presentation/state/PageStateEvent.kt` | 状态事件 |
| `base/repository/BaseRepository.kt` | `base/data/BaseRepository.kt` | Repository 基类 |
| `base/repository/IRepository.kt` | `base/data/IRepository.kt` | Repository 接口 |
| `base/repository/Repository.kt` | `base/data/Repository.kt` | Repository 标记 |
| `base/event/BaseStateEvent.kt` | `base/presentation/state/BaseStateEvent.kt` | 旧的状态事件（@Deprecated） |

### 包名变更映射

| 原包名 | 新包名 |
|--------|--------|
| `com.swallow.fly.base.app` | `com.swallow.fly.base.lifecycle` |
| `com.swallow.fly.base.app.config` | `com.swallow.fly.base.lifecycle.config` |
| `com.swallow.fly.base.app.parse` | `com.swallow.fly.base.lifecycle.parse` |
| `com.swallow.fly.base.view` | `com.swallow.fly.base.ui.activity` / `com.swallow.fly.base.ui.fragment` |
| `com.swallow.fly.base.viewmodel` | `com.swallow.fly.base.presentation` |
| `com.swallow.fly.base.viewstate` | `com.swallow.fly.base.presentation.state` |
| `com.swallow.fly.base.repository` | `com.swallow.fly.base.data` |
| `com.swallow.fly.base.event` | `com.swallow.fly.base.presentation.state` |

## 正确性属性

*属性是一个特征或行为，应该在系统的所有有效执行中保持为真——本质上是关于系统应该做什么的形式化陈述。属性是人类可读规范和机器可验证正确性保证之间的桥梁。*

### 属性 1：包导入一致性
*对于任何*被移动的类文件，更新后的导入语句应该指向新的包路径，并且所有引用该类的文件都应该能够成功编译。

**验证：需求 1.4, 7.1, 7.2**

### 属性 2：架构层分离正确性
*对于任何*类文件，它应该被放置在与其架构职责相匹配的包中：
- UI 层类（Activity、Fragment、接口）在 `ui` 包及其子包中
- 表示层类（ViewModel、状态类）在 `presentation` 包及其子包中
- 数据层类（Repository）在 `data` 包中
- 生命周期类在 `lifecycle` 包中

**验证：需求 1.2, 4.2, 4.3, 5.2, 5.3, 6.2, 6.3**

### 属性 3：公共 API 不变性
*对于任何*公共类、接口或方法，重构前后的签名（方法名、参数类型、返回类型、可见性）应该保持完全相同。

**验证：需求 7.3**

### 属性 4：包命名规范性
*对于任何*包名，它应该：
- 全部使用小写字母
- 使用点号分隔的单词（不使用下划线或驼峰）
- 具有描述性，清楚表达包内类的职责
- 遵循 Kotlin/Android 命名约定

**验证：需求 8.1, 8.3**

### 属性 5：编译成功性
*对于任何*重构步骤完成后的代码库状态，执行 Gradle 编译应该成功，不应该出现任何编译错误。

**验证：需求 7.2**

## 错误处理

### 编译错误处理

**场景**：移动文件后出现导入错误

**处理策略**：
1. 使用 IDE 的"查找用法"功能定位所有引用
2. 批量更新导入语句
3. 使用 `getDiagnostics` 工具验证修复

### 依赖冲突处理

**场景**：包重命名导致循环依赖

**处理策略**：
1. 分析依赖关系图
2. 识别循环依赖的根源
3. 通过接口抽象打破循环
4. 必要时调整包结构

### 测试失败处理

**场景**：重构后测试用例失败

**处理策略**：
1. 确认是测试代码的导入问题还是功能问题
2. 如果是导入问题，更新测试文件的导入语句
3. 如果是功能问题，回滚并重新分析

## 测试策略

### 单元测试

**测试范围**：
- 验证特定文件移动后导入正确
- 验证特定类的公共 API 签名不变
- 验证特定包命名符合约定

**测试示例**：
```kotlin
@Test
fun `BaseActivity should be in ui_activity package`() {
    val clazz = BaseActivity::class.java
    assertEquals("com.swallow.fly.base.ui.activity", clazz.packageName)
}

@Test
fun `BaseViewModel public methods unchanged`() {
    val methods = BaseViewModel::class.java.declaredMethods
        .filter { Modifier.isPublic(it.modifiers) }
    // 验证方法签名
}
```

### 属性测试

**测试配置**：
- 使用 Kotlin 的 Kotest 属性测试库
- 每个属性测试运行 100 次迭代
- 使用随机生成的类路径和包名进行测试

**属性测试示例**：

**属性测试 1：包导入一致性**
```kotlin
@Test
fun `property test - all moved classes have updated imports`() = runTest {
    checkAll(100, Arb.movedClass()) { movedClass ->
        val sourceFile = File(movedClass.newPath)
        val imports = extractImports(sourceFile)
        
        // 验证：不应该包含旧包路径的导入
        imports.none { it.contains(movedClass.oldPackage) } shouldBe true
        
        // 验证：所有引用该类的文件都能编译
        val referencingFiles = findReferencingFiles(movedClass.className)
        referencingFiles.all { it.compiles() } shouldBe true
    }
}
```
**功能：base-package-refactoring，属性 1：对于任何被移动的类文件，更新后的导入语句应该指向新的包路径**

**属性测试 2：架构层分离正确性**
```kotlin
@Test
fun `property test - classes are in correct architectural layer`() = runTest {
    checkAll(100, Arb.classFile()) { classFile ->
        val packagePath = classFile.packageName
        val classType = classFile.architecturalLayer
        
        // 验证：UI 层类在 ui 包中
        if (classType == ArchitecturalLayer.UI) {
            packagePath.contains(".ui.") shouldBe true
        }
        
        // 验证：表示层类在 presentation 包中
        if (classType == ArchitecturalLayer.PRESENTATION) {
            packagePath.contains(".presentation") shouldBe true
        }
        
        // 验证：数据层类在 data 包中
        if (classType == ArchitecturalLayer.DATA) {
            packagePath.contains(".data") shouldBe true
        }
        
        // 验证：生命周期类在 lifecycle 包中
        if (classType == ArchitecturalLayer.LIFECYCLE) {
            packagePath.contains(".lifecycle") shouldBe true
        }
    }
}
```
**功能：base-package-refactoring，属性 2：对于任何类文件，它应该被放置在与其架构职责相匹配的包中**

**属性测试 3：公共 API 不变性**
```kotlin
@Test
fun `property test - public API signatures unchanged`() = runTest {
    checkAll(100, Arb.publicClass()) { className ->
        val oldSignature = getClassSignature(className, "before")
        val newSignature = getClassSignature(className, "after")
        
        // 验证：公共方法签名完全相同
        oldSignature.publicMethods shouldBe newSignature.publicMethods
        
        // 验证：公共属性签名完全相同
        oldSignature.publicProperties shouldBe newSignature.publicProperties
        
        // 验证：类可见性不变
        oldSignature.visibility shouldBe newSignature.visibility
    }
}
```
**功能：base-package-refactoring，属性 3：对于任何公共类，重构前后的签名应该保持完全相同**

**属性测试 4：包命名规范性**
```kotlin
@Test
fun `property test - package names follow conventions`() = runTest {
    checkAll(100, Arb.packageName()) { packageName ->
        val lastSegment = packageName.split(".").last()
        
        // 验证：包名全小写
        lastSegment shouldBe lastSegment.lowercase()
        
        // 验证：包名只包含字母（无数字、下划线）
        lastSegment.matches(Regex("[a-z]+")) shouldBe true
        
        // 验证：包名长度合理（3-20字符）
        lastSegment.length in 3..20 shouldBe true
        
        // 验证：包名具有描述性（不是通用词）
        val genericNames = listOf("app", "util", "common", "base")
        if (lastSegment in genericNames) {
            // 如果是通用名称，应该有更具体的父包
            packageName.split(".").size shouldBeGreaterThan 4
        }
    }
}
```
**功能：base-package-refactoring，属性 4：对于任何包名，它应该遵循命名规范并具有描述性**

**属性测试 5：编译成功性**
```kotlin
@Test
fun `property test - code compiles after each refactoring step`() = runTest {
    checkAll(100, Arb.refactoringStep()) { step ->
        // 应用重构步骤
        applyRefactoringStep(step)
        
        // 验证：Gradle 编译成功
        val result = runGradleBuild("compileDebugKotlin")
        result.exitCode shouldBe 0
        result.output.contains("BUILD SUCCESSFUL") shouldBe true
    }
}
```
**功能：base-package-refactoring，属性 5：对于任何重构步骤完成后，执行 Gradle 编译应该成功**

### 集成测试

**测试范围**：
- 完整的重构流程端到端测试
- 验证所有文件移动和导入更新
- 验证项目编译成功
- 验证现有测试用例通过

**测试步骤**：
1. 备份当前代码库
2. 执行完整重构
3. 运行 Gradle 编译
4. 运行所有现有测试
5. 对比重构前后的行为

### 测试工具

- **Kotest**：Kotlin 属性测试框架
- **getDiagnostics**：验证编译错误
- **Gradle**：编译验证
- **JUnit**：单元测试框架

## 实施注意事项

### 向后兼容性

为了确保平滑过渡，可以考虑以下策略：

1. **类型别名**：在旧包位置创建类型别名指向新位置
```kotlin
// 在 com.swallow.fly.base.view 包中
@Deprecated("Use com.swallow.fly.base.ui.activity.BaseActivity")
typealias BaseActivity<VM, VB> = com.swallow.fly.base.ui.activity.BaseActivity<VM, VB>
```

2. **渐进式迁移**：分阶段完成迁移，每个阶段确保编译通过

3. **文档更新**：同步更新 `structure.md` 和其他文档

### IDE 支持

利用 Android Studio / IntelliJ IDEA 的重构功能：

1. **移动类**：使用 "Move Class" 重构（F6）
2. **重命名包**：使用 "Rename" 重构（Shift+F6）
3. **查找用法**：使用 "Find Usages"（Alt+F7）
4. **优化导入**：使用 "Optimize Imports"（Ctrl+Alt+O）

### 性能考虑

重构不应影响运行时性能，因为：
- 包结构只影响编译时
- 不改变类的实现逻辑
- 不引入额外的抽象层

## 文档更新

### 需要更新的文档

1. **structure.md**：更新包结构说明
2. **README.md**：更新快速开始指南中的导入示例
3. **API 文档**：更新 KDoc 中的包引用
4. **迁移指南**：创建从旧结构到新结构的迁移指南

### 迁移指南内容

```markdown
# Base 包重构迁移指南

## 包名变更

| 旧包名 | 新包名 |
|--------|--------|
| com.swallow.fly.base.app | com.swallow.fly.base.lifecycle |
| com.swallow.fly.base.view | com.swallow.fly.base.ui.activity / ui.fragment |
| com.swallow.fly.base.viewmodel | com.swallow.fly.base.presentation |
| com.swallow.fly.base.repository | com.swallow.fly.base.data |

## 导入更新示例

### 旧代码
```kotlin
import com.swallow.fly.base.view.BaseActivity
import com.swallow.fly.base.viewmodel.BaseViewModel
```

### 新代码
```kotlin
import com.swallow.fly.base.ui.activity.BaseActivity
import com.swallow.fly.base.presentation.BaseViewModel
```

## 自动迁移

使用 IDE 的全局替换功能：
1. 打开 "Replace in Path"（Ctrl+Shift+R）
2. 使用正则表达式批量替换导入语句
```
