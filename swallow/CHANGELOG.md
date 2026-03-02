# Swallow Framework Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- API 稳定性注解系统 (`@Stable`, `@ExperimentalSwallowApi`, `@InternalSwallowApi`, `@ScheduledForRemoval`)
- 完整的 ProGuard 混淆规则
- 性能优化的 BaseViewModel 实现
- 单元测试基础设施

### Changed
- 优化 StateFlow/SharedFlow 配置以提升性能
- 更新 ProGuard 规则以适配新的包结构

### Fixed
- 修复 ProGuard 规则中过时的类路径引用

## [2.0.0] - 2024-12-XX

### 🎉 新特性

#### 架构升级
- **现代化状态管理**: 从 LiveData 迁移到 StateFlow/SharedFlow
- **Domain 层支持**: 添加 UseCase 模式支持
- **Kotlin DSL 配置**: 使用类型安全的 DSL 配置框架
- **HttpResult 封装**: 统一的网络请求结果处理

#### 新增功能
- 添加 `DeepLinkNavigator` 深度链接导航支持
- 添加 `FlowUseCase` 接口支持响应式 UseCase
- 添加 `PermissionDelegate` 权限请求代理
- 添加 `ProgressDelegate` 进度弹窗代理
- 支持 Activity Result API

### ⚠️ 破坏性变更

#### 包名重构
为了更清晰的架构分层，进行了以下包名调整：

| 旧包名 | 新包名 | 说明 |
|--------|--------|------|
| `base.app` | `base.lifecycle` | 应用生命周期管理 |
| `base.view` | `base.ui.activity` / `base.ui.fragment` | UI 层组件 |
| `base.viewmodel` | `base.presentation` | 表现层 |
| `base.viewstate` | `base.presentation.state` | 状态管理 |
| `base.event` | `base.presentation.state` | 事件管理 |
| `base.repository` | `base.data` | 数据层 |

**迁移指南**: 使用 IDE 的全局替换功能批量更新导入语句

#### API 变更

**BaseViewModel**
```kotlin
// ❌ 旧 API (已废弃)
viewModel.pageStateEvent.observe(this) { event ->
    when (event.event) {
        EventArgs.SHOW_LOADING -> showLoading()
        EventArgs.SHOW_ERROR -> showError()
    }
}

// ✅ 新 API
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        launch { viewModel.uiState.collect { state -> handleUiState(state) } }
        launch { viewModel.uiEvent.collect { event -> handleUiEvent(event) } }
    }
}
```

**IRepositoryManager**
```kotlin
// ❌ 旧 API (已废弃)
val service = repositoryManager.obtainRetrofitService(ApiService::class.java)

// ✅ 新 API (解耦技术栈)
val service = repositoryManager.obtainService(ApiService::class.java)
```

**BaseActivity ViewModel 初始化**
```kotlin
// ✅ 方式 1: 提供 modelClass (推荐，兼容性好)
override val modelClass: Class<MainViewModel>
    get() = MainViewModel::class.java

// ✅ 方式 2: 使用委托 (更现代)
override val mViewModel: MainViewModel by viewModels()
```

### 🔄 废弃 API

以下 API 已标记为 `@Deprecated`，将在 3.0.0 版本中移除：

#### BaseViewModel
- `pageStateEvent: LiveData<BaseStateEvent>` → 使用 `uiState: StateFlow<UiState>` 和 `uiEvent: SharedFlow<UiEvent>`
- `showLoading(msg: Int)` → 使用 `showLoading(message: String?)`
- `showError(code: Int, message: String?)` → 使用 `showError(message: String)`
- `hideAllDialog()` → 使用 `hideLoading()`

#### IRepositoryManager
- `obtainRetrofitService()` → 使用 `obtainService()`

#### AppDataBaseManager
- `save()` → 使用 `suspend saveCache()`
- `get()` → 使用 `suspend getCache()`
- `delete()` → 使用 `suspend deleteCache()`

#### AppCacheDao
- `loadCacheLiveData()` → 使用 `loadCacheFlow()`
- `loadCacheSync()` → 使用 `suspend loadCache()`
- `insertSync()` → 使用 `suspend insert()`
- `deleteCaches()` → 使用 `suspend delete()`

### 🐛 Bug 修复
- 修复 BaseActivity 中 ViewModel 在 Hilt 环境下的依赖注入问题
- 修复 BaseFragment 中 ViewModel 创建时未使用 `defaultViewModelProviderFactory` 的问题
- 修复 StateFlow 重复发射相同状态的性能问题

### 📝 文档
- 添加完整的 KDoc 文档
- 添加 API 兼容性说明文档
- 添加迁移指南
- 更新 ProGuard 混淆规则

### 🔧 依赖更新
- Kotlin: 2.0.21
- Hilt: 2.57.2
- Coroutines: 1.9.0
- Lifecycle: 2.8.7
- Room: 2.6.1
- Glide: 5.0.5
- Retrofit: 2.11.0
- OkHttp: 4.12.0

## [1.0.3] - 2024-11-XX

### Changed
- 移除 ARouter 核心依赖，改为可选依赖
- 优化依赖管理策略

### Fixed
- 修复部分内存泄漏问题

## [1.0.2] - 2024-10-XX

### Added
- 添加 MMKV 支持
- 添加 Paging 3 支持

### Fixed
- 修复网络请求异常处理

## [1.0.1] - 2024-09-XX

### Fixed
- 修复 BaseActivity 生命周期问题
- 修复 Glide 配置问题

## [1.0.0] - 2024-08-XX

### Added
- 初始版本发布
- MVVM 架构支持
- Hilt 依赖注入
- Retrofit 网络层
- Room 数据库
- Glide 图片加载
- 基础 UI 组件

---

## 版本说明

### 版本号规则
遵循语义化版本 (Semantic Versioning)：`MAJOR.MINOR.PATCH`

- **MAJOR**: 不兼容的 API 变更
- **MINOR**: 向后兼容的功能新增
- **PATCH**: 向后兼容的问题修复

### 支持策略
- **当前版本 (2.x)**: 完全支持，持续更新
- **上一版本 (1.x)**: 安全修复支持至 2025-06-30
- **更早版本**: 不再支持

### 升级建议
- **1.x → 2.0**: 需要代码迁移，参考 [MIGRATION_GUIDE.md](MIGRATION_GUIDE.md)
- **2.0.x → 2.0.y**: 无需代码修改，直接升级

[Unreleased]: https://github.com/yourusername/swallow/compare/v2.0.0...HEAD
[2.0.0]: https://github.com/yourusername/swallow/compare/v1.0.3...v2.0.0
[1.0.3]: https://github.com/yourusername/swallow/compare/v1.0.2...v1.0.3
[1.0.2]: https://github.com/yourusername/swallow/compare/v1.0.1...v1.0.2
[1.0.1]: https://github.com/yourusername/swallow/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/yourusername/swallow/releases/tag/v1.0.0
