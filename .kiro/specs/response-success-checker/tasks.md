# Implementation Tasks: Response Success Checker

## Tasks

- [x] 1. 在 NetworkConfigBuilder 中新增 successChecker 配置项
  - 新增 `successChecker: ((code: Int, status: String) -> Boolean)?` 字段，默认 `null`，`private set`
  - 新增链式方法 `successChecker(checker: (code: Int, status: String) -> Boolean): NetworkConfigBuilder`
  - 文件: `swallow/src/main/java/com/swallow/fly/http/di/NetworkConfigBuilder.kt`
  - 需求: Requirement 2

- [x] 2. 创建 ResponseConfig 全局单例
  - 新建 `ResponseConfig.kt`，`object` 单例
  - `successChecker` 属性使用 `@Volatile` 修饰，`internal set` 限制写入权限
  - 文件: `swallow/src/main/java/com/swallow/fly/http/result/ResponseConfig.kt`
  - 需求: Requirement 3

- [x] 3. 修改 BaseResponse.isSuccessful() 委托 ResponseConfig
  - 读入局部变量后判断，checker 不为 null 时委托执行，否则 fallback 到默认逻辑
  - 方法签名保持不变
  - 文件: `swallow/src/main/java/com/swallow/fly/http/result/BaseResponse.kt`
  - 需求: Requirement 1, 4, 6

- [x] 4. 在 NetworkModule 中注入 successChecker 到 ResponseConfig
  - 在 `provideResponseErrorListener` 中添加 `ResponseConfig.successChecker = config.network.successChecker`
  - 文件: `swallow/src/main/java/com/swallow/fly/http/di/NetworkModule.kt`
  - 需求: Requirement 3
