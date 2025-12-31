# 多模块配置方案 - 基于 Hilt 的现代化实现

## 设计目标

1. ✅ 支持多模块独立配置（支付、用户、订单等业务模块）
2. ✅ 避免反射和 Manifest 解析
3. ✅ 类型安全、编译时检查
4. ✅ 优先级控制（app > 业务模块 > 基础模块）
5. ✅ 保持 DSL 的简洁性

## 核心思路

**使用 Hilt 的 Multibinding（多重绑定）机制**，让各模块通过 Hilt 注册配置，框架自动收集并按优先级合并。

## 架构设计

```
┌─────────────────────────────────────────────────────────┐
│  App Module (Priority: 100)                             │
│  - MyApplication.provideConfig() [DSL]                  │
│  - 主配置：baseUrl, database, 全局拦截器                │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│  Business Modules (Priority: 50-99)                     │
│  - PaymentConfigProvider [Hilt Module]                  │
│  - UserConfigProvider [Hilt Module]                     │
│  - 扩展配置：模块特定拦截器、Header                      │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│  FrameworkConfigHolder                                   │
│  - 收集所有 ConfigProvider                               │
│  - 按优先级排序                                          │
│  - 合并配置（高优先级覆盖低优先级）                      │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│  NetworkModule / DatabaseModule / ImageModule            │
│  - 应用合并后的配置                                      │
└─────────────────────────────────────────────────────────┘
```

## 实现步骤

### 1. 定义模块配置接口

```kotlin
// swallow/src/main/java/com/swallow/fly/base/lifecycle/config/ModuleConfigProvider.kt

/**
 * 模块配置提供者
 * 
 * 各业务模块通过 Hilt 实现此接口来注册配置
 */
interface ModuleConfigProvider {
    
    /**
     * 配置优先级
     * - app 模块：100
     * - 业务模块：50-99
     * - 基础模块：0-49
     */
    val priority: Int
    
    /**
     * 模块名称（用于日志）
     */
    val moduleName: String
    
    /**
     * 提供配置
     */
    fun provideConfig(): FrameworkConfig
}
```

### 2. 修改 FrameworkConfigHolder

```kotlin
// swallow/src/main/java/com/swallow/fly/base/lifecycle/config/FrameworkConfigHolder.kt

object FrameworkConfigHolder {
    
    @Volatile
    private var appConfig: FrameworkConfig? = null
    
    @Volatile
    private var moduleConfigs: Set<ModuleConfigProvider> = emptySet()
    
    /**
     * 初始化配置
     * 
     * @param application 应用实例
     * @param providers 通过 Hilt 注入的模块配置提供者
     */
    fun initialize(
        application: Application,
        providers: Set<ModuleConfigProvider> = emptySet()
    ) {
        if (appConfig == null) {
            synchronized(this) {
                if (appConfig == null) {
                    // 1. 获取 app 主配置
                    if (application is FrameworkConfigProvider) {
                        appConfig = application.provideConfig()
                    }
                    
                    // 2. 保存模块配置（按优先级排序）
                    moduleConfigs = providers
                    
                    // 3. 日志输出配置信息
                    logConfigInfo()
                }
            }
        }
    }
    
    /**
     * 应用网络配置（合并所有模块）
     */
    fun applyNetworkConfig(context: Context, builder: NetworkConfigBuilder) {
        // 1. 按优先级从低到高应用模块配置
        moduleConfigs
            .sortedBy { it.priority }
            .forEach { provider ->
                val config = provider.provideConfig()
                config.networkConfig(context, builder)
            }
        
        // 2. 最后应用 app 主配置（优先级最高）
        appConfig?.networkConfig?.invoke(context, builder)
    }
    
    /**
     * 应用数据库配置
     */
    fun applyDatabaseConfig(context: Context, builder: DatabaseConfigBuilder) {
        moduleConfigs
            .sortedBy { it.priority }
            .forEach { provider ->
                val config = provider.provideConfig()
                config.databaseConfig(context, builder)
            }
        
        appConfig?.databaseConfig?.invoke(context, builder)
    }
    
    /**
     * 应用图片加载配置
     */
    fun applyImageConfig(context: Context, builder: ImageConfigBuilder) {
        moduleConfigs
            .sortedBy { it.priority }
            .forEach { provider ->
                val config = provider.provideConfig()
                config.imageConfig(context, builder)
            }
        
        appConfig?.imageConfig?.invoke(context, builder)
    }
    
    /**
     * 应用日志配置
     */
    fun applyLogConfig(context: Context, builder: LogConfigBuilder) {
        moduleConfigs
            .sortedBy { it.priority }
            .forEach { provider ->
                val config = provider.provideConfig()
                config.logConfig(context, builder)
            }
        
        appConfig?.logConfig?.invoke(context, builder)
    }
    
    private fun logConfigInfo() {
        println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        println("📦 Framework Configuration Loaded")
        println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        println("✓ App Config: ${if (appConfig != null) "Loaded" else "None"}")
        println("✓ Module Configs: ${moduleConfigs.size}")
        moduleConfigs
            .sortedByDescending { it.priority }
            .forEach { provider ->
                println("  - [${provider.priority}] ${provider.moduleName}")
            }
        println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    }
}
```

### 3. 创建 Hilt 配置模块

```kotlin
// swallow/src/main/java/com/swallow/fly/base/lifecycle/config/ConfigProviderModule.kt

@Module
@InstallIn(SingletonComponent::class)
abstract class ConfigProviderModule {
    
    // 这是一个空的抽象类，用于安装 Multibinding
    // 各业务模块会在自己的 Module 中添加绑定
}

/**
 * 用于标记模块配置提供者的限定符
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ModuleConfig
```

### 4. 修改 BaseApplication

```kotlin
// swallow/src/main/java/com/swallow/fly/base/lifecycle/BaseApplication.kt

abstract class BaseApplication : MultiDexApplication(), FrameworkConfigProvider {
    
    // ✅ 通过 Hilt 注入模块配置
    @Inject
    @ModuleConfig
    lateinit var moduleConfigProviders: Set<@JvmSuppressWildcards ModuleConfigProvider>
    
    override fun onCreate() {
        super.onCreate()
        
        // ✅ 初始化配置（传入模块配置）
        FrameworkConfigHolder.initialize(this, moduleConfigProviders)
        
        onFrameworkInitialized()
        initLoggerConfig()
        AppManager.getInstance().init(this)
        initMMKV()
        appDelegate?.onCreate(this)
    }
    
    // ... 其他代码保持不变
}
```

### 5. 修改 NetworkModule

```kotlin
// swallow/src/main/java/com/swallow/fly/http/di/NetworkModule.kt

@Provides
fun provideBaseUrl(@ApplicationContext context: Context): HttpUrl {
    val builder = NetworkConfigBuilder()
    // ✅ 应用合并后的配置
    FrameworkConfigHolder.applyNetworkConfig(context, builder)
    return builder.baseUrl ?: DEFAULT_BASE_URL.toHttpUrlOrNull()!!
}

@Provides
fun provideGlobalHttpHandler(@ApplicationContext context: Context): GlobalHttpHandler? {
    val builder = NetworkConfigBuilder()
    FrameworkConfigHolder.applyNetworkConfig(context, builder)
    return builder.handler
}

// ... 其他 Provides 方法类似修改
```

## 使用示例

### App 模块（主配置）

```kotlin
// app/src/main/java/com/swallow/gyps/MyApplication.kt

@HiltAndroidApp
class MyApplication : BaseApplication() {
    
    override fun provideConfig(): FrameworkConfig {
        return frameworkConfig {
            network {
                baseUrl("https://api.example.com/")
                globalHttpHandler(HttpHandlerImpl())
            }
            
            database {
                val db = Room.databaseBuilder(it, AppDataBase::class.java, "app.db")
                    .build()
                database(db)
            }
        }
    }
}
```

### 业务模块（扩展配置）

```kotlin
// payment/src/main/java/com/example/payment/PaymentConfigProvider.kt

/**
 * 支付模块配置
 */
class PaymentConfigProvider @Inject constructor() : ModuleConfigProvider {
    
    override val priority: Int = 80
    override val moduleName: String = "Payment"
    
    override fun provideConfig(): FrameworkConfig {
        return frameworkConfig {
            network {
                // 添加支付模块专用拦截器
                addInterceptor(PaymentSignInterceptor())
                addInterceptor(PaymentLogInterceptor())
            }
        }
    }
}

// payment/src/main/java/com/example/payment/di/PaymentModule.kt

@Module
@InstallIn(SingletonComponent::class)
abstract class PaymentModule {
    
    @Binds
    @IntoSet
    @ModuleConfig
    abstract fun bindPaymentConfig(
        provider: PaymentConfigProvider
    ): ModuleConfigProvider
}
```

```kotlin
// user/src/main/java/com/example/user/UserConfigProvider.kt

/**
 * 用户模块配置
 */
class UserConfigProvider @Inject constructor() : ModuleConfigProvider {
    
    override val priority: Int = 70
    override val moduleName: String = "User"
    
    override fun provideConfig(): FrameworkConfig {
        return frameworkConfig {
            network {
                // 添加用户认证 Header
                addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer ${getToken()}")
                        .build()
                    chain.proceed(request)
                }
            }
        }
    }
    
    private fun getToken(): String {
        // 从本地获取 token
        return ""
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class UserModule {
    
    @Binds
    @IntoSet
    @ModuleConfig
    abstract fun bindUserConfig(
        provider: UserConfigProvider
    ): ModuleConfigProvider
}
```

## 配置合并规则

### 优先级示例

```
Priority 100: App (baseUrl, globalHandler)
Priority 80:  Payment (paymentInterceptor)
Priority 70:  User (authInterceptor)
Priority 50:  Analytics (analyticsInterceptor)

最终 OkHttpClient 拦截器顺序：
1. analyticsInterceptor (Priority 50)
2. authInterceptor (Priority 70)
3. paymentInterceptor (Priority 80)
4. globalHandler (Priority 100)
```

### 配置覆盖规则

- **baseUrl**: 高优先级覆盖低优先级
- **拦截器**: 累加（所有模块的拦截器都会添加）
- **database**: 高优先级覆盖低优先级
- **自定义配置**: 根据 Builder 实现决定

## 优势对比

| 特性 | ManifestParser 反射 | Hilt Multibinding |
|------|-------------------|-------------------|
| 类型安全 | ❌ 运行时错误 | ✅ 编译时检查 |
| 性能 | ❌ 反射开销 | ✅ 编译时生成 |
| 调试 | ❌ 难以追踪 | ✅ 清晰的依赖图 |
| IDE 支持 | ❌ 无法跳转 | ✅ 完整支持 |
| 模块化 | ⚠️ 需要 Manifest | ✅ 纯代码 |
| 优先级控制 | ⚠️ 手动排序 | ✅ 自动排序 |
| 可测试性 | ❌ 难以 Mock | ✅ 易于测试 |

## 迁移步骤

1. ✅ 创建 `ModuleConfigProvider` 接口
2. ✅ 修改 `FrameworkConfigHolder` 支持多配置合并
3. ✅ 修改 `BaseApplication` 注入模块配置
4. ✅ 修改各 Hilt Module 使用合并配置
5. ✅ 删除 `ManifestParser` 和 `ConfigModule` 接口
6. ✅ 删除 AndroidManifest 中的 meta-data
7. ✅ 各业务模块实现 `ModuleConfigProvider`

## 扩展能力

### 支持动态配置

```kotlin
class DynamicConfigProvider @Inject constructor(
    private val remoteConfig: RemoteConfig
) : ModuleConfigProvider {
    
    override val priority: Int = 90
    override val moduleName: String = "Dynamic"
    
    override fun provideConfig(): FrameworkConfig {
        return frameworkConfig {
            network {
                // 从远程配置读取 baseUrl
                baseUrl(remoteConfig.getBaseUrl())
            }
        }
    }
}
```

### 支持环境切换

```kotlin
class EnvironmentConfigProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : ModuleConfigProvider {
    
    override val priority: Int = 95
    override val moduleName: String = "Environment"
    
    override fun provideConfig(): FrameworkConfig {
        val env = getCurrentEnvironment()
        
        return frameworkConfig {
            network {
                baseUrl(when(env) {
                    Environment.DEV -> "https://dev-api.example.com/"
                    Environment.TEST -> "https://test-api.example.com/"
                    Environment.PROD -> "https://api.example.com/"
                })
            }
        }
    }
}
```
