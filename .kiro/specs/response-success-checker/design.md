# Design Document: Response Success Checker

## Overview

本功能将 `BaseResponse.isSuccessful()` 的成功判断逻辑从硬编码改为可配置，通过引入 `ResponseConfig` 单例和 `NetworkConfigBuilder.successChecker` 配置项，实现业务层自定义成功码的能力，同时保持完全向后兼容。

---

## Architecture

### 组件关系图

```
业务层 (AppLifecycleImpl)
    │
    │  builder.successChecker { code, status -> code == 200 }
    ▼
NetworkConfigBuilder          ← 持有 successChecker lambda（可选）
    │
    │  SwallowConfig.network.successChecker
    ▼
NetworkModule (Hilt @Provides)  ← 框架初始化时注入
    │
    │  ResponseConfig.successChecker = config.network.successChecker
    ▼
ResponseConfig (object)         ← 全局单例，持有运行时 checker
    │
    │  checker?.invoke(code, status) ?: (code == 0 || status == "success")
    ▼
BaseResponse.isSuccessful()     ← 最终调用点
```

### 数据流

1. 业务层在 `AppLifecycleImpl.configureNetwork()` 中通过 `NetworkConfigBuilder.successChecker()` 注册 lambda
2. `SwallowConfig` 作为配置快照，将 `NetworkConfigBuilder` 实例传递给 Hilt
3. `NetworkModule.provideResponseErrorListener()` 在 Hilt 初始化时被调用，将 checker 写入 `ResponseConfig`
4. 运行时每次调用 `BaseResponse.isSuccessful()` 时，从 `ResponseConfig` 读取 checker 并执行

---

## Component Design

### 1. NetworkConfigBuilder

**文件**: `swallow/src/main/java/com/swallow/fly/http/di/NetworkConfigBuilder.kt`

新增字段和方法：

```kotlin
// 字段：默认 null，保证向后兼容
var successChecker: ((code: Int, status: String) -> Boolean)? = null
    private set

// 链式方法
fun successChecker(checker: (code: Int, status: String) -> Boolean): NetworkConfigBuilder {
    this.successChecker = checker
    return this
}
```

设计要点：
- `private set` 防止外部直接赋值，只能通过链式方法配置
- 默认 `null`，未配置时不影响现有行为
- 多次调用以最后一次为准（覆盖语义）

---

### 2. ResponseConfig

**文件**: `swallow/src/main/java/com/swallow/fly/http/result/ResponseConfig.kt`

```kotlin
object ResponseConfig {
    @Volatile
    var successChecker: ((code: Int, status: String) -> Boolean)? = null
        internal set
}
```

设计要点：
- `object` 单例，JVM 级别唯一实例
- `@Volatile` 保证多线程可见性（主线程写入，任意线程读取）
- `internal set` 限制写入权限仅在 `swallow` 模块内，防止业务层绕过配置链路直接修改
- 不使用 `AtomicReference`，因为框架初始化为单次写入，`@Volatile` 已足够

---

### 3. BaseResponse

**文件**: `swallow/src/main/java/com/swallow/fly/http/result/BaseResponse.kt`

```kotlin
fun isSuccessful(): Boolean {
    val checker = ResponseConfig.successChecker  // 局部变量，避免并发竞态
    return if (checker != null) {
        checker(code, status)
    } else {
        code == 0 || status == "success"
    }
}
```

设计要点：
- 先将 `ResponseConfig.successChecker` 读入局部变量 `checker`，避免 check-then-act 竞态
- 严格单路径执行：checker 不为 null 走自定义路径，否则走默认路径
- 方法签名不变，调用方零改动

---

### 4. NetworkModule

**文件**: `swallow/src/main/java/com/swallow/fly/http/di/NetworkModule.kt`

注入点选择 `provideResponseErrorListener`，原因：
- 该方法依赖 `SwallowConfig`，与 checker 注入所需依赖一致
- 避免新增独立的 `@Provides` 方法（减少 Hilt 图节点）
- `@Singleton` 保证只执行一次，与 `ResponseConfig` 单次写入语义匹配

```kotlin
@Singleton
@Provides
fun provideResponseErrorListener(config: SwallowConfig): ResponseErrorListener {
    // 注入 successChecker（null 或业务层配置的 lambda）
    ResponseConfig.successChecker = config.network.successChecker
    return config.network.responseErrorListener ?: object : ResponseErrorListener {
        override fun handleResponseError(t: Throwable?): Throwable = RuntimeException(t)
    }
}
```

---

## Backward Compatibility

| 场景 | 行为 |
|------|------|
| 未调用 `successChecker()` | `NetworkConfigBuilder.successChecker == null` → `ResponseConfig.successChecker == null` → 使用默认逻辑 |
| 调用了 `successChecker()` | 使用业务层提供的 lambda |
| 旧版本升级，不修改任何代码 | 与升级前行为完全一致 |

`BaseResponse` 构造函数签名、`isSuccessful()` 方法签名均未变更，二进制兼容。

---

## Usage Example

```kotlin
// AppLifecycleImpl.kt（业务层）
override fun configureNetwork(context: Context, builder: NetworkConfigBuilder) {
    builder
        .baseUrl("https://api.example.com/")
        // 适配后端 code=200 为成功的规范
        .successChecker { code, status -> code == 200 }
}

// 或同时判断 code 和 status
builder.successChecker { code, status ->
    code == 200 || status == "ok"
}
```

未配置时，框架行为与之前完全一致：

```kotlin
// 不调用 successChecker()，默认逻辑生效
override fun configureNetwork(context: Context, builder: NetworkConfigBuilder) {
    builder.baseUrl("https://api.example.com/")
    // isSuccessful() 仍然使用 code == 0 || status == "success"
}
```

---

## File Changes Summary

| 文件 | 变更类型 | 说明 |
|------|----------|------|
| `NetworkConfigBuilder.kt` | 修改 | 新增 `successChecker` 字段和链式方法 |
| `ResponseConfig.kt` | 新增 | 全局单例，持有运行时 checker |
| `BaseResponse.kt` | 修改 | `isSuccessful()` 委托 `ResponseConfig` |
| `NetworkModule.kt` | 修改 | `provideResponseErrorListener` 中注入 checker |
