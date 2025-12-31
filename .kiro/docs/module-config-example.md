# 多模块配置使用示例

## 完整示例：支付模块配置

### 1. 创建配置提供者

```kotlin
// payment/src/main/java/com/example/payment/config/PaymentConfigProvider.kt

package com.example.payment.config

import com.swallow.fly.base.lifecycle.config.ModuleConfigProvider
import com.swallow.fly.base.lifecycle.config.FrameworkConfig
import com.swallow.fly.base.lifecycle.config.frameworkConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 支付模块配置提供者
 * 
 * 职责：
 * 1. 添加支付签名拦截器
 * 2. 添加支付日志拦截器
 * 3. 配置支付相关的网络参数
 */
@Singleton
class PaymentConfigProvider @Inject constructor(
    private val paymentSigner: PaymentSigner
) : ModuleConfigProvider {
    
    override val priority: Int = 80
    override val moduleName: String = "Payment"
    
    override fun provideConfig(): FrameworkConfig {
        return frameworkConfig {
            network {
                // 添加支付签名拦截器
                addInterceptor(PaymentSignInterceptor(paymentSigner))
                
                // 添加支付日志拦截器
                addInterceptor(PaymentLogInterceptor())
            }
        }
    }
}

/**
 * 支付签名拦截器
 */
class PaymentSignInterceptor(
    private val signer: PaymentSigner
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        
        // 只对支付相关的请求添加签名
        if (!original.url.encodedPath.contains("/payment/")) {
            return chain.proceed(original)
        }
        
        val signature = signer.sign(original)
        
        val request = original.newBuilder()
            .addHeader("X-Payment-Sign", signature)
            .addHeader("X-Payment-Timestamp", System.currentTimeMillis().toString())
            .build()
        
        return chain.proceed(request)
    }
}

/**
 * 支付日志拦截器
 */
class PaymentLogInterceptor : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        if (request.url.encodedPath.contains("/payment/")) {
            println("💰 Payment Request: ${request.url}")
        }
        
        return chain.proceed(request)
    }
}
```

### 2. 注册到 Hilt

```kotlin
// payment/src/main/java/com/example/payment/di/PaymentModule.kt

package com.example.payment.di

import com.example.payment.config.PaymentConfigProvider
import com.swallow.fly.base.lifecycle.config.ModuleConfig
import com.swallow.fly.base.lifecycle.config.ModuleConfigProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 支付模块 Hilt 配置
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class PaymentModule {
    
    /**
     * 绑定支付配置提供者
     * 
     * @Binds: 告诉 Hilt 如何提供 ModuleConfigProvider 实例
     * @IntoSet: 将此实例添加到 Set<ModuleConfigProvider> 集合中
     * @ModuleConfig: 限定符，用于区分不同的 Set
     */
    @Binds
    @IntoSet
    @ModuleConfig
    @Singleton
    abstract fun bindPaymentConfig(
        provider: PaymentConfigProvider
    ): ModuleConfigProvider
}
```

## 更多示例

### 用户模块配置（认证 Token）

```kotlin
// user/src/main/java/com/example/user/config/UserConfigProvider.kt

@Singleton
class UserConfigProvider @Inject constructor(
    private val tokenManager: TokenManager
) : ModuleConfigProvider {
    
    override val priority: Int = 70
    override val moduleName: String = "User"
    
    override fun provideConfig(): FrameworkConfig {
        return frameworkConfig {
            network {
                // 添加认证拦截器
                addInterceptor { chain ->
                    val token = tokenManager.getToken()
                    
                    val request = if (token.isNotEmpty()) {
                        chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer $token")
                            .build()
                    } else {
                        chain.request()
                    }
                    
                    chain.proceed(request)
                }
            }
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class UserModule {
    
    @Binds
    @IntoSet
    @ModuleConfig
    @Singleton
    abstract fun bindUserConfig(
        provider: UserConfigProvider
    ): ModuleConfigProvider
}
```

### 统计模块配置

```kotlin
// analytics/src/main/java/com/example/analytics/config/AnalyticsConfigProvider.kt

@Singleton
class AnalyticsConfigProvider @Inject constructor(
    private val analytics: Analytics
) : ModuleConfigProvider {
    
    override val priority: Int = 50
    override val moduleName: String = "Analytics"
    
    override fun provideConfig(): FrameworkConfig {
        return frameworkConfig {
            network {
                // 添加网络请求统计
                addInterceptor { chain ->
                    val request = chain.request()
                    val startTime = System.currentTimeMillis()
                    
                    val response = chain.proceed(request)
                    
                    val duration = System.currentTimeMillis() - startTime
                    analytics.trackNetworkRequest(
                        url = request.url.toString(),
                        method = request.method,
                        duration = duration,
                        statusCode = response.code
                    )
                    
                    response
                }
            }
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {
    
    @Binds
    @IntoSet
    @ModuleConfig
    @Singleton
    abstract fun bindAnalyticsConfig(
        provider: AnalyticsConfigProvider
    ): ModuleConfigProvider
}
```

### 环境切换模块配置

```kotlin
// debug/src/main/java/com/example/debug/config/EnvironmentConfigProvider.kt

@Singleton
class EnvironmentConfigProvider @Inject constructor(
    private val envManager: EnvironmentManager
) : ModuleConfigProvider {
    
    override val priority: Int = 95  // 高优先级，但低于 app 主配置
    override val moduleName: String = "Environment"
    
    override fun provideConfig(): FrameworkConfig {
        val currentEnv = envManager.getCurrentEnvironment()
        
        return frameworkConfig {
            network {
                // 根据环境切换 baseUrl
                baseUrl(when(currentEnv) {
                    Environment.DEV -> "https://dev-api.example.com/"
                    Environment.TEST -> "https://test-api.example.com/"
                    Environment.STAGING -> "https://staging-api.example.com/"
                    Environment.PROD -> "https://api.example.com/"
                })
                
                // 添加环境标识 Header
                addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("X-Environment", currentEnv.name)
                        .build()
                    chain.proceed(request)
                }
            }
        }
    }
}
```

## App 主配置

```kotlin
// app/src/main/java/com/swallow/gyps/MyApplication.kt

@HiltAndroidApp
class MyApplication : BaseApplication() {
    
    override fun provideConfig(): FrameworkConfig {
        return frameworkConfig {
            
            // 网络配置（优先级最高，会覆盖所有模块配置的 baseUrl）
            network {
                baseUrl("http://ny.shuanghui.net:4081/")
                globalHttpHandler(HttpHandlerImpl())
            }
            
            // 数据库配置
            database {
                val db = Room.databaseBuilder(
                    it,
                    AppDataBase::class.java,
                    "gyps_test.db"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                
                database(db)
            }
            
            // 图片加载配置
            image {
                imageLoaderInterceptor(object : Interceptor {
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val request = chain.request().newBuilder()
                            .header("Content-Type", "application/json;charset=UTF-8")
                            .addHeader("platform", "Android")
                            .build()
                        return chain.proceed(request)
                    }
                })
            }
        }
    }
    
    override fun onFrameworkInitialized() {
        super.onFrameworkInitialized()
        
        // 框架初始化完成后的自定义逻辑
        initMscSdk()
        initARouterConfig()
    }
}
```

## 配置生效顺序

启动时控制台输出：

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📦 Framework Configuration Loaded
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
✓ App Config: Loaded
✓ Module Configs: 4
  - [Priority 95] Environment
  - [Priority 80] Payment
  - [Priority 70] User
  - [Priority 50] Analytics
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

实际配置应用顺序（从低到高）：

1. **Analytics** (50) - 添加统计拦截器
2. **User** (70) - 添加认证 Token
3. **Payment** (80) - 添加支付签名
4. **Environment** (95) - 设置环境 baseUrl
5. **App** (100) - 最终 baseUrl 和全局 Handler

最终 OkHttpClient 拦截器链：

```
Request → Analytics → User → Payment → Environment → App GlobalHandler → Server
```

## 配置覆盖规则

### baseUrl 覆盖

```kotlin
// 最终生效的是 App 配置的 baseUrl
Analytics (50): 无 baseUrl
User (70): 无 baseUrl
Payment (80): 无 baseUrl
Environment (95): "https://test-api.example.com/"  ← 被覆盖
App (100): "http://ny.shuanghui.net:4081/"  ← 最终生效 ✓
```

### 拦截器累加

```kotlin
// 所有模块的拦截器都会添加
Analytics (50): analyticsInterceptor
User (70): authInterceptor
Payment (80): paymentSignInterceptor, paymentLogInterceptor
App (100): globalHttpHandler

// 最终 OkHttpClient 包含所有拦截器
```

## 测试配置

```kotlin
// test/src/main/java/com/example/test/TestConfigProvider.kt

@Singleton
class TestConfigProvider @Inject constructor() : ModuleConfigProvider {
    
    override val priority: Int = 999  // 测试环境最高优先级
    override val moduleName: String = "Test"
    
    override fun provideConfig(): FrameworkConfig {
        return frameworkConfig {
            network {
                baseUrl("http://localhost:8080/")
                
                // 添加 Mock 拦截器
                addInterceptor(MockInterceptor())
            }
        }
    }
}
```

## 动态配置示例

```kotlin
// 支持运行时动态切换配置
@Singleton
class DynamicConfigProvider @Inject constructor(
    private val remoteConfig: RemoteConfigService
) : ModuleConfigProvider {
    
    override val priority: Int = 90
    override val moduleName: String = "Dynamic"
    
    override fun provideConfig(): FrameworkConfig {
        return frameworkConfig {
            network {
                // 从远程配置读取
                val config = remoteConfig.getNetworkConfig()
                
                baseUrl(config.baseUrl)
                
                // 动态添加拦截器
                config.interceptors.forEach { interceptorClass ->
                    addInterceptor(createInterceptor(interceptorClass))
                }
            }
        }
    }
}
```

## 注意事项

1. **优先级规则**：数字越大优先级越高
   - App 主配置：100
   - 业务模块：50-99
   - 基础模块：0-49

2. **配置覆盖**：
   - baseUrl、database 等单一配置：高优先级覆盖低优先级
   - 拦截器等列表配置：累加所有模块的配置

3. **依赖注入**：
   - ConfigProvider 可以注入其他依赖（如 TokenManager、Analytics）
   - 通过 Hilt 自动管理生命周期

4. **模块化**：
   - 每个业务模块独立配置
   - 不需要修改 app 模块代码
   - 支持动态添加/移除模块

5. **调试**：
   - 启动时会输出所有已加载的配置模块
   - 可以通过日志查看配置应用顺序
