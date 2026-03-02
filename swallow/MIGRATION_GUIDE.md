# Swallow Framework 迁移指南

本文档帮助你从 Swallow 1.x 迁移到 2.0。

## 快速开始

### 1. 更新依赖

```kotlin
// build.gradle.kts
dependencies {
    // 旧版本
    // implementation("com.swallow:swallow:1.0.3")
    
    // 新版本
    implementation("com.swallow:swallow:2.0.0")
}
```

### 2. 更新 Kotlin 和依赖版本

```kotlin
// gradle/libs.versions.toml
[versions]
kotlin = "2.0.21"  # 最低 1.9.0
hilt = "2.57.2"    # 最低 2.48
coroutines = "1.9.0"  # 最低 1.7.0
```

### 3. 批量替换包名

使用 IDE 的全局替换功能（Ctrl+Shift+R / Cmd+Shift+R）：

| 查找 | 替换为 |
|------|--------|
| `com.swallow.fly.base.app` | `com.swallow.fly.base.lifecycle` |
| `com.swallow.fly.base.view` | `com.swallow.fly.base.ui.activity` |
| `com.swallow.fly.base.viewmodel` | `com.swallow.fly.base.presentation` |
| `com.swallow.fly.base.viewstate` | `com.swallow.fly.base.presentation.state` |
| `com.swallow.fly.base.event` | `com.swallow.fly.base.presentation.state` |
| `com.swallow.fly.base.repository` | `com.swallow.fly.base.data` |

---

## 详细迁移步骤

### 一、BaseActivity 迁移

#### 1.1 包名更新

```kotlin
// ❌ 旧代码
import com.swallow.fly.base.view.BaseActivity

// ✅ 新代码
import com.swallow.fly.base.ui.activity.BaseActivity
```

#### 1.2 ViewModel 初始化（无需修改）

你的当前实现已经是正确的：

```kotlin
@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    
    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate
    
    override val modelClass: Class<MainViewModel>
        get() = MainViewModel::class.java
    
    // ✅ mViewModel 自动可用，支持 Hilt 注入
}
```

**可选的现代化方式**（如果你想更新）：

```kotlin
@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    
    // 使用委托方式（更符合 Kotlin 风格）
    override val mViewModel: MainViewModel by viewModels()
    
    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate
}
```

---

### 二、BaseFragment 迁移

#### 2.1 包名更新

```kotlin
// ❌ 旧代码
import com.swallow.fly.base.view.BaseFragment

// ✅ 新代码
import com.swallow.fly.base.ui.fragment.BaseFragment
```

#### 2.2 使用方式（无需修改）

```kotlin
@AndroidEntryPoint
class TestFragment : BaseFragment<TestViewModel, FragmentTestBinding>() {
    
    override val modelClass: Class<TestViewModel>?
        get() = TestViewModel::class.java
    
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTestBinding
        get() = FragmentTestBinding::inflate
    
    override fun initView() {
        // 初始化 UI
    }
}
```

---

### 三、BaseViewModel 迁移

#### 3.1 包名更新

```kotlin
// ❌ 旧代码
import com.swallow.fly.base.viewmodel.BaseViewModel
import com.swallow.fly.base.viewstate.*
import com.swallow.fly.base.event.*

// ✅ 新代码
import com.swallow.fly.base.presentation.BaseViewModel
import com.swallow.fly.base.presentation.state.*
```

#### 3.2 从 LiveData 迁移到 StateFlow/SharedFlow

**旧代码（仍然可用，但已废弃）**:

```kotlin
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : BaseViewModel() {
    
    // ❌ 旧方式 - 使用 LiveData
    fun loadData() {
        viewModelScope.launch {
            repository.getData()
                .onStart { showLoading("加载中...") }
                .catch { showError(-1, it.message ?: "错误") }
                .onCompletion { hideAllDialog() }
                .collect { result ->
                    result.onSuccess {
                        // 处理成功
                    }.onFailure {
                        showError(-1, it?.message ?: "失败")
                    }
                }
        }
    }
}
```

**新代码（推荐）**:

```kotlin
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : BaseViewModel() {
    
    // ✅ 新方式 - 使用 StateFlow/SharedFlow
    fun loadData() {
        viewModelScope.launch {
            repository.getData()
                .onStart { 
                    showLoading("加载中...")  // 自动更新 uiState
                }
                .catch { 
                    showError(it.message ?: "错误")  // 自动发送 uiEvent
                }
                .onCompletion { 
                    hideLoading()  // 自动更新 uiState
                }
                .collect { result ->
                    result.onSuccess {
                        // 处理成功
                        _uiState.value = UiState.Success(it)
                    }.onFailure {
                        showError(it?.message ?: "失败")
                    }
                }
        }
    }
}
```

#### 3.3 在 Activity/Fragment 中观察状态

**旧代码（仍然可用，但已废弃）**:

```kotlin
override fun initData(savedInstanceState: Bundle?) {
    // ❌ 旧方式 - 观察 LiveData
    mViewModel.pageStateEvent.observe(this) { event ->
        when (event.event) {
            EventArgs.SHOW_LOADING -> showLoading(getString(event.message))
            EventArgs.HIDE_DIALOG -> hideDialog()
            EventArgs.SHOW_ERROR -> showToast(event.errorMsg)
            EventArgs.SHOW_TOAST -> showToast(event.toastMsg)
        }
    }
}
```

**新代码（推荐）**:

```kotlin
override fun initData(savedInstanceState: Bundle?) {
    // ✅ 新方式 - 观察 StateFlow/SharedFlow
    // BaseActivity 已经自动观察，你只需要重写处理方法
}

// 可选：自定义状态处理
override fun handleUiState(state: UiState) {
    when (state) {
        is UiState.Init -> {}
        is UiState.Idle -> hideDialog()
        is UiState.Loading -> showLoading(state.message)
        is UiState.Success<*> -> {
            hideDialog()
            // 处理成功数据
            val data = state.data
        }
        is UiState.Error -> {
            hideDialog()
            showToast(state.message)
        }
    }
}

// 可选：自定义事件处理
override fun handleUiEvent(event: UiEvent) {
    when (event) {
        is UiEvent.ShowToast -> showToast(event.message)
        is UiEvent.ShowError -> showToast(event.message)
        is UiEvent.Navigate -> {
            // 处理导航
        }
    }
}
```

---

### 四、BaseRepository 迁移

#### 4.1 包名更新

```kotlin
// ❌ 旧代码
import com.swallow.fly.base.repository.BaseRepository
import com.swallow.fly.base.repository.IRepository

// ✅ 新代码
import com.swallow.fly.base.data.BaseRepository
import com.swallow.fly.base.data.IRepository
import com.swallow.fly.base.data.BaseRepositoryRemote  // 如果只有远程数据源
import com.swallow.fly.base.data.BaseRepositoryLocal   // 如果只有本地数据源
import com.swallow.fly.base.data.BaseRepositoryBoth    // 如果有远程和本地数据源
```

#### 4.2 选择合适的 Repository 基类

**场景 1: 只有网络请求（最常见）**

```kotlin
// ✅ 使用 BaseRepositoryNothing（最简单）
@Singleton
class MainRepository @Inject constructor() : BaseRepositoryNothing() {
    
    fun reportHealthyStatus(model: HealthModel): Flow<HttpResult<BaseResponse<Any>>> {
        return flowRequest {
            obtainService(HealthyService::class.java).reportHealthyStatus(model)
        }
    }
}
```

**场景 2: 有远程数据源接口**

```kotlin
// 1. 定义远程数据源
interface UserRemoteDataSource : IRemoteDataSource {
    suspend fun getUserInfo(userId: String): User
}

// 2. 使用 BaseRepositoryRemote
@Singleton
class UserRepository @Inject constructor(
    remoteDataSource: UserRemoteDataSource
) : BaseRepositoryRemote<UserRemoteDataSource>(remoteDataSource) {
    
    fun getUserInfo(userId: String): Flow<HttpResult<User>> {
        return flowRequest {
            remoteDataSource.getUserInfo(userId)
        }
    }
}
```

**场景 3: 有本地和远程数据源**

```kotlin
// 使用 BaseRepositoryBoth
@Singleton
class UserRepository @Inject constructor(
    remoteDataSource: UserRemoteDataSource,
    localDataSource: UserLocalDataSource
) : BaseRepositoryBoth<UserRemoteDataSource, UserLocalDataSource>(
    remoteDataSource, localDataSource
) {
    
    fun getUserInfo(userId: String): Flow<HttpResult<User>> = flow {
        // 先发射本地缓存
        localDataSource.getUser(userId)?.let {
            emit(HttpResult.Success(it))
        }
        
        // 再请求网络
        val result = executeRequest {
            remoteDataSource.getUserInfo(userId)
        }
        
        // 成功后更新缓存
        if (result is HttpResult.Success) {
            localDataSource.saveUser(result.value)
        }
        
        emit(result)
    }.flowOn(Dispatchers.IO)
}
```

---

### 五、IRepositoryManager 迁移

#### 5.1 方法名更新

```kotlin
// ❌ 旧代码
@Deprecated("Use obtainService instead")
fun <T> obtainRetrofitService(service: Class<T>): T

// ✅ 新代码
fun <T> obtainService(service: Class<T>): T
```

**迁移示例**:

```kotlin
// ❌ 旧代码
val service = repositoryManager.obtainRetrofitService(ApiService::class.java)

// ✅ 新代码
val service = repositoryManager.obtainService(ApiService::class.java)
```

**或者直接在 Repository 中使用**:

```kotlin
class MainRepository @Inject constructor() : BaseRepositoryNothing() {
    
    fun getData(): Flow<HttpResult<Data>> {
        return flowRequest {
            // ✅ 直接使用 obtainService
            obtainService(ApiService::class.java).getData()
        }
    }
}
```

---

### 六、数据库迁移

#### 6.1 AppDataBaseManager 方法更新

```kotlin
// ❌ 旧代码（同步方法，已废弃）
AppDataBaseManager.save(cacheId, key, jsonData)
val data = AppDataBaseManager.get(key)
AppDataBaseManager.delete(key)

// ✅ 新代码（suspend 方法）
viewModelScope.launch {
    AppDataBaseManager.saveCache(key, jsonData)
    val data = AppDataBaseManager.getCache(key)
    AppDataBaseManager.deleteCache(key)
}
```

#### 6.2 AppCacheDao 方法更新

```kotlin
// ❌ 旧代码
@Query("SELECT * FROM app_cache WHERE cache_key = :key")
fun loadCacheLiveData(key: String): LiveData<AppCacheEntity>?

@Query("SELECT * FROM app_cache WHERE cache_key = :key")
fun loadCacheSync(key: String): AppCacheEntity?

// ✅ 新代码
@Query("SELECT * FROM app_cache WHERE cache_key = :key")
suspend fun loadCache(key: String): AppCacheEntity?

@Query("SELECT * FROM app_cache WHERE cache_key = :key")
fun loadCacheFlow(key: String): Flow<AppCacheEntity?>
```

---

### 七、权限请求迁移

#### 7.1 使用新的权限代理

**旧代码**:

```kotlin
override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    // 处理权限结果
}
```

**新代码**:

```kotlin
// BaseActivity 已经初始化了权限代理，直接使用
override fun initView(savedInstanceState: Bundle?) {
    binding.btnRequestPermission.setOnClickListener {
        // 请求权限
        requestPermissions(arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ))
    }
}

// 重写回调方法处理结果
override fun onPermissionsResult(permissions: Map<String, Boolean>) {
    val allGranted = permissions.values.all { it }
    if (allGranted) {
        // 所有权限已授予
        showToast("权限已授予")
    } else {
        // 部分权限被拒绝
        val denied = permissions.filter { !it.value }.keys
        showToast("权限被拒绝: $denied")
    }
}
```

---

## 常见问题

### Q1: 编译错误 "Unresolved reference: view"

**原因**: 包名从 `base.view` 改为 `base.ui.activity` 和 `base.ui.fragment`

**解决**: 全局替换导入语句

### Q2: ViewModel 依赖注入失败

**原因**: 可能没有使用 `defaultViewModelProviderFactory`

**解决**: 确保 BaseActivity 使用了正确的 Factory（2.0 版本已修复）

### Q3: LiveData 观察不到数据

**原因**: 可能在使用废弃的 `pageStateEvent`

**解决**: 迁移到 `uiState` 和 `uiEvent`

### Q4: ProGuard 混淆后崩溃

**原因**: ProGuard 规则过时

**解决**: 使用 2.0 版本提供的新 ProGuard 规则

---

## 迁移检查清单

完成迁移后，请检查以下项目：

- [ ] 所有导入语句已更新
- [ ] 编译无错误和警告
- [ ] 所有 `@Deprecated` API 已迁移
- [ ] 单元测试通过
- [ ] UI 测试通过
- [ ] ProGuard 混淆测试通过
- [ ] 在真机上测试主要功能

---

## 获取帮助

如果遇到迁移问题：

1. 查看 [CHANGELOG.md](CHANGELOG.md) 了解详细变更
2. 查看 [API_COMPATIBILITY.md](API_COMPATIBILITY.md) 了解 API 兼容性
3. 提交 [GitHub Issue](https://github.com/yourusername/swallow/issues)
4. 加入技术交流群获取支持

---

## 回滚方案

如果迁移遇到严重问题，可以临时回滚到 1.x 版本：

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.swallow:swallow:1.0.3")
}
```

**注意**: 1.x 版本仅提供安全修复支持至 2025-06-30。
