# 快速开始：Gyps 配置系统

## 5 分钟上手

### 1. App 基础配置（必需）

在你的 Application 类中：

```kotlin
@HiltAndroidApp
class MyApplication : BaseApplication() {
    
    override fun provideConfig(): FrameworkConfig {
        return frameworkConfig {
            
            // 配置网络
            network {
                baseUrl("https://api.example.com/")
            }
            
            // 配置数据库
            database {
                val db = Room.databaseBuilder(it, AppDataBase::class.java, "app.db")
                    .build()
                database(db)
            }
        }
    }
}
```

✅ 完成！框架已配置好，可以开始开发了。

### 2. 添加业务模块配置（可选）

如果你需要在业务模块中添加特定配置：

**步骤 1：创建 ConfigProvider**

```kotlin
// payment/config/PaymentConfigProvider.kt

@Singleton
class PaymentConfigProvider @Inject constructor() : ModuleConfigProvider {
    
    override val priority: Int = 80
    override val moduleName: String = "Payment"
    
    override fun provideConfig(): FrameworkConfig {
        return frameworkConfig {
            network {
                // 添加支付签名拦截器
                addInterceptor(PaymentInterceptor())
            }
        }
    }
}
```

**步骤 2：注册到 Hilt**

```kotlin
// payment/di/PaymentModule.kt

@Module
@InstallIn(SingletonComponent::class)
abstract class PaymentModule {
    
    @Binds
    @IntoSet
    @ModuleConfig
    @Singleton
    abstract fun bindPaymentConfig(
        provider: PaymentConfigProvider
    ): ModuleConfigProvider
}
```

✅ 完成！支付模块配置已自动集成。

## 常用配置示例

### 网络配置

```kotlin
network {
    // 基础 URL
    baseUrl("https://api.example.com/")
    
    // 全局请求处理器
    globalHttpHandler(object : GlobalHttpHandler {
        override fun onHttpRequestBefore(chain: Interceptor.Chain, request: Request): Request {
            return request.newBuilder()
                .addHeader("Token", getToken())
                .build()
        }
    })
    
    // 添加拦截器
    addInterceptor(LoggingInterceptor())
    
    // 配置 OkHttp
    okhttpConfiguration { context, builder ->
        builder.connectTimeout(30, TimeUnit.SECONDS)
    }
    
    // 配置 Retrofit
    retrofitConfiguration { context, builder ->
        builder.addConverterFactory(MoshiConverterFactory.create())
    }
}
```

### 数据库配置

```kotlin
database {
    val db = Room.databaseBuilder(it, AppDataBase::class.java, "app.db")
        .fallbackToDestructiveMigration()
        .addMigrations(MIGRATION_1_2)
        .build()
    
    database(db)
}
```

### 图片加载配置

```kotlin
image {
    imageLoaderInterceptor(object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .addHeader("User-Agent", "MyApp/1.0")
                .build()
            return chain.proceed(request)
        }
    })
}
```

### 日志配置

```kotlin
log {
    // 设置 HTTP 日志级别
    printHttpLogLevel(RequestInterceptor.Level.ALL)
    
    // 自定义日志格式化器
    formatPrinter(CustomFormatPrinter())
}
```

## 优先级说明

```
100:  App 主配置（MyApplication）
80:   支付模块
70:   用户模块
50:   统计模块
```

- 数字越大优先级越高
- 高优先级配置会覆盖低优先级配置
- 拦截器会累加（不会覆盖）

## 查看配置加载情况

启动应用后查看 Logcat：

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📦 Framework Configuration Loaded
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
✓ App Config: Loaded
✓ Module Configs: 2
  - [Priority 80] Payment
  - [Priority 70] User
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

## 完整示例

参见：
- [详细示例](module-config-example.md)
- [迁移指南](migration-guide.md)
- [配置总结](configuration-summary.md)

## 常见问题

**Q: 必须配置所有项吗？**
A: 不需要，只配置你需要的项。框架会使用默认值。

**Q: 可以不添加业务模块配置吗？**
A: 可以，业务模块配置是可选的。只在 App 中配置也完全够用。

**Q: 如何添加自定义拦截器？**
A: 在 network 块中使用 `addInterceptor()`。

**Q: 配置什么时候生效？**
A: Application.onCreate() 时自动初始化并生效。

## 下一步

- 查看 [完整示例](module-config-example.md) 了解更多用法
- 阅读 [配置总结](configuration-summary.md) 了解架构设计
- 参考 [迁移指南](migration-guide.md) 从旧版本升级
