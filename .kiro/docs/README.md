# Gyps 框架配置系统文档

## 📚 文档索引

### 快速开始
- **[快速开始指南](quick-start-config.md)** - 5 分钟上手配置系统

### 详细文档
- **[配置总结](configuration-summary.md)** - 配置系统架构和核心概念
- **[完整示例](module-config-example.md)** - 多模块配置的完整示例代码
- **[详细设计方案](configuration-integration-plan.md)** - 技术设计和实现细节

### 迁移指南
- **[迁移指南](migration-guide.md)** - 从旧版本升级到新配置系统

## 🎯 配置系统概述

Gyps 框架采用基于 **Hilt Multibinding** 的现代化配置系统：

- ✅ **类型安全**：编译时检查，避免运行时错误
- ✅ **零反射**：高性能，无反射开销
- ✅ **模块化**：支持多业务模块独立配置
- ✅ **优先级控制**：灵活的配置覆盖机制
- ✅ **依赖注入**：完整的 Hilt 支持

## 🚀 快速示例

### App 基础配置

```kotlin
@HiltAndroidApp
class MyApplication : BaseApplication() {
    
    override fun provideConfig(): FrameworkConfig {
        return frameworkConfig {
            network {
                baseUrl("https://api.example.com/")
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

### 业务模块配置（可选）

```kotlin
@Singleton
class PaymentConfigProvider @Inject constructor() : ModuleConfigProvider {
    override val priority: Int = 80
    override val moduleName: String = "Payment"
    
    override fun provideConfig(): FrameworkConfig {
        return frameworkConfig {
            network {
                addInterceptor(PaymentInterceptor())
            }
        }
    }
}

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

## 📖 推荐阅读顺序

1. **新用户**：
   - [快速开始指南](quick-start-config.md) → [配置总结](configuration-summary.md)

2. **需要多模块配置**：
   - [快速开始指南](quick-start-config.md) → [完整示例](module-config-example.md)

3. **从旧版本升级**：
   - [迁移指南](migration-guide.md) → [配置总结](configuration-summary.md)

4. **深入了解架构**：
   - [详细设计方案](configuration-integration-plan.md)

## 🔧 核心组件

### 1. FrameworkConfig (DSL)
类型安全的配置 DSL，支持 network、database、image、log 配置。

### 2. ModuleConfigProvider
业务模块配置接口，支持优先级控制和依赖注入。

### 3. FrameworkConfigHolder
配置管理中心，负责收集、排序、合并所有模块配置。

### 4. Hilt Integration
通过 Hilt Multibinding 自动收集和注入模块配置。

## 🎚️ 优先级系统

```
100:    App 主配置（最高优先级）
99-80:  核心业务模块
79-50:  普通业务模块
49-0:   基础模块
```

## 📦 配置项

### Network（网络）
- baseUrl - 基础 URL
- globalHttpHandler - 全局请求处理器
- addInterceptor - 添加拦截器
- okhttpConfiguration - OkHttp 配置
- retrofitConfiguration - Retrofit 配置
- gsonConfiguration - Gson 配置

### Database（数据库）
- database - Room 数据库实例

### Image（图片加载）
- imageLoaderInterceptor - 图片加载拦截器

### Log（日志）
- printHttpLogLevel - HTTP 日志级别
- formatPrinter - 日志格式化器

## 💡 最佳实践

1. **App 模块**：配置主要参数（baseUrl、database）
2. **业务模块**：添加模块特定的拦截器和配置
3. **优先级**：合理设置，避免冲突
4. **命名**：使用清晰的 moduleName
5. **文档**：为 ConfigProvider 添加注释

## 🔍 调试

启动应用时查看配置加载日志：

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

## 📞 技术支持

- 查看示例代码：`app/src/main/java/com/swallow/gyps/MyApplication.kt`
- 查看源码：`swallow/src/main/java/com/swallow/fly/base/lifecycle/config/`
- 参考文档：本目录下的各个 Markdown 文件

## 🎉 特性亮点

- **简洁**：DSL 语法，易读易写
- **灵活**：支持多模块扩展
- **强大**：优先级控制和配置合并
- **高效**：编译时生成，零反射
- **安全**：类型安全，编译时检查
- **友好**：完整的 IDE 支持

---

**Gyps Framework** - 现代化的 Android MVVM 开发框架
