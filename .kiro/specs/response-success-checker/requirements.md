# Requirements Document

## Introduction

Swallow 框架的 `BaseResponse.isSuccessful()` 原先硬编码了成功判断逻辑（`code == 0 || status == "success"`），无法适配不同后端接口规范。本功能通过在 `NetworkConfigBuilder` 中引入可选的 `successChecker` lambda，将成功判断逻辑外置为可配置项，同时保持对已有接入项目的完全向后兼容。

## Glossary

- **BaseResponse**: Swallow 框架中统一封装 HTTP 响应体的泛型类，位于 `com.swallow.fly.http.result`
- **ResponseConfig**: 持有全局 `successChecker` 的单例对象，由框架初始化时注入，位于 `com.swallow.fly.http.result`
- **NetworkConfigBuilder**: 网络层配置构建器，业务层通过链式调用配置网络参数，位于 `com.swallow.fly.http.di`
- **NetworkModule**: Hilt 网络依赖注入模块，负责在框架初始化时将配置注入到各组件，位于 `com.swallow.fly.http.di`
- **SuccessChecker**: 类型为 `(code: Int, status: String) -> Boolean` 的函数类型，由业务层提供，用于判断响应是否业务成功
- **DefaultSuccessLogic**: 框架内置的默认成功判断逻辑：`code == 0 || status == "success"`
- **SwallowConfig**: 框架全局配置快照，聚合所有子模块配置，位于 `com.swallow.fly.base.lifecycle.config`

---

## Requirements

### Requirement 1: 默认成功判断逻辑保持不变

**User Story:** 作为已接入 Swallow 框架的 Android 开发者，我希望在不修改任何现有代码的情况下，框架升级后的行为与升级前完全一致，以确保存量项目零改动迁移。

#### Acceptance Criteria

1. THE `BaseResponse` SHALL 在未配置 `SuccessChecker` 时，使用 `DefaultSuccessLogic`（`code == 0 || status == "success"`）作为 `isSuccessful()` 的返回值依据。
2. WHEN `ResponseConfig.successChecker` 为 `null`，THE `BaseResponse.isSuccessful()` SHALL 返回 `code == 0 || status == "success"` 的计算结果。
3. THE `NetworkConfigBuilder` SHALL 在未调用 `successChecker()` 方法时，将 `successChecker` 字段默认值保持为 `null`。

---

### Requirement 2: 业务层可通过 NetworkConfigBuilder 注册自定义 SuccessChecker

**User Story:** 作为 Android 开发者，我希望通过 `NetworkConfigBuilder` 的链式 API 注册自定义成功判断逻辑，以适配后端返回 `code == 200` 或其他非标准成功码的接口规范。

#### Acceptance Criteria

1. THE `NetworkConfigBuilder` SHALL 提供 `successChecker(checker: (code: Int, status: String) -> Boolean): NetworkConfigBuilder` 方法，支持链式调用。
2. WHEN 业务层调用 `builder.successChecker { code, status -> code == 200 }` 后，THE `NetworkConfigBuilder.successChecker` 字段 SHALL 持有该 lambda 引用。
3. THE `successChecker` 方法 SHALL 返回 `NetworkConfigBuilder` 自身实例，以保持链式调用语义。
4. WHERE 业务层需要同时判断 `code` 和 `status`，THE `SuccessChecker` lambda SHALL 接收 `code: Int` 和 `status: String` 两个参数。

---

### Requirement 3: 框架初始化时将 SuccessChecker 注入 ResponseConfig

**User Story:** 作为框架维护者，我希望在 Hilt 依赖注入完成时，将业务层配置的 `SuccessChecker` 自动注入到全局单例 `ResponseConfig`，以确保 `BaseResponse` 在任意调用时机均能获取到正确的判断逻辑。

#### Acceptance Criteria

1. WHEN `NetworkModule.provideResponseErrorListener` 被 Hilt 调用，THE `NetworkModule` SHALL 将 `SwallowConfig.network.successChecker` 赋值给 `ResponseConfig.successChecker`。
2. WHEN `SwallowConfig.network.successChecker` 为 `null`，THE `NetworkModule` SHALL 将 `ResponseConfig.successChecker` 设置为 `null`，以触发 `BaseResponse` 的 `DefaultSuccessLogic`。
3. THE `ResponseConfig.successChecker` 属性 SHALL 使用 `@Volatile` 修饰，以保证多线程环境下的可见性。
4. THE `ResponseConfig.successChecker` 的 setter SHALL 限定为 `internal`，防止框架外部直接修改。

---

### Requirement 4: BaseResponse.isSuccessful() 优先委托 SuccessChecker

**User Story:** 作为 Android 开发者，我希望 `BaseResponse.isSuccessful()` 在已配置 `SuccessChecker` 时优先使用自定义逻辑，在未配置时自动回退到默认逻辑，以实现透明的行为切换。

#### Acceptance Criteria

1. WHEN `ResponseConfig.successChecker` 不为 `null`，THE `BaseResponse.isSuccessful()` SHALL 调用 `ResponseConfig.successChecker.invoke(code, status)` 并返回其结果。
2. IF `ResponseConfig.successChecker` 为 `null`，THEN THE `BaseResponse.isSuccessful()` SHALL 返回 `DefaultSuccessLogic` 的计算结果。
3. THE `BaseResponse.isSuccessful()` SHALL 在单次调用中仅执行一条判断路径（checker 路径或默认路径），不得同时执行两者。
4. FOR ALL 有效的 `(code: Int, status: String)` 输入组合，WHEN 注册的 `SuccessChecker` 返回 `true`，THE `BaseResponse.isSuccessful()` SHALL 返回 `true`；WHEN 注册的 `SuccessChecker` 返回 `false`，THE `BaseResponse.isSuccessful()` SHALL 返回 `false`。

---

### Requirement 5: SuccessChecker 配置的幂等性

**User Story:** 作为框架维护者，我希望多次调用 `successChecker()` 方法时，后一次调用覆盖前一次，以确保配置行为可预期且无副作用。

#### Acceptance Criteria

1. WHEN `NetworkConfigBuilder.successChecker()` 被调用多次，THE `NetworkConfigBuilder` SHALL 以最后一次调用传入的 lambda 作为最终有效值。
2. THE `ResponseConfig.successChecker` SHALL 在框架生命周期内保持单一有效值，不得累积多个 checker。

---

### Requirement 6: 向后兼容性保证

**User Story:** 作为已接入 Swallow 框架的 Android 开发者，我希望本次改动不引入任何破坏性变更，现有代码无需修改即可正常编译和运行。

#### Acceptance Criteria

1. THE `BaseResponse` 类的构造函数签名 SHALL 保持不变，不得新增或删除构造参数。
2. THE `BaseResponse.isSuccessful()` 方法签名 SHALL 保持不变（无参数，返回 `Boolean`）。
3. THE `NetworkConfigBuilder` 中新增的 `successChecker` 字段 SHALL 默认值为 `null`，不影响未调用该方法的现有配置代码。
4. IF 业务层未在 `NetworkConfigBuilder` 中配置 `successChecker`，THEN THE 框架 SHALL 表现出与改动前完全相同的行为。
5. THE 废弃文件 SHALL 保留原有实现，仅添加 `@Deprecated` 注解，不得删除或修改其功能逻辑。
