# 需求文档

## 简介

本文档概述了 Gyps Android 框架中 `com.swallow.fly.base` 包下文件结构重组和优化的需求。当前结构是自然增长形成的，需要更好的组织来提高可维护性、可发现性，并遵循清晰的架构原则。

## 术语表

- **Base_Package（基础包）**: 包含核心框架类的 `com.swallow.fly.base` 包
- **MVVM**: Model-View-ViewModel 架构模式
- **Framework_Core（框架核心）**: 定义框架基础的核心类
- **Lifecycle_Components（生命周期组件）**: 与 Android 生命周期管理相关的类
- **UI_Components（UI组件）**: 与用户界面相关的类（Activities、Fragments）
- **Data_Components（数据组件）**: 与数据管理相关的类（Repository、ViewModel）
- **Configuration_System（配置系统）**: 管理框架初始化和配置的类

## 需求

### 需求 1: 组织核心接口和契约

**用户故事：** 作为框架开发者，我希望核心接口和契约按逻辑分组，以便快速理解框架的扩展点。

#### 验收标准

1. Base_Package 应当包含一个专门用于核心接口的子目录
2. 当接口定义组件契约（IActivity、IFragment、IViewModel、IRepository）时，系统应当按架构层对它们进行分组
3. 系统应当将行为接口（ViewBehavior）与组件接口分开
4. 当接口被移动时，系统应当更新所有依赖文件中的导入语句

### 需求 2: 分离应用生命周期管理

**用户故事：** 作为框架开发者，我希望应用生命周期和配置类清晰分离，以便初始化逻辑易于理解和维护。

#### 验收标准

1. Base_Package 应当包含一个专门用于应用生命周期管理的子目录
2. 当类处理应用初始化（BaseApplication、AppDelegate、AppLifecycles）时，系统应当将它们分组在一起
3. 系统应当将配置类与生命周期类分开
4. 系统应当在不引入破坏性变更的情况下维护现有的初始化流程

### 需求 3: 组织配置系统

**用户故事：** 作为框架开发者，我希望配置相关的类有清晰的层次结构，以便框架设置更加直观。

#### 验收标准

1. Base_Package 应当包含一个专门用于配置管理的子目录
2. 当类提供配置（FrameworkConfig、GlobalConfiguration、ConfigModule）时，系统应当将它们分组在一起
3. 系统应当将配置构建器与配置提供者分开
4. 系统应当将清单解析工具与配置类放在一起

### 需求 4: 分组 UI 层组件

**用户故事：** 作为框架开发者，我希望所有 UI 相关的基类组织在一起，以便轻松找到 Activity 和 Fragment 实现。

#### 验收标准

1. Base_Package 应当包含一个专门用于 UI 组件的子目录
2. 当类扩展 Android UI 组件（BaseActivity、BaseFragment、BaseLazyFragment）时，系统应当将它们分组在一起
3. 系统应当将 UI 特定接口（IActivity、IFragment）与 UI 实现放在一起
4. 系统应当将快速/简化实现与功能完整的实现分开

### 需求 5: 组织数据层组件

**用户故事：** 作为框架开发者，我希望数据层类（Repository、ViewModel）与 UI 分开组织，以便架构层次清晰定义。

#### 验收标准

1. Base_Package 应当包含专门用于 Repository 和 ViewModel 层的子目录
2. 当类属于数据层（BaseRepository、IRepository）时，系统应当将它们分组在 repository 子目录中
3. 当类属于表示层（BaseViewModel、IViewModel）时，系统应当将它们分组在 viewmodel 子目录中
4. 系统应当将相关的状态类（UiState、UiEvent）与 ViewModel 类放在一起

### 需求 6: 整合状态管理

**用户故事：** 作为框架开发者，我希望所有状态相关的类组织在一起，以便状态管理模式清晰明了。

#### 验收标准

1. Base_Package 应当包含一个专门用于状态管理的子目录
2. 当类定义 UI 状态（UiState、UiEvent、PageViewState）时，系统应当将它们分组在一起
3. 当类定义状态事件（BaseStateEvent、PageStateEvent）时，系统应当将它们与状态类分组
4. 系统应当将页面特定状态与通用状态类分开
5. 当类是废弃的状态机制（BaseStateEvent、EventArgs）时，系统应当将它们与新的状态类放在同一包中以便对比和迁移

### 需求 7: 维护向后兼容性

**用户故事：** 作为框架用户，我希望重构不会破坏我现有的代码，以便无问题地升级。

#### 验收标准

1. 当文件被移动时，系统应当更新所有内部导入语句
2. 当包名改变时，系统应当验证不存在编译错误
3. 系统应当保持所有公共 API 签名不变
4. 系统应当在不改变行为的情况下保留所有现有功能

### 需求 8: 改进包命名一致性

**用户故事：** 作为框架开发者，我希望包名清楚地表明其用途，以便导航更加直观。

#### 验收标准

1. 系统应当使用反映内容用途的描述性包名
2. 当包包含多个相关概念时，系统应当使用清晰的层次命名
3. 系统应当遵循 Kotlin/Android 命名约定
4. 系统应当避免过于通用的名称，如用 "app" 表示特定用途

### 需求 9: 文档化包结构

**用户故事：** 作为框架开发者，我希望有清晰的新结构文档，以便了解在哪里放置新类。

#### 验收标准

1. 系统应当提供显示新组织结构的包结构图
2. 当创建包时，系统应当记录其用途
3. 系统应当提供关于在哪里放置新类的指南
4. 系统应当更新项目的 structure.md 文档
