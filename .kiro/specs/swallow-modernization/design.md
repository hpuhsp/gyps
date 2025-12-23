# Swallow 核心库现代化升级设计文档

## 概述

本设计文档详细说明了 Swallow 核心库从传统技术栈升级到现代化技术栈的技术方案。升级涉及构建工具、SDK 版本、依赖库、架构模式等多个方面，目标是打造一个高性能、易维护、面向未来的 MVVM 开发框架。

## 架构设计

### 当前架构分析

**模块结构**:
```
Gyps/
├── swallow/          # 核心框架库（本次升级重点）
├── base/             # UI 资源和组件库（依赖 swallow）
├── msc/              # 语音 SDK 集成模块
└── app/              # 测试应用模块
```

**核心层次**:
```
View Layer (Activity/Fragment)
    ↓
ViewModel Layer (BaseViewModel)
    ↓
Repository Layer (BaseRepository)
    ↓
Data Source Layer (Retrofit/Room)
```

### 升级后架构设计

**分层架构优化**:
```
┌─────────────────────────────────────────┐
│  Presentation Layer (View/Compose)      │
│  - BaseActivity (优化)                  │
│  - BaseFragment (优化)                  │
│  - Compose Support (新增)               │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│  ViewModel Layer                        │
│  - BaseViewModel (优化)                 │
│  - StateFlow/SharedFlow (新增)          │
│  - Compose ViewModel Integration        │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│  Domain Layer (可选，为 KMM 准备)       │
│  - Use Cases (接口化)                   │
│  - Business Logic (平台无关)            │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│  Repository Layer                       │
│  - IRepository (接口)                   │
│  - BaseRepository (实现)                │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│  Data Source Layer                      │
│  - Remote (Retrofit + Coroutines)       │
│  - Local (Room + Flow)                  │
│  - Cache (MMKV)                         │
└─────────────────────────────────────────┘
```

## 框架初始化和配置设计

### 核心设计原则

1. **配置集中在 swallow 核心库** - 所有关键配置逻辑都在 swallow 模块中实现
2. **最小化 app 模块的 Hilt 使用** - app 模块只需简单的初始化调用，无需编写大量 Hilt 模块
3. **DSL 配置方式** - 使用 Kotlin DSL 提供优雅的配置 API
4. **向后兼容** - 保持与现有 ManifestParser + meta-data 方式的兼容性

### 框架初始化架构

```
┌─────────────────────────────────────────┐
│  App Module (MyApplication)             │
│  - 简单的初始化调用                      │
│  - 无需 Hilt 模块                       │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│  SwallowFramework (swallow 核心库)      │
│  - DSL 配置接口                         │
│  - 框架初始化逻辑                       │
└─────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────┐
│  Internal Hilt Modules (swallow 内部)   │
│  - FrameworkConfigModule                │
│  - NetworkModule                        │
│  - DatabaseModule                       │
│  - ImageModule                          │
└─────────────────────────────────────────┘
```

### SwallowFramework 核心类设计

**位置**: `swallow/src/main/java/com/swallow/fly/SwallowFramework.kt`

```kotlin
/**
 * Swallow 框架核心类
 * 提供 DSL 配置和初始化功能
 */
object SwallowFramework {
    
    private var config: FrameworkConfig? = null
    private var isInitialized = false
    
    /**
     * 初始化框架（在 Application.onCreate 中调用）
     */
    fun init(context: Context, block: FrameworkConfigBuilder.() -> Unit) {
        if (isInitialized) {
            Timber.w("SwallowFramework already initialized")
            return
        }
        
        val builder = FrameworkConfigBuilder(context)
        builder.block()
        config = builder.build()
        
        // 初始化日志
        if (config!!.logConfig.enabled) {
            Timber.plant(Timber.DebugTree())
        }
        
        // 初始化 MMKV
        MMKV.initialize(context)
        
        isInitialized = true
        Timber.d("SwallowFramework initialized successfully")
    }
    
    /**
     * 获取框架配置
     */
    internal fun getConfig(): FrameworkConfig {
        checkNotNull(config) { "SwallowFramework not initialized. Call init() first." }
        return config!!
    }
    
    /**
     * 检查是否已初始化
     */
    fun isInitialized(): Boolean = isInitialized
}

/**
 * 框架配置构建器（DSL）
 */
class FrameworkConfigBuilder(private val context: Context) {
    
    // 网络配置
    var baseUrl: String = ""
    var connectTimeout: Long = 30L
    var readTimeout: Long = 30L
    var writeTimeout: Long = 30L
    var enableLogging: Boolean = BuildConfig.DEBUG
    var globalHttpHandler: GlobalHttpHandler? = null
    var responseErrorListener: ResponseErrorListener? = null
    
    // 图片加载配置
    var imageConfig: ImageConfig = ImageConfig()
    
    // 数据库配置
    var databaseName: String = "swallow_db"
    var enableDatabaseLogging: Boolean = BuildConfig.DEBUG
    
    // 日志配置
    var logConfig: LogConfig = LogConfig()
    
    /**
     * 配置网络层
     */
    fun network(block: NetworkConfigBuilder.() -> Unit) {
        val builder = NetworkConfigBuilder()
        builder.block()
        this.baseUrl = builder.baseUrl
        this.connectTimeout = builder.connectTimeout
        this.readTimeout = builder.readTimeout
        this.writeTimeout = builder.writeTimeout
        this.enableLogging = builder.enableLogging
        this.globalHttpHandler = builder.globalHttpHandler
        this.responseErrorListener = builder.responseErrorListener
    }
    
    /**
     * 配置图片加载
     */
    fun image(block: ImageConfigBuilder.() -> Unit) {
        val builder = ImageConfigBuilder()
        builder.block()
        this.imageConfig = builder.build()
    }
    
    /**
     * 配置数据库
     */
    fun database(block: DatabaseConfigBuilder.() -> Unit) {
        val builder = DatabaseConfigBuilder()
        builder.block()
        this.databaseName = builder.databaseName
        this.enableDatabaseLogging = builder.enableLogging
    }
    
    /**
     * 配置日志
     */
    fun log(block: LogConfigBuilder.() -> Unit) {
        val builder = LogConfigBuilder()
        builder.block()
        this.logConfig = builder.build()
    }
    
    internal fun build(): FrameworkConfig {
        require(baseUrl.isNotEmpty()) { "baseUrl must not be empty" }
        
        return FrameworkConfig(
            context = context,
            baseUrl = baseUrl,
            connectTimeout = connectTimeout,
            readTimeout = readTimeout,
            writeTimeout = writeTimeout,
            enableNetworkLogging = enableLogging,
            globalHttpHandler = globalHttpHandler,
            responseErrorListener = responseErrorListener,
            imageConfig = imageConfig,
            databaseName = databaseName,
            enableDatabaseLogging = enableDatabaseLogging,
            logConfig = logConfig
        )
    }
}

/**
 * 网络配置构建器
 */
class NetworkConfigBuilder {
    var baseUrl: String = ""
    var connectTimeout: Long = 30L
    var readTimeout: Long = 30L
    var writeTimeout: Long = 30L
    var enableLogging: Boolean = BuildConfig.DEBUG
    var globalHttpHandler: GlobalHttpHandler? = null
    var responseErrorListener: ResponseErrorListener? = null
}

/**
 * 图片配置构建器
 */
class ImageConfigBuilder {
    var memoryCacheSize: Long = 1024 * 1024 * 20L // 20MB
    var diskCacheSize: Long = 1024 * 1024 * 100L // 100MB
    var defaultPlaceholder: Int = R.drawable.img_default_normal_thumb
    var defaultError: Int = R.drawable.img_default_error_thumb
    
    fun build() = ImageConfig(
        memoryCacheSize = memoryCacheSize,
        diskCacheSize = diskCacheSize,
        defaultPlaceholder = defaultPlaceholder,
        defaultError = defaultError
    )
}

/**
 * 数据库配置构建器
 */
class DatabaseConfigBuilder {
    var databaseName: String = "swallow_db"
    var enableLogging: Boolean = BuildConfig.DEBUG
}

/**
 * 日志配置构建器
 */
class LogConfigBuilder {
    var enabled: Boolean = BuildConfig.DEBUG
    var tag: String = "Swallow"
    
    fun build() = LogConfig(
        enabled = enabled,
        tag = tag
    )
}

/**
 * 框架配置数据类
 */
data class FrameworkConfig(
    val context: Context,
    val baseUrl: String,
    val connectTimeout: Long,
    val readTimeout: Long,
    val writeTimeout: Long,
    val enableNetworkLogging: Boolean,
    val globalHttpHandler: GlobalHttpHandler?,
    val responseErrorListener: ResponseErrorListener?,
    val imageConfig: ImageConfig,
    val databaseName: String,
    val enableDatabaseLogging: Boolean,
    val logConfig: LogConfig
)

data class ImageConfig(
    val memoryCacheSize: Long = 1024 * 1024 * 20L,
    val diskCacheSize: Long = 1024 * 1024 * 100L,
    val defaultPlaceholder: Int = 0,
    val defaultError: Int = 0
)

data class LogConfig(
    val enabled: Boolean = true,
    val tag: String = "Swallow"
)
```

### FrameworkConfigModule 实现

**位置**: `swallow/src/main/java/com/swallow/fly/di/FrameworkConfigModule.kt`

```kotlin
/**
 * 框架配置 Hilt 模块（内部使用）
 * 将 SwallowFramework 的配置注入到 Hilt 依赖图中
 */
@Module
@InstallIn(SingletonComponent::class)
internal object FrameworkConfigModule {
    
    @Provides
    @Singleton
    fun provideFrameworkConfig(): FrameworkConfig {
        return SwallowFramework.getConfig()
    }
    
    @Provides
    @Singleton
    fun provideContext(config: FrameworkConfig): Context {
        return config.context
    }
    
    @Provides
    @Singleton
    fun provideBaseUrl(config: FrameworkConfig): HttpUrl {
        return config.baseUrl.toHttpUrl()
    }
    
    @Provides
    @Singleton
    fun provideGlobalHttpHandler(config: FrameworkConfig): GlobalHttpHandler? {
        return config.globalHttpHandler
    }
    
    @Provides
    @Singleton
    fun provideResponseErrorListener(config: FrameworkConfig): ResponseErrorListener? {
        return config.responseErrorListener
    }
}
```

### App 模块使用示例

**位置**: `app/src/main/java/com/swallow/gyps/MyApplication.kt`

```kotlin
@HiltAndroidApp
class MyApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化 Swallow 框架（使用 DSL 配置）
        SwallowFramework.init(this) {
            // 网络配置
            network {
                baseUrl = "https://api.github.com/"
                connectTimeout = 30L
                readTimeout = 30L
                writeTimeout = 30L
                enableLogging = BuildConfig.DEBUG
                
                // 可选：自定义 HTTP 处理器
                globalHttpHandler = object : GlobalHttpHandler {
                    override fun onHttpRequestBefore(
                        chain: Interceptor.Chain,
                        request: Request
                    ): Request {
                        // 添加通用请求头
                        return request.newBuilder()
                            .addHeader("User-Agent", "Gyps-Android")
                            .addHeader("Accept-Language", "zh-CN")
                            .build()
                    }
                    
                    override fun onHttpResultResponse(
                        httpResult: String?,
                        chain: Interceptor.Chain,
                        response: Response
                    ): Response {
                        // 处理响应
                        return response
                    }
                }
                
                // 可选：自定义错误监听器
                responseErrorListener = object : ResponseErrorListener {
                    override fun handleResponseError(context: Context?, t: Throwable) {
                        Timber.e(t, "Network error")
                        // 显示错误提示
                    }
                }
            }
            
            // 图片加载配置
            image {
                memoryCacheSize = 1024 * 1024 * 20L // 20MB
                diskCacheSize = 1024 * 1024 * 100L // 100MB
                defaultPlaceholder = R.drawable.img_default_normal_thumb
                defaultError = R.drawable.img_default_error_thumb
            }
            
            // 数据库配置
            database {
                databaseName = "gyps_db"
                enableLogging = BuildConfig.DEBUG
            }
            
            // 日志配置
            log {
                enabled = BuildConfig.DEBUG
                tag = "Gyps"
            }
        }
    }
}
```

### 向后兼容：支持 ManifestParser 方式

为了保持向后兼容，swallow 仍然支持通过 AndroidManifest.xml 的 meta-data 方式配置：

```kotlin
/**
 * 配置模块接口（向后兼容）
 */
interface ConfigModule {
    fun applyOptions(context: Context?, builder: FrameworkConfigBuilder)
}

/**
 * Manifest 解析器
 */
internal object ManifestParser {
    
    private const val MODULE_VALUE = "ConfigModule"
    
    fun parse(context: Context): ConfigModule? {
        try {
            val appInfo = context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )
            
            val metaData = appInfo.metaData ?: return null
            
            for (key in metaData.keySet()) {
                val value = metaData.get(key)
                if (MODULE_VALUE == value) {
                    val clazz = Class.forName(key)
                    return clazz.newInstance() as? ConfigModule
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse ConfigModule from manifest")
        }
        
        return null
    }
}

/**
 * 支持 ManifestParser 的初始化方式
 */
fun SwallowFramework.initWithManifest(context: Context) {
    val configModule = ManifestParser.parse(context)
    
    init(context) {
        // 应用 manifest 配置
        configModule?.applyOptions(context, this)
    }
}
```

**App 模块使用 ManifestParser 方式**:

```kotlin
// AndroidManifest.xml
<application>
    <meta-data
        android:name="com.swallow.gyps.app.GlobalConfiguration"
        android:value="ConfigModule" />
</application>

// GlobalConfiguration.kt
class GlobalConfiguration : ConfigModule {
    override fun applyOptions(context: Context?, builder: FrameworkConfigBuilder) {
        builder.network {
            baseUrl = "https://api.github.com/"
            connectTimeout = 30L
            enableLogging = BuildConfig.DEBUG
        }
        
        builder.image {
            memoryCacheSize = 1024 * 1024 * 20L
        }
    }
}

// MyApplication.kt
@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 使用 ManifestParser 方式初始化
        SwallowFramework.initWithManifest(this)
    }
}
```

### 优势总结

1. **App 模块简洁** - 只需在 Application 中调用一次初始化，无需编写 Hilt 模块
2. **配置集中** - 所有配置逻辑都在 swallow 核心库中
3. **DSL 优雅** - 使用 Kotlin DSL 提供类型安全的配置 API
4. **向后兼容** - 支持现有的 ManifestParser 方式
5. **易于测试** - 配置可以在测试中轻松模拟

## 组件和接口设计

### 1. 构建配置组件

#### Version Catalog 设计

**文件**: `gradle/libs.versions.toml`

```toml
[versions]
# Build Tools
agp = "8.7.3"
kotlin = "2.0.21"
ksp = "2.0.21-1.0.28"

# SDK
compileSdk = "35"
targetSdk = "35"
minSdk = "24"

# AndroidX Core
core-ktx = "1.15.0"
appcompat = "1.7.0"
material = "1.12.0"

# Lifecycle
lifecycle = "2.8.7"

# Navigation
navigation = "2.8.5"

# Room
room = "2.6.1"

# Hilt
hilt = "2.54"

# Coroutines
coroutines = "1.9.0"

# Network
retrofit = "2.11.0"
okhttp = "4.12.0"
gson = "2.11.0"

# Image Loading
glide = "4.16.0"

# Utilities
timber = "5.0.1"
mmkv = "2.0.1"

[libraries]
# AndroidX
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "core-ktx" }
androidx-appcompat = { group = "androidx.appcompat", name = "appcompat", version.ref = "appcompat" }
material = { group = "com.google.android.material", name = "material", version.ref = "material" }

# Lifecycle
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycle" }
androidx-lifecycle-viewmodel-ktx = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-ktx", version.ref = "lifecycle" }
androidx-lifecycle-livedata-ktx = { group = "androidx.lifecycle", name = "lifecycle-livedata-ktx", version.ref = "lifecycle" }

# ... 更多依赖定义

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
android-library = { id = "com.android.library", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-parcelize = { id = "org.jetbrains.kotlin.plugin.parcelize", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
```



#### Gradle Kotlin DSL 结构

**根目录 build.gradle.kts**:
```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}
```

**swallow/build.gradle.kts** (核心库):
```kotlin
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("maven-publish")
}

android {
    namespace = "com.swallow.fly"
    compileSdk = libs.versions.compileSdk.get().toInt()
    
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.FlowPreview"
        )
    }
    
    buildFeatures {
        viewBinding = true
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    // AndroidX Core
    api(libs.androidx.core.ktx)
    api(libs.androidx.appcompat)
    api(libs.material)
    
    // Lifecycle
    api(libs.androidx.lifecycle.runtime.ktx)
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.androidx.lifecycle.livedata.ktx)
    
    // Hilt
    api(libs.hilt.android)
    ksp(libs.hilt.compiler)
    
    // Room
    api(libs.androidx.room.runtime)
    api(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    
    // Retrofit
    api(libs.retrofit)
    api(libs.retrofit.converter.gson)
    api(libs.okhttp)
    api(libs.okhttp.logging.interceptor)
    
    // Glide
    api(libs.glide)
    ksp(libs.glide.ksp)
    
    // Coroutines
    api(libs.kotlinx.coroutines.android)
    api(libs.kotlinx.coroutines.core)
    
    // Compose (可选支持)
    api(platform(libs.compose.bom))
    api(libs.compose.ui)
    api(libs.compose.material3)
    api(libs.compose.ui.tooling.preview)
    
    // Utilities
    api(libs.timber)
    api(libs.mmkv)
}
```

### 2. 基础组件重构

#### BaseActivity 现代化设计

**优化点**:
1. 使用 `by viewModels()` 委托
2. 使用 Activity Result API
3. 移除 ProgressDialog，使用 Material 进度指示器
4. 使用 `repeatOnLifecycle` 观察 Flow
5. 简化权限处理

**新设计**:
```kotlin
abstract class BaseActivity<VM : BaseViewModel, VB : ViewBinding> : AppCompatActivity() {
    
    // 使用委托简化 ViewModel 创建
    protected abstract val viewModel: VM
    
    // ViewBinding
    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!
    
    protected abstract val bindingInflater: (LayoutInflater) -> VB
    
    // Activity Result API
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        onPermissionsResult(permissions)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingInflater(layoutInflater)
        setContentView(binding.root)
        
        setupImmersionBar()
        initView(savedInstanceState)
        observeViewModel()
        initData(savedInstanceState)
    }
    
    // 使用 repeatOnLifecycle 观察 Flow
    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        handleUiState(state)
                    }
                }
                launch {
                    viewModel.uiEvent.collect { event ->
                        handleUiEvent(event)
                    }
                }
            }
        }
    }
    
    // 现代化权限请求
    protected fun requestPermissions(permissions: Array<String>) {
        permissionLauncher.launch(permissions)
    }
    
    protected open fun onPermissionsResult(permissions: Map<String, Boolean>) {
        // 子类实现
    }
    
    // Material Design 进度指示器
    private var loadingDialog: MaterialAlertDialogBuilder? = null
    
    protected fun showLoading(message: String? = null) {
        hideLoading()
        loadingDialog = MaterialAlertDialogBuilder(this)
            .setView(R.layout.dialog_loading)
            .setCancelable(false)
            .also { it.show() }
    }
    
    protected fun hideLoading() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }
    
    abstract fun initView(savedInstanceState: Bundle?)
    abstract fun initData(savedInstanceState: Bundle?)
    abstract fun handleUiState(state: UiState)
    abstract fun handleUiEvent(event: UiEvent)
    
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
```

#### BaseViewModel 现代化设计

**优化点**:
1. 使用 StateFlow 替代 LiveData
2. 使用 SharedFlow 处理一次性事件
3. 结构化异常处理
4. 支持 Compose

**新设计**:
```kotlin
abstract class BaseViewModel : ViewModel() {
    
    // UI 状态 - 使用 StateFlow
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    // UI 事件 - 使用 SharedFlow (一次性事件)
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()
    
    // 加载状态
    protected fun showLoading(message: String? = null) {
        _uiState.value = UiState.Loading(message)
    }
    
    protected fun hideLoading() {
        _uiState.value = UiState.Idle
    }
    
    // 错误处理
    protected fun showError(error: Throwable) {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ShowError(error.message ?: "Unknown error"))
        }
    }
    
    // Toast 消息
    protected fun showToast(message: String) {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ShowToast(message))
        }
    }
    
    // 结构化异常处理
    protected fun <T> Flow<T>.handleErrors(): Flow<T> = catch { error ->
        handleError(error)
    }
    
    private fun handleError(error: Throwable) {
        when (error) {
            is IOException -> showError(Throwable("Network error"))
            is HttpException -> showError(Throwable("Server error: ${error.code()}"))
            else -> showError(error)
        }
    }
}

// UI 状态密封类
sealed class UiState {
    object Idle : UiState()
    data class Loading(val message: String? = null) : UiState()
    data class Success<T>(val data: T) : UiState()
    data class Error(val message: String) : UiState()
}

// UI 事件密封类
sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
    data class ShowError(val message: String) : UiEvent()
    data class Navigate(val route: String) : UiEvent()
}
```



#### BaseRepository 现代化设计

**优化点**:
1. 接口化设计（为 KMM 准备）
2. 使用 Flow 替代回调
3. 统一错误处理
4. 支持缓存策略

**新设计**:
```kotlin
// Repository 接口（平台无关）
interface IRepository {
    suspend fun <T> executeRequest(request: suspend () -> T): Result<T>
}

// 基础实现
abstract class BaseRepository : IRepository {
    
    @Inject
    lateinit var repositoryManager: RepositoryManager
    
    // 获取 Retrofit Service
    protected fun <T> obtainService(service: Class<T>): T {
        return repositoryManager.obtainRetrofitService(service)
    }
    
    // 统一请求执行（带错误处理）
    override suspend fun <T> executeRequest(request: suspend () -> T): Result<T> {
        return try {
            Result.success(request())
        } catch (e: Exception) {
            Result.failure(repositoryManager.handleResponseError(e))
        }
    }
    
    // Flow 包装器
    protected fun <T> flowRequest(request: suspend () -> T): Flow<Result<T>> = flow {
        emit(executeRequest(request))
    }.flowOn(Dispatchers.IO)
    
    // 带缓存的请求
    protected fun <T> cachedFlowRequest(
        cacheKey: String,
        request: suspend () -> T
    ): Flow<Result<T>> = flow {
        // 先发射缓存数据
        val cached = repositoryManager.getCache<T>(cacheKey)
        if (cached != null) {
            emit(Result.success(cached))
        }
        
        // 再请求网络数据
        val result = executeRequest(request)
        if (result.isSuccess) {
            repositoryManager.saveCache(cacheKey, result.getOrNull())
        }
        emit(result)
    }.flowOn(Dispatchers.IO)
}
```

### 3. 网络层设计

#### Retrofit 配置（支持 Kotlin 协程）

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    private const val TIMEOUT = 30L
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        globalHttpHandler: GlobalHttpHandler?
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .apply {
                // 添加全局请求拦截器
                globalHttpHandler?.let { handler ->
                    addInterceptor { chain ->
                        val request = handler.onHttpRequestBefore(chain, chain.request())
                        val response = chain.proceed(request)
                        handler.onHttpResultResponse(null, chain, response)
                    }
                }
                
                // 添加日志拦截器（仅 Debug）
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                }
            }
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        baseUrl: HttpUrl,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .serializeNulls()
            .create()
    }
}
```

### 4. 数据库层设计

#### Room 配置（使用 KSP）

```kotlin
@Database(
    entities = [AppCacheEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cacheDao(): AppCacheDao
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "swallow_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    fun provideCacheDao(database: AppDatabase): AppCacheDao {
        return database.cacheDao()
    }
}

// DAO 使用 Flow
@Dao
interface AppCacheDao {
    @Query("SELECT * FROM cache WHERE key = :key")
    fun getCacheFlow(key: String): Flow<AppCacheEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCache(cache: AppCacheEntity)
    
    @Query("DELETE FROM cache WHERE key = :key")
    suspend fun deleteCache(key: String)
}
```

### 5. 图片加载层设计

#### Glide 5.x 配置（使用 KSP）

```kotlin
// AppGlideModule (在 app 模块中)
@GlideModule
class MyAppGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDefaultRequestOptions(
            RequestOptions()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        )
    }
}

// Glide 扩展（在 swallow 模块中）
@GlideExtension
object SwallowGlideExtension {
    
    @JvmStatic
    @GlideOption
    fun roundedCorners(options: BaseRequestOptions<*>, radius: Int): BaseRequestOptions<*> {
        return options.transform(RoundedCorners(radius))
    }
    
    @JvmStatic
    @GlideOption
    fun circularCrop(options: BaseRequestOptions<*>): BaseRequestOptions<*> {
        return options.circleCrop()
    }
}

// Kotlin 扩展函数
fun ImageView.loadImage(
    url: String?,
    placeholder: Int = R.drawable.img_default_normal_thumb,
    error: Int = R.drawable.img_default_error_thumb
) {
    Glide.with(this)
        .load(url)
        .placeholder(placeholder)
        .error(error)
        .into(this)
}
```



## 数据模型

### 1. 网络响应模型

```kotlin
// 通用响应包装
data class ApiResponse<T>(
    val code: Int,
    val message: String?,
    val data: T?
)

// Result 扩展
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val exception: Throwable) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}

// 扩展函数
fun <T> NetworkResult<T>.onSuccess(action: (T) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Success) action(data)
    return this
}

fun <T> NetworkResult<T>.onError(action: (Throwable) -> Unit): NetworkResult<T> {
    if (this is NetworkResult.Error) action(exception)
    return this
}
```

### 2. 数据库实体模型

```kotlin
@Entity(tableName = "cache")
data class AppCacheEntity(
    @PrimaryKey
    val key: String,
    val value: String,
    val timestamp: Long = System.currentTimeMillis(),
    val expireTime: Long = 0L // 0 表示永不过期
)

// 类型转换器
class Converters {
    @TypeConverter
    fun fromString(value: String?): List<String>? {
        return value?.split(",")
    }
    
    @TypeConverter
    fun toString(list: List<String>?): String? {
        return list?.joinToString(",")
    }
}
```

### 3. UI 状态模型

```kotlin
// 页面状态
data class PageState<T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val error: String? = null,
    val isRefreshing: Boolean = false
)

// 列表状态
data class ListState<T>(
    val items: List<T> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val error: String? = null
)
```

## Compose 支持设计

### 1. Compose 集成配置

```kotlin
// build.gradle.kts
android {
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

dependencies {
    // Compose BOM
    api(platform("androidx.compose:compose-bom:2024.02.00"))
    api("androidx.compose.ui:ui")
    api("androidx.compose.material3:material3")
    api("androidx.compose.ui:ui-tooling-preview")
    debugApi("androidx.compose.ui:ui-tooling")
    
    // Compose Integration
    api("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    api("androidx.navigation:navigation-compose:2.7.7")
    api("androidx.hilt:hilt-navigation-compose:1.2.0")
    
    // Accompanist (辅助库)
    api("com.google.accompanist:accompanist-systemuicontroller:0.34.0")
}
```

### 2. Compose 基础组件

```kotlin
// 基础 Composable 函数
@Composable
fun <T> LoadingContent(
    state: PageState<T>,
    onRetry: () -> Unit,
    content: @Composable (T) -> Unit
) {
    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        state.error != null -> {
            ErrorView(
                message = state.error,
                onRetry = onRetry
            )
        }
        state.data != null -> {
            content(state.data)
        }
    }
}

// ViewModel 集成
@Composable
inline fun <reified VM : ViewModel> hiltViewModel(): VM {
    return androidx.hilt.navigation.compose.hiltViewModel()
}
```

## KMM 准备设计

### 1. 模块结构规划

```
swallow/
├── src/
│   ├── commonMain/          # 共享代码（未来）
│   │   └── kotlin/
│   │       ├── domain/      # 业务逻辑
│   │       ├── data/        # 数据接口
│   │       └── utils/       # 工具类
│   ├── androidMain/         # Android 特定代码
│   │   └── kotlin/
│   │       ├── base/
│   │       ├── http/
│   │       └── db/
│   └── iosMain/             # iOS 特定代码（未来）
```

### 2. 接口抽象设计

```kotlin
// 平台无关的接口定义
interface INetworkClient {
    suspend fun <T> get(url: String, params: Map<String, Any>): Result<T>
    suspend fun <T> post(url: String, body: Any): Result<T>
}

interface ILocalStorage {
    suspend fun save(key: String, value: String)
    suspend fun get(key: String): String?
    suspend fun remove(key: String)
}

interface ILogger {
    fun d(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable? = null)
}

// Android 实现
class AndroidNetworkClient @Inject constructor(
    private val retrofit: Retrofit
) : INetworkClient {
    // 实现细节
}

class AndroidLocalStorage @Inject constructor(
    private val mmkv: MMKV
) : ILocalStorage {
    // 实现细节
}
```

## 错误处理策略

### 1. 全局异常处理

```kotlin
class GlobalExceptionHandler @Inject constructor() {
    
    fun handleException(throwable: Throwable): AppException {
        return when (throwable) {
            is IOException -> AppException.NetworkException(throwable)
            is HttpException -> {
                when (throwable.code()) {
                    401 -> AppException.UnauthorizedException()
                    403 -> AppException.ForbiddenException()
                    404 -> AppException.NotFoundException()
                    500 -> AppException.ServerException()
                    else -> AppException.HttpException(throwable.code(), throwable.message())
                }
            }
            is JsonSyntaxException -> AppException.ParseException(throwable)
            else -> AppException.UnknownException(throwable)
        }
    }
}

sealed class AppException(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {
    class NetworkException(cause: Throwable) : AppException("网络连接失败", cause)
    class UnauthorizedException : AppException("未授权，请重新登录")
    class ForbiddenException : AppException("无权限访问")
    class NotFoundException : AppException("请求的资源不存在")
    class ServerException : AppException("服务器错误")
    class HttpException(val code: Int, message: String?) : AppException("HTTP错误: $code - $message")
    class ParseException(cause: Throwable) : AppException("数据解析失败", cause)
    class UnknownException(cause: Throwable) : AppException("未知错误", cause)
}
```

### 2. Repository 层错误处理

```kotlin
abstract class BaseRepository {
    
    @Inject
    lateinit var exceptionHandler: GlobalExceptionHandler
    
    protected suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Result<T> {
        return try {
            Result.success(apiCall())
        } catch (e: Exception) {
            val appException = exceptionHandler.handleException(e)
            Result.failure(appException)
        }
    }
    
    protected fun <T> safeFlowApiCall(
        apiCall: suspend () -> T
    ): Flow<Result<T>> = flow {
        emit(safeApiCall(apiCall))
    }.flowOn(Dispatchers.IO)
}
```



## 测试策略

### 1. 单元测试设计

```kotlin
// ViewModel 测试
@ExperimentalCoroutinesApi
class MainViewModelTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    
    private lateinit var viewModel: MainViewModel
    private lateinit var repository: FakeMainRepository
    
    @Before
    fun setup() {
        repository = FakeMainRepository()
        viewModel = MainViewModel(repository)
    }
    
    @Test
    fun `when load data success, uiState should be success`() = runTest {
        // Given
        val expectedData = listOf("item1", "item2")
        repository.setData(expectedData)
        
        // When
        viewModel.loadData()
        
        // Then
        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(expectedData, (state as UiState.Success).data)
    }
}

// Repository 测试
class MainRepositoryTest {
    
    private lateinit var repository: MainRepository
    private lateinit var apiService: FakeApiService
    private lateinit var cacheDao: FakeCacheDao
    
    @Before
    fun setup() {
        apiService = FakeApiService()
        cacheDao = FakeCacheDao()
        repository = MainRepository(apiService, cacheDao)
    }
    
    @Test
    fun `when fetch data, should return success result`() = runTest {
        // Given
        val expectedData = "test data"
        apiService.setResponse(expectedData)
        
        // When
        val result = repository.fetchData()
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedData, result.getOrNull())
    }
}
```

### 2. 集成测试设计

```kotlin
@HiltAndroidTest
class MainActivityTest {
    
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
    
    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    
    @Before
    fun setup() {
        hiltRule.inject()
    }
    
    @Test
    fun testMainActivityDisplay() {
        onView(withId(R.id.main_content))
            .check(matches(isDisplayed()))
    }
}
```

## 性能优化策略

### 1. 编译性能优化

**KSP vs KAPT 对比**:
- KSP 编译速度提升 25-40%
- 更好的 Kotlin 支持
- 更少的内存占用

**Gradle 配置优化**:
```kotlin
// gradle.properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=1024m -XX:+HeapDumpOnOutOfMemoryError
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=true

kotlin.incremental=true
kotlin.incremental.usePreciseJavaTracking=true
kotlin.caching.enabled=true
kotlin.parallel.tasks.in.project=true
```

### 2. 运行时性能优化

**协程优化**:
```kotlin
// 使用结构化并发
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : BaseViewModel() {
    
    fun loadData() {
        viewModelScope.launch {
            // 并行请求
            val deferredUser = async { repository.getUser() }
            val deferredPosts = async { repository.getPosts() }
            
            val user = deferredUser.await()
            val posts = deferredPosts.await()
            
            // 处理结果
            _uiState.value = UiState.Success(UserWithPosts(user, posts))
        }
    }
}
```

**图片加载优化**:
```kotlin
// Glide 配置
@GlideModule
class MyAppGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        // 内存缓存大小
        val memoryCacheSizeBytes = 1024 * 1024 * 20 // 20MB
        builder.setMemoryCache(LruResourceCache(memoryCacheSizeBytes.toLong()))
        
        // 磁盘缓存大小
        val diskCacheSizeBytes = 1024 * 1024 * 100 // 100MB
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, diskCacheSizeBytes.toLong()))
    }
}
```

**数据库优化**:
```kotlin
@Database(entities = [...], version = 1)
abstract class AppDatabase : RoomDatabase() {
    
    companion object {
        fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "app_db")
                .setQueryCallback({ sqlQuery, bindArgs ->
                    Timber.d("SQL Query: $sqlQuery SQL Args: $bindArgs")
                }, Executors.newSingleThreadExecutor())
                .build()
        }
    }
}
```

## 迁移策略

### 1. 渐进式迁移计划

**阶段 1: 构建工具升级** (1-2 天)
- 升级 AGP、Gradle、Kotlin
- 迁移到 Kotlin DSL
- 创建 Version Catalog

**阶段 2: 依赖库升级** (2-3 天)
- 升级 Jetpack 组件
- 升级网络库
- 升级图片加载库
- 升级工具库

**阶段 3: KSP 迁移** (1-2 天)
- Hilt 迁移到 KSP
- Room 迁移到 KSP
- Glide 迁移到 KSP

**阶段 4: 代码重构** (3-5 天)
- BaseActivity 重构
- BaseViewModel 重构
- BaseRepository 重构
- 权限处理重构

**阶段 5: Compose 支持** (2-3 天)
- 添加 Compose 依赖
- 创建基础 Composable 组件
- ViewModel Compose 集成

**阶段 6: 测试和验证** (2-3 天)
- 单元测试
- 集成测试
- 功能验证
- 性能测试

**阶段 7: 文档更新** (1 天)
- 更新 README
- 编写迁移指南
- 更新 API 文档

### 2. 兼容性处理

**破坏性变更列表**:

1. **最低 SDK 版本**: minSdk 从 21 升级到 24
2. **JDK 版本**: 从 JDK 8 升级到 JDK 17
3. **BaseActivity API**: 
   - 移除 `modelClass` 属性，使用 `by viewModels()` 委托
   - 移除 `onRequestPermissionsResult`，使用 Activity Result API
4. **BaseViewModel API**:
   - `pageStateEvent: LiveData` 改为 `uiState: StateFlow`
   - 新增 `uiEvent: SharedFlow` 处理一次性事件
5. **Repository API**:
   - 返回类型从回调改为 `Flow<Result<T>>`
6. **Glide**: 从 4.11.0 升级到 5.0.5，API 有变化

**迁移指南示例**:

```kotlin
// 旧代码
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    override val modelClass: Class<MainViewModel>
        get() = MainViewModel::class.java
    
    override fun initData(savedInstanceState: Bundle?) {
        mViewModel.data.observe(this) { data ->
            // 处理数据
        }
    }
}

// 新代码
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    override val viewModel: MainViewModel by viewModels()
    
    override fun initData(savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Success -> {
                            // 处理数据
                        }
                        // ...
                    }
                }
            }
        }
    }
}
```



## 依赖版本管理

### Version Catalog 完整配置

**gradle/libs.versions.toml**:

```toml
[versions]
# Build Tools
agp = "8.7.3"
kotlin = "2.0.21"
ksp = "2.0.21-1.0.28"

# SDK Versions
compileSdk = "35"
targetSdk = "35"
minSdk = "24"

# AndroidX Core
core-ktx = "1.15.0"
appcompat = "1.7.0"
activity-ktx = "1.9.3"
fragment-ktx = "1.8.5"
material = "1.12.0"
constraintlayout = "2.2.0"
recyclerview = "1.3.2"
swiperefreshlayout = "1.1.0"
vectordrawable = "1.2.0"

# Lifecycle
lifecycle = "2.8.7"

# Navigation
navigation = "2.8.5"

# Room
room = "2.6.1"

# Paging
paging = "3.3.5"

# Hilt
hilt = "2.54"

# Coroutines
coroutines = "1.9.0"

# Network
retrofit = "2.11.0"
okhttp = "4.12.0"
gson = "2.11.0"
retrofit-url-manager = "1.4.0"

# Image Loading
glide = "4.16.0"

# Compose
compose-bom = "2024.12.01"
compose-compiler = "1.5.8"

# Utilities
timber = "5.0.1"
mmkv = "2.0.1"
utilcodex = "1.31.1"
immersionbar = "3.2.2"

# EventBus
eventbus = "3.3.1"

# Test
junit = "4.13.2"
androidx-test-ext-junit = "1.2.1"
espresso-core = "3.6.1"

[libraries]
# AndroidX Core
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "core-ktx" }
androidx-appcompat = { group = "androidx.appcompat", name = "appcompat", version.ref = "appcompat" }
androidx-activity-ktx = { group = "androidx.activity", name = "activity-ktx", version.ref = "activity-ktx" }
androidx-fragment-ktx = { group = "androidx.fragment", name = "fragment-ktx", version.ref = "fragment-ktx" }
material = { group = "com.google.android.material", name = "material", version.ref = "material" }
androidx-constraintlayout = { group = "androidx.constraintlayout", name = "constraintlayout", version.ref = "constraintlayout" }
androidx-recyclerview = { group = "androidx.recyclerview", name = "recyclerview", version.ref = "recyclerview" }
androidx-swiperefreshlayout = { group = "androidx.swiperefreshlayout", name = "swiperefreshlayout", version.ref = "swiperefreshlayout" }
androidx-vectordrawable = { group = "androidx.vectordrawable", name = "vectordrawable", version.ref = "vectordrawable" }

# Lifecycle
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycle" }
androidx-lifecycle-viewmodel-ktx = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-ktx", version.ref = "lifecycle" }
androidx-lifecycle-livedata-ktx = { group = "androidx.lifecycle", name = "lifecycle-livedata-ktx", version.ref = "lifecycle" }
androidx-lifecycle-common-java8 = { group = "androidx.lifecycle", name = "lifecycle-common-java8", version.ref = "lifecycle" }

# Navigation
androidx-navigation-fragment-ktx = { group = "androidx.navigation", name = "navigation-fragment-ktx", version.ref = "navigation" }
androidx-navigation-ui-ktx = { group = "androidx.navigation", name = "navigation-ui-ktx", version.ref = "navigation" }

# Room
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }

# Paging
androidx-paging-runtime = { group = "androidx.paging", name = "paging-runtime-ktx", version.ref = "paging" }

# Hilt
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }

# Coroutines
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
kotlinx-coroutines-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version.ref = "coroutines" }

# Network
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-converter-gson = { group = "com.squareup.retrofit2", name = "converter-gson", version.ref = "retrofit" }
okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
okhttp-logging-interceptor = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "okhttp" }
gson = { group = "com.google.code.gson", name = "gson", version.ref = "gson" }
retrofit-url-manager = { group = "me.jessyan", name = "retrofit-url-manager", version.ref = "retrofit-url-manager" }

# Image Loading
glide = { group = "com.github.bumptech.glide", name = "glide", version.ref = "glide" }
glide-ksp = { group = "com.github.bumptech.glide", name = "ksp", version.ref = "glide" }
glide-okhttp3-integration = { group = "com.github.bumptech.glide", name = "okhttp3-integration", version.ref = "glide" }

# Compose
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
compose-material3 = { group = "androidx.compose.material3", name = "material3" }
compose-runtime = { group = "androidx.compose.runtime", name = "runtime" }

# Utilities
timber = { group = "com.jakewharton.timber", name = "timber", version.ref = "timber" }
mmkv = { group = "com.tencent", name = "mmkv", version.ref = "mmkv" }
utilcodex = { group = "com.blankj", name = "utilcodex", version.ref = "utilcodex" }
immersionbar = { group = "com.geyifeng.immersionbar", name = "immersionbar", version.ref = "immersionbar" }
immersionbar-components = { group = "com.geyifeng.immersionbar", name = "immersionbar-components", version.ref = "immersionbar" }

# EventBus
eventbus = { group = "org.greenrobot", name = "eventbus", version.ref = "eventbus" }

# Test
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-test-ext-junit = { group = "androidx.test.ext", name = "junit", version.ref = "androidx-test-ext-junit" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espresso-core" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
android-library = { id = "com.android.library", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-parcelize = { id = "org.jetbrains.kotlin.plugin.parcelize", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
```

## 风险评估和缓解策略

### 1. 技术风险

| 风险 | 影响 | 概率 | 缓解策略 |
|------|------|------|----------|
| KSP 兼容性问题 | 高 | 中 | 逐个库测试，准备回退方案 |
| Glide 5.x API 变化 | 中 | 高 | 详细阅读迁移文档，创建适配层 |
| Retrofit 3.x 破坏性变更 | 高 | 低 | 使用 2.11.x 稳定版本 |
| 依赖冲突 | 中 | 高 | 使用 Version Catalog 统一管理 |
| 编译失败 | 高 | 中 | 分阶段升级，每步验证 |

### 2. 业务风险

| 风险 | 影响 | 概率 | 缓解策略 |
|------|------|------|----------|
| 功能回归 | 高 | 中 | 完整的测试覆盖 |
| 性能下降 | 中 | 低 | 性能基准测试 |
| 用户体验变化 | 低 | 低 | UI 保持一致 |

### 3. 时间风险

| 风险 | 影响 | 概率 | 缓解策略 |
|------|------|------|----------|
| 升级时间超预期 | 中 | 中 | 预留缓冲时间 |
| 测试时间不足 | 高 | 中 | 自动化测试 |
| 文档编写延迟 | 低 | 低 | 边开发边文档 |

## 验收标准

### 1. 功能验收

- [ ] 所有模块成功编译
- [ ] app 模块成功运行
- [ ] MVVM 架构正常工作
- [ ] 网络请求正常
- [ ] 数据库操作正常
- [ ] 图片加载正常
- [ ] 依赖注入正常
- [ ] 生命周期管理正常

### 2. 性能验收

- [ ] KSP 编译速度提升 ≥ 25%
- [ ] 应用启动时间 ≤ 原有时间
- [ ] 内存占用 ≤ 原有水平
- [ ] 网络请求响应时间 ≤ 原有时间

### 3. 代码质量验收

- [ ] Kotlin 代码比例 ≥ 95%
- [ ] 无编译警告
- [ ] 无 Lint 错误
- [ ] 代码格式统一

### 4. 文档验收

- [ ] README 更新完成
- [ ] 迁移指南编写完成
- [ ] API 文档更新完成
- [ ] 版本说明编写完成

## 总结

本设计文档详细规划了 Swallow 核心库的现代化升级方案，涵盖了构建工具、依赖库、架构设计、代码重构等各个方面。通过渐进式迁移策略，可以确保升级过程平稳进行，最终交付一个高性能、易维护、面向未来的 MVVM 开发框架。
