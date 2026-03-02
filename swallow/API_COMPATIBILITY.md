# Swallow Framework API 兼容性说明

## API 稳定性级别

Swallow Framework 使用注解标记 API 的稳定性级别，帮助开发者了解 API 的成熟度和变更风险。

### @Stable - 稳定 API

**定义**: 标记为 `@Stable` 的 API 已经过充分测试，在主版本内保证不会有破坏性变更。

**保证**:
- 在同一主版本内（如 2.x），API 签名不会改变
- 行为保持一致，不会有意外的变更
- 如需废弃，会先标记 `@Deprecated` 并在至少一个次版本后才移除

**示例**:
```kotlin
@Stable
abstract class BaseActivity<VM : BaseViewModel, VB : ViewBinding> : AppCompatActivity() {
    // 稳定的公开 API
}
```

**使用建议**: 可以放心在生产环境使用，不用担心升级问题。

---

### @ExperimentalSwallowApi - 实验性 API

**定义**: 标记为实验性的 API 正在开发或测试中，可能会在未来版本中变更或移除。

**风险**:
- API 签名可能会改变
- 功能可能不完整或有 bug
- 可能在未来版本中被移除

**使用方式**:
```kotlin
// 需要显式 opt-in
@OptIn(ExperimentalSwallowApi::class)
fun useExperimentalFeature() {
    DeepLinkNavigator.navigate(...)
}

// 或在文件级别 opt-in
@file:OptIn(ExperimentalSwallowApi::class)
```

**使用建议**: 
- 仅在非关键功能中使用
- 做好随时调整代码的准备
- 关注版本更新日志

---

### @InternalSwallowApi - 内部 API

**定义**: 标记为内部的 API 仅供框架内部使用，不应被外部调用。

**限制**:
- 使用时会产生编译错误
- 可能在任何版本中变更或移除，不会有通知
- 不提供任何兼容性保证

**示例**:
```kotlin
@InternalSwallowApi
object FrameworkConfigHolder {
    // 仅供框架内部使用
}
```

**使用建议**: 不要使用！如果确实需要，请提 issue 讨论是否应该公开。

---

### @Deprecated + @ScheduledForRemoval - 计划废弃

**定义**: 标记为废弃的 API 将在指定版本移除。

**时间线**:
1. 在版本 X.Y 标记为 `@Deprecated`
2. 在版本 X.Y+1 继续保留但发出警告
3. 在版本 X+1.0 完全移除

**示例**:
```kotlin
@Deprecated(
    message = "Use uiState and uiEvent instead",
    replaceWith = ReplaceWith("uiState/uiEvent")
)
@ScheduledForRemoval(version = "3.0.0", replaceWith = "uiState/uiEvent")
val pageStateEvent: LiveData<BaseStateEvent>
```

**使用建议**: 尽快迁移到新 API，避免在移除版本发布后出现编译错误。

---

## 版本兼容性矩阵

### Swallow 2.x 系列

| 版本 | 发布日期 | 支持状态 | 最低 Android SDK | 最低 Kotlin | 说明 |
|------|---------|---------|-----------------|------------|------|
| 2.0.0 | 2024-12 | ✅ 当前版本 | API 24 | 1.9.0 | 现代化架构升级 |
| 2.1.0 | 计划中 | 🔄 开发中 | API 24 | 2.0.0 | 性能优化 |
| 2.2.0 | 计划中 | 📋 规划中 | API 24 | 2.0.0 | Compose 支持 |

### Swallow 1.x 系列

| 版本 | 发布日期 | 支持状态 | 最低 Android SDK | 最低 Kotlin | 说明 |
|------|---------|---------|-----------------|------------|------|
| 1.0.3 | 2024-11 | ⚠️ 维护模式 | API 21 | 1.8.0 | 仅安全修复 |
| 1.0.2 | 2024-10 | ❌ 不再支持 | API 21 | 1.8.0 | - |
| 1.0.1 | 2024-09 | ❌ 不再支持 | API 21 | 1.8.0 | - |
| 1.0.0 | 2024-08 | ❌ 不再支持 | API 21 | 1.8.0 | 初始版本 |

---

## 依赖兼容性

### AndroidX 依赖

| 库 | 最低版本 | 推荐版本 | 说明 |
|----|---------|---------|------|
| androidx.core | 1.12.0 | 1.15.0 | 核心库 |
| androidx.lifecycle | 2.6.0 | 2.8.7 | 生命周期组件 |
| androidx.navigation | 2.7.0 | 2.8.5 | 导航组件 |
| androidx.room | 2.5.0 | 2.6.1 | 数据库 |
| androidx.paging | 3.2.0 | 3.3.5 | 分页 |

### Kotlin 依赖

| 库 | 最低版本 | 推荐版本 | 说明 |
|----|---------|---------|------|
| Kotlin | 1.9.0 | 2.0.21 | Kotlin 语言 |
| Coroutines | 1.7.0 | 1.9.0 | 协程 |

### 第三方依赖

| 库 | 最低版本 | 推荐版本 | 说明 |
|----|---------|---------|------|
| Hilt | 2.48 | 2.57.2 | 依赖注入 |
| Retrofit | 2.9.0 | 2.11.0 | 网络请求 |
| OkHttp | 4.10.0 | 4.12.0 | HTTP 客户端 |
| Glide | 4.15.0 | 5.0.5 | 图片加载 |

---

## 升级路径

### 从 1.x 升级到 2.0

**兼容性**: ⚠️ 需要代码迁移

**步骤**:
1. 阅读 [MIGRATION_GUIDE.md](MIGRATION_GUIDE.md)
2. 更新依赖版本
3. 修复编译错误（主要是包名变更）
4. 迁移废弃 API
5. 运行测试确保功能正常

**预计工作量**: 
- 小型项目（<50 个文件）: 2-4 小时
- 中型项目（50-200 个文件）: 1-2 天
- 大型项目（>200 个文件）: 3-5 天

### 在 2.x 内升级

**兼容性**: ✅ 完全兼容

**步骤**:
1. 更新 `build.gradle.kts` 中的版本号
2. 同步项目
3. 运行测试

**预计工作量**: 5-10 分钟

---

## 破坏性变更政策

### 何时会引入破坏性变更？

1. **主版本升级** (X.0.0)
   - 可以移除已废弃的 API
   - 可以改变核心架构
   - 可以修改包结构

2. **次版本升级** (X.Y.0)
   - 只能标记 API 为 `@Deprecated`
   - 不能移除现有 API
   - 可以添加新功能

3. **补丁版本** (X.Y.Z)
   - 只能修复 bug
   - 不能改变 API
   - 不能添加新功能

### 废弃流程

```
版本 2.0 → 标记 @Deprecated
    ↓
版本 2.1 → 继续保留，发出警告
    ↓
版本 2.2 → 继续保留，发出警告
    ↓
版本 3.0 → 完全移除
```

**最短保留期**: 至少一个次版本（通常 3-6 个月）

---

## 反馈和建议

如果你遇到以下情况，请提交 issue：

1. **API 不稳定**: 标记为 `@Stable` 的 API 出现意外变更
2. **缺少替代方案**: 废弃的 API 没有明确的替代方案
3. **文档不清晰**: API 文档不够详细或有歧义
4. **需要新功能**: 希望某个 `@InternalSwallowApi` 变为公开 API

**Issue 模板**: [GitHub Issues](https://github.com/yourusername/swallow/issues)

---

## 参考资料

- [Semantic Versioning](https://semver.org/)
- [Kotlin Evolution Principles](https://kotlinlang.org/docs/kotlin-evolution.html)
- [Android API Guidelines](https://developer.android.com/guide)
