# Swallow Framework

[![Maven Central](https://img.shields.io/maven-central/v/com.swallow/swallow)](https://search.maven.org/artifact/com.swallow/swallow)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=24)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org)

现代化的 Android 移动开发框架，基于 Google 最新的 Android Architecture Components (AAC) 构建，提供完整的 MVVM 架构支持。

## 特性

- 🏗️ **MVVM 架构** - 清晰的分层设计（UI/Presentation/Data）
- 💉 **Hilt 依赖注入** - 编译时依赖注入，类型安全
- 🔄 **响应式编程** - Kotlin Coroutines + Flow
- 🎨 **ViewBinding** - 类型安全的视图绑定
- 🌐 **网络层** - Retrofit + OkHttp + 统一结果封装
- 💾 **本地存储** - Room + MMKV
- 🖼️ **图片加载** - Glide 5.0
- 🧭 **导航** - AndroidX Navigation + DeepLink
- 🔐 **权限管理** - 现代化的权限请求 API
- 📊 **状态管理** - StateFlow + SharedFlow

## 快速开始

### 添加依赖

```kotlin
// build.gradle.kts (Project)
buildscript {
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.57.2")
    }
}

// build.gradle.kts (Module)
plugins {
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

dependencies {
    implementation("com.swallow:swallow:2.0.0")
    ksp("com.google.dagger:hilt-compiler:2.57.2")
}
```

### 初始化框架

```kotlin
@HiltAndroidApp
class MyApplication : BaseApplication() {
    
    override fun provideConfig(): FrameworkConfig {
        return frameworkConfig {
            // 配置网络层
            network {
                baseUrl("https://api.example.com/")
                connectTimeout(30)
                readTimeout(30)
                writeTimeout(30)
            }
            
            // 配置数据库
            database {
                databaseName("my_app.db")
            }
            
            // 配置日志
            log {
                enableLogging(BuildConfig.DEBUG)
            }
        }
    }
}
```

### 创建 ViewModel

```kotlin
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : BaseViewModel() {
    
    fun loadData() {
        viewModelScope.launch {
            repository.getData()
                .onStart { showLoading("加载中...") }
                .catch { showError(it) }
                .onCompletion { hideLoading() }
                .collect { result ->
                    result.onSuccess { data ->
                        _uiState.value = UiState.Success(data)
                    }
                }
        }
    }
}
```

### 创建 Activity

```kotlin
@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    
    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate
    
    override val modelClass: Class<MainViewModel>
        get() = MainViewModel::class.java
    
    override fun initView(savedInstanceState: Bundle?) {
        binding.btnLoad.setOnClickListener {
            mViewModel.loadData()
        }
    }
    
    override fun initData(savedInstanceState: Bundle?) {
        mViewModel.loadData()
    }
    
    // 可选：自定义状态处理
    override fun handleUiState(state: UiState) {
        when (state) {
            is UiState.Success<*> -> {
                val data = state.data
                // 更新 UI
            }
            else -> super.handleUiState(state)
        }
    }
}
```

### 创建 Repository

```kotlin
@Singleton
class MainRepository @Inject constructor() : BaseRepositoryNothing() {
    
    fun getData(): Flow<HttpResult<Data>> {
        return flowRequest {
            obtainService(ApiService::class.java).getData()
        }
    }
}
```

## 文档

- [变更日志](CHANGELOG.md) - 版本更新记录
- [API 兼容性](API_COMPATIBILITY.md) - API 稳定性说明
- [迁移指南](MIGRATION_GUIDE.md) - 从 1.x 升级到 2.0

## 架构

```
┌─────────────────────────────────────────┐
│           UI Layer (Activity/Fragment)   │
│  - BaseActivity / BaseFragment          │
│  - ViewBinding                          │
│  - StateFlow/SharedFlow 观察            │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│      Presentation Layer (ViewModel)     │
│  - BaseViewModel                        │
│  - StateFlow (UI 状态)                  │
│  - SharedFlow (UI 事件)                 │
│  - UseCase (可选)                       │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│        Data Layer (Repository)          │
│  - BaseRepository                       │
│  - HttpResult 封装                      │
│  - Flow 响应式数据流                    │
└─────────────────┬───────────────────────┘
                  │
        ┌─────────┴─────────┐
        │                   │
┌───────▼────────┐  ┌──────▼──────────┐
│  Remote Source │  │  Local Source   │
│  - Retrofit    │  │  - Room         │
│  - OkHttp      │  │  - MMKV         │
└────────────────┘  └─────────────────┘
```

## API 稳定性

Swallow 使用注解标记 API 的稳定性级别：

- `@Stable` - 稳定 API，保证向后兼容
- `@ExperimentalSwallowApi` - 实验性 API，可能会变更
- `@InternalSwallowApi` - 内部 API，不应使用
- `@Deprecated` + `@ScheduledForRemoval` - 计划废弃的 API

详见 [API 兼容性文档](API_COMPATIBILITY.md)

## 版本要求

| 组件 | 最低版本 | 推荐版本 |
|------|---------|---------|
| Android SDK | API 24 | API 35 |
| Kotlin | 1.9.0 | 2.0.21 |
| Gradle | 8.0 | 8.7 |
| JDK | 17 | 17 |

## 依赖版本

| 库 | 版本 |
|----|------|
| Hilt | 2.57.2 |
| Coroutines | 1.9.0 |
| Lifecycle | 2.8.7 |
| Navigation | 2.8.5 |
| Room | 2.6.1 |
| Retrofit | 2.11.0 |
| OkHttp | 4.12.0 |
| Glide | 5.0.5 |

## 示例项目

查看 [app](../app) 模块了解完整的使用示例。

## 贡献

欢迎提交 Issue 和 Pull Request！

## 许可证

```
Copyright 2024 Swallow Framework

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 联系方式

- Email: 1101121039@qq.com
- GitHub: [Swallow Framework](https://github.com/yourusername/swallow)

---

Made with ❤️ by Swallow Team
