# 配置系统迁移指南

## 概述

Gyps 框架已从基于反射的 `ManifestParser + ConfigModule` 配置方式升级为基于 **Hilt Multibinding** 的现代化配置系统。

## 变更内容

### 移除的内容

- ❌ `ManifestParser` 反射解析
- ❌ `ConfigModule` 接口（旧版）
- ❌ AndroidManifest.xml 中的 `<meta-data>` 配置注册

### 新增的内容

- ✅ `ModuleConfigProvider` 接口（新版）
- ✅ `@ModuleConfig` 限定符
- ✅ `FrameworkConfigHolder.applyXxxConfig()` 方法
- ✅ 基于 Hilt 的自动配置收集

## 迁移步骤

### 1. App 模块（已完成）

**之前（已删除）：**
```kotlin
// GlobalConfiguration.kt
class GlobalConfiguration : ConfigModule {
    override fun priority(): Int = 100
    
    override fun configureNetwork(context: Context, builder: NetworkConfigBuilder) {
        builder.baseUrl("http://example.com/")
    }
}
```

```xml
<!-- AndroidManifest.xml -->
<meta-data
    android:name="com.swallow.gyps.app.GlobalConfiguration"
    android:value="ConfigModule" />
```

**现在（使用 DSL）：**
```kotlin
// MyApplication.kt
@HiltAndroidApp
class MyApplication : BaseApplication() {
    
    override fun provideConfig(): FrameworkConfig {
        return frameworkConfig {
            network {
                baseUrl("http://example.com/")
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

### 2. 业务模块配置（新功能）

如果你有多个业务模块需要独立配置：

**步骤 1：创建 ConfigProvider**

```kotlin
// payment/src/main/java/com/example/payment/config/PaymentConfigProvider.kt

@Singleton
class PaymentConfigProvider @Inject constructor() : ModuleConfigProvider {
    
    override val priority: Int = 80
    override val moduleName: String = "Payment"
    
    override fun provideConfig(): FrameworkConfig {
        return frameworkConfig {
            network {
                addInterceptor(PaymentSignInterceptor())
            }
        }
    }
}
```

**步骤 2：注册到 Hilt**

```kotlin
// payment/src/main/java/com/example/payment/di/PaymentModule.kt

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

### 3. 清理旧代码

**需要删除的文件：**
- `app/src/main/java/com/swallow/gyps/app/GlobalConfiguration.kt` ✅ 已删除
- 其他模块中类似的 `ConfigModule` 实现

**需要修改的文件：**
- `AndroidManifest.xml` - 删除 `<meta-data>` 配置 ✅ 已完成

## 配置对比

### 旧方式 vs 新方式

| 特性 | 旧方式 (ConfigModule) | 新方式 (ModuleConfigProvider) |
|------|----------------------|-------------------------------|
| 注册方式 | AndroidManifest.xml | Hilt @Binds @IntoSet |
| 类型安全 | ❌ 运行时错误 | ✅ 编译时检查 |
| 性能 | ❌ 反射开销 | ✅ 编译时生成 |
| IDE 支持 | ❌ 无法跳转 | ✅ 完整支持 |
| 依赖注入 | ❌ 不支持 | ✅ 支持构造函数注入 |
| 调试 | ❌ 难以追踪 | ✅ 清晰的依赖图 |
| 优先级 | `priority(): Int` | `val priority: Int` |
| 模块名称 | 无 | `val moduleName: String` |

## API 变更

### FrameworkConfigHolder

**已废弃：**
```kotlin
@Deprecated
fun getConfig(): FrameworkConfig
```

**新方法：**
```kotlin
fun applyNetworkConfig(context: Context, builder: NetworkConfigBuilder)
fun applyDatabaseConfig(context: Context, builder: DatabaseConfigBuilder)
fun applyImageConfig(context: Context, builder: ImageConfigBuilder)
fun applyLogConfig(context: Context, builder: LogConfigBuilder)
```

### 使用示例

**之前：**
```kotlin
val config = FrameworkConfigHolder.getConfig()
config.networkConfig(context, builder)
```

**现在：**
```kotlin
FrameworkConfigHolder.applyNetworkConfig(context, builder)
```

## 优先级规则

### 配置优先级

```
100: App 主配置（MyApplication.provideConfig()）
99-50: 业务模块配置（Payment, User, Order 等）
49-0: 基础模块配置（Analytics, Log 等）
```

### 配置合并规则

1. **单一值配置**（如 baseUrl、database）：高优先级覆盖低优先级
2. **列表配置**（如拦截器）：累加所有模块的配置

### 示例

```kotlin
// 模块配置
Analytics (50): addInterceptor(analyticsInterceptor)
User (70): addInterceptor(authInterceptor), baseUrl("https://test.com/")
Payment (80): addInterceptor(paymentInterceptor)
App (100): baseUrl("https://api.com/"), globalHttpHandler(handler)

// 最终结果
baseUrl: "https://api.com/"  // App 配置覆盖 User 配置
拦截器链: [analyticsInterceptor, authInterceptor, paymentInterceptor, handler]
```

## 常见问题

### Q1: 为什么要移除 ManifestParser？

**A:** 
- 反射性能开销大
- 编译时无法检查错误
- IDE 无法提供代码跳转和自动补全
- 不支持依赖注入

### Q2: 如何支持多模块配置？

**A:** 使用 `ModuleConfigProvider` + Hilt Multibinding：

```kotlin
// 每个模块创建自己的 ConfigProvider
@Singleton
class PaymentConfigProvider @Inject constructor() : ModuleConfigProvider {
    override val priority: Int = 80
    override val moduleName: String = "Payment"
    override fun provideConfig(): FrameworkConfig { ... }
}

// 通过 Hilt 注册
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

### Q3: App 主配置和模块配置有什么区别？

**A:**
- **App 主配置**：通过 `MyApplication.provideConfig()` 提供，优先级最高（100）
- **模块配置**：通过 `ModuleConfigProvider` 提供，优先级可自定义（0-99）

### Q4: 如何查看当前加载了哪些配置？

**A:** 启动应用时查看控制台输出：

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📦 Framework Configuration Loaded
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
✓ App Config: Loaded
✓ Module Configs: 3
  - [Priority 80] Payment
  - [Priority 70] User
  - [Priority 50] Analytics
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

### Q5: 旧的 ConfigModule 接口还能用吗？

**A:** 
- 旧的 `ConfigModule` 接口已被标记为 `@Deprecated`
- `ManifestParser` 已被移除
- 建议尽快迁移到新的 `ModuleConfigProvider`

### Q6: 如何在测试中使用？

**A:** 创建测试专用的 ConfigProvider：

```kotlin
@Singleton
class TestConfigProvider @Inject constructor() : ModuleConfigProvider {
    override val priority: Int = 999  // 最高优先级
    override val moduleName: String = "Test"
    
    override fun provideConfig(): FrameworkConfig {
        return frameworkConfig {
            network {
                baseUrl("http://localhost:8080/")
                addInterceptor(MockInterceptor())
            }
        }
    }
}
```

## 完整示例

参见：
- [多模块配置示例](.kiro/docs/module-config-example.md)
- [配置整合方案](.kiro/docs/configuration-integration-plan.md)

## 技术支持

如有问题，请参考：
1. 示例代码：`app/src/main/java/com/swallow/gyps/MyApplication.kt`
2. 文档：`.kiro/docs/module-config-example.md`
3. 源码：`swallow/src/main/java/com/swallow/fly/base/lifecycle/config/`
