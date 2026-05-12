# UseCase 层优化分析报告

## 执行日期
2026-03-13

## 当前状态

### 现有实现

#### 1. UseCase 接口层次
```
UseCase<P, R>                    # 基础接口
├── NoParamsUseCase<R>           # 无参数 UseCase
├── FlowUseCase<P, R>            # Flow UseCase
└── BaseUseCase<P, R>            # 抽象基类
```

#### 2. 文件结构
```
swallow/src/main/java/com/swallow/fly/domain/usecase/
├── UseCase.kt          # 接口定义
└── BaseUseCase.kt      # 抽象基类
```

#### 3. 使用情况
- ✅ 接口定义完善
- ✅ 提供了基础实现
- ✅ 文档注释详细
- ❌ 项目中没有实际的 UseCase 实现类
- ❌ 缺少实用工具和扩展

---

## 需要优化的地方

### 🔴 高优先级优化

#### 1. 缺少 UseCase 组合器

**问题**: 无法方便地组合多个 UseCase

**场景**:
```kotlin
// 需要串行执行多个 UseCase
val user = getUserUseCase(userId).getOrThrow()
val profile = getUserProfileUseCase(user.id).getOrThrow()
val posts = getUserPostsUseCase(user.id).getOrThrow()
```

**优化方案**: 添加 UseCase 组合器


```kotlin
// 1. 串行组合器
infix fun <P, R1, R2> UseCase<P, R1>.then(
    next: UseCase<R1, R2>
): UseCase<P, R2> = object : UseCase<P, R2> {
    override suspend fun invoke(params: P): Result<R2> {
        return this@then(params).mapCatching { result ->
            next(result).getOrThrow()
        }
    }
}

// 2. 并行组合器
fun <P, R1, R2> parallel(
    useCase1: UseCase<P, R1>,
    useCase2: UseCase<P, R2>
): UseCase<P, Pair<R1, R2>> = object : UseCase<P, Pair<R1, R2>> {
    override suspend fun invoke(params: P): Result<Pair<R1, R2>> {
        return coroutineScope {
            val deferred1 = async { useCase1(params) }
            val deferred2 = async { useCase2(params) }
            
            val result1 = deferred1.await().getOrThrow()
            val result2 = deferred2.await().getOrThrow()
            
            Result.success(result1 to result2)
        }
    }
}

// 使用示例
val combinedUseCase = getUserUseCase then getUserProfileUseCase
val result = combinedUseCase(userId)
```

**收益**: 
- 减少样板代码
- 提高代码可读性
- 支持复杂业务逻辑组合

---

#### 2. 缺少 UseCase 扩展函数

**问题**: 缺少常用的 UseCase 操作扩展

**优化方案**: 添加实用扩展函数

```kotlin
// 1. 映射结果
fun <P, R, T> UseCase<P, R>.map(
    transform: (R) -> T
): UseCase<P, T> = object : UseCase<P, T> {
    override suspend fun invoke(params: P): Result<T> {
        return this@map(params).map(transform)
    }
}

// 2. 过滤结果
fun <P, R> UseCase<P, R>.filter(
    predicate: (R) -> Boolean,
    errorMessage: String = "Filter condition not met"
): UseCase<P, R> = object : UseCase<P, R> {
    override suspend fun invoke(params: P): Result<R> {
        return this@filter(params).mapCatching { result ->
            if (predicate(result)) result
            else throw IllegalStateException(errorMessage)
        }
    }
}

// 3. 重试机制
fun <P, R> UseCase<P, R>.retry(
    times: Int = 3,
    delayMillis: Long = 1000
): UseCase<P, R> = object : UseCase<P, R> {
    override suspend fun invoke(params: P): Result<R> {
        var lastException: Exception? = null
        repeat(times) { attempt ->
            val result = this@retry(params)
            if (result.isSuccess) return result
            lastException = result.exceptionOrNull() as? Exception
            if (attempt < times - 1) delay(delayMillis)
        }
        return Result.failure(lastException!!)
    }
}

// 4. 缓存结果
fun <P, R> UseCase<P, R>.cached(
    cache: MutableMap<P, R> = mutableMapOf()
): UseCase<P, R> = object : UseCase<P, R> {
    override suspend fun invoke(params: P): Result<R> {
        cache[params]?.let { return Result.success(it) }
        return this@cached(params).onSuccess { cache[params] = it }
    }
}

// 使用示例
val cachedUseCase = getUserUseCase
    .retry(times = 3)
    .map { user -> user.name }
    .cached()
```

**收益**:
- 提供常用功能
- 减少重复代码
- 提高开发效率

---

#### 3. 缺少 FlowUseCase 基类

**问题**: FlowUseCase 只有接口，没有基类实现

**优化方案**: 添加 BaseFlowUseCase

```kotlin
abstract class BaseFlowUseCase<in P, out R> : FlowUseCase<P, R> {
    
    override fun invoke(params: P): Flow<Result<R>> {
        return flow {
            try {
                execute(params).collect { result ->
                    emit(Result.success(result))
                }
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        }
    }
    
    protected abstract fun execute(params: P): Flow<R>
}

// 使用示例
class ObserveUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) : BaseFlowUseCase<String, UserProfile>() {
    override fun execute(params: String): Flow<UserProfile> {
        return userRepository.observeUserProfile(params)
    }
}
```

**收益**:
- 统一错误处理
- 减少样板代码
- 与 BaseUseCase 保持一致

---

#### 4. 缺少 NoParamsUseCase 基类

**问题**: NoParamsUseCase 只有接口，没有基类实现

**优化方案**: 添加 BaseNoParamsUseCase

```kotlin
abstract class BaseNoParamsUseCase<out R> : NoParamsUseCase<R> {
    
    override suspend fun invoke(): Result<R> {
        return try {
            Timber.tag(TAG).d("Executing ${this::class.simpleName}")
            val result = execute()
            Timber.tag(TAG).d("Successfully executed ${this::class.simpleName}")
            Result.success(result)
        } catch (e: Exception) {
            Timber.tag(TAG).e("Failed to execute ${this::class.simpleName}: ${e.message}")
            Result.failure(e)
        }
    }
    
    protected abstract suspend fun execute(): R
    
    companion object {
        private const val TAG = "BaseNoParamsUseCase"
    }
}

// 使用示例
class GetCurrentUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) : BaseNoParamsUseCase<User>() {
    override suspend fun execute(): User {
        return userRepository.getCurrentUser().getOrThrow()
    }
}
```

**收益**:
- 统一错误处理
- 统一日志记录
- 减少样板代码

---

### 🟡 中优先级优化

#### 5. 缺少 UseCase 测试工具

**问题**: 没有提供 UseCase 测试基类和工具

**优化方案**: 添加测试工具类

```kotlin
// 1. UseCase 测试基类
abstract class BaseUseCaseTest<P, R> {
    protected lateinit var useCase: UseCase<P, R>
    protected val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = createUseCase()
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    protected abstract fun createUseCase(): UseCase<P, R>
    
    protected fun runTest(block: suspend TestScope.() -> Unit) {
        kotlinx.coroutines.test.runTest(testDispatcher) {
            block()
        }
    }
}

// 2. Mock UseCase
class MockUseCase<P, R>(
    private val result: Result<R>
) : UseCase<P, R> {
    var invocationCount = 0
        private set
    var lastParams: P? = null
        private set
    
    override suspend fun invoke(params: P): Result<R> {
        invocationCount++
        lastParams = params
        return result
    }
}

// 使用示例
class GetUserUseCaseTest : BaseUseCaseTest<String, User>() {
    private lateinit var mockRepository: UserRepository
    
    override fun createUseCase(): UseCase<String, User> {
        mockRepository = mockk()
        return GetUserUseCase(mockRepository)
    }
    
    @Test
    fun `should return user when repository succeeds`() = runTest {
        // Given
        val userId = "123"
        val expectedUser = User(userId, "John")
        coEvery { mockRepository.getUser(userId) } returns Result.success(expectedUser)
        
        // When
        val result = useCase(userId)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedUser, result.getOrNull())
    }
}
```

**收益**:
- 简化测试编写
- 提高测试覆盖率
- 统一测试风格

---

#### 6. 缺少 UseCase 调度器配置

**问题**: UseCase 执行在哪个线程不明确

**优化方案**: 添加调度器配置

```kotlin
// 1. UseCase 调度器接口
interface UseCaseDispatcher {
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
}

// 2. 默认实现
class DefaultUseCaseDispatcher : UseCaseDispatcher {
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val default: CoroutineDispatcher = Dispatchers.Default
}

// 3. 支持调度器的 UseCase
abstract class DispatchedUseCase<in P, out R>(
    private val dispatcher: UseCaseDispatcher
) : UseCase<P, R> {
    
    override suspend fun invoke(params: P): Result<R> {
        return withContext(dispatcher.io) {
            try {
                Result.success(execute(params))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    protected abstract suspend fun execute(params: P): R
}

// 使用示例
class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    dispatcher: UseCaseDispatcher
) : DispatchedUseCase<String, User>(dispatcher) {
    override suspend fun execute(params: String): User {
        return userRepository.getUser(params).getOrThrow()
    }
}
```

**收益**:
- 明确线程调度
- 方便测试（可注入测试调度器）
- 提高性能

---

#### 7. 缺少 UseCase 执行监控

**问题**: 无法监控 UseCase 执行情况

**优化方案**: 添加执行监控

```kotlin
// 1. UseCase 执行监听器
interface UseCaseExecutionListener {
    fun onStart(useCaseName: String, params: Any?)
    fun onSuccess(useCaseName: String, result: Any?, durationMs: Long)
    fun onFailure(useCaseName: String, error: Throwable, durationMs: Long)
}

// 2. 支持监控的 UseCase
abstract class MonitoredUseCase<in P, out R>(
    private val listener: UseCaseExecutionListener? = null
) : UseCase<P, R> {
    
    override suspend fun invoke(params: P): Result<R> {
        val useCaseName = this::class.simpleName ?: "Unknown"
        val startTime = System.currentTimeMillis()
        
        listener?.onStart(useCaseName, params)
        
        return try {
            val result = execute(params)
            val duration = System.currentTimeMillis() - startTime
            listener?.onSuccess(useCaseName, result, duration)
            Result.success(result)
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            listener?.onFailure(useCaseName, e, duration)
            Result.failure(e)
        }
    }
    
    protected abstract suspend fun execute(params: P): R
}

// 3. 默认监听器实现
class LoggingUseCaseListener : UseCaseExecutionListener {
    override fun onStart(useCaseName: String, params: Any?) {
        Timber.d("[$useCaseName] Started with params: $params")
    }
    
    override fun onSuccess(useCaseName: String, result: Any?, durationMs: Long) {
        Timber.d("[$useCaseName] Succeeded in ${durationMs}ms")
    }
    
    override fun onFailure(useCaseName: String, error: Throwable, durationMs: Long) {
        Timber.e("[$useCaseName] Failed in ${durationMs}ms: ${error.message}")
    }
}
```

**收益**:
- 监控执行性能
- 追踪错误
- 便于调试

---

### 🟢 低优先级优化

#### 8. 缺少 UseCase 示例实现

**问题**: 项目中没有实际的 UseCase 实现类作为参考

**优化方案**: 添加示例 UseCase

```kotlin
// 示例 1: 简单 UseCase
class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) : BaseUseCase<String, User>() {
    override suspend fun execute(params: String): User {
        return userRepository.getUser(params).getOrThrow()
    }
}

// 示例 2: 无参数 UseCase
class GetCurrentUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) : BaseNoParamsUseCase<User>() {
    override suspend fun execute(): User {
        return userRepository.getCurrentUser().getOrThrow()
    }
}

// 示例 3: Flow UseCase
class ObserveUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) : BaseFlowUseCase<String, User>() {
    override fun execute(params: String): Flow<User> {
        return userRepository.observeUser(params)
    }
}

// 示例 4: 复杂 UseCase（组合多个操作）
class GetUserWithProfileUseCase @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) : BaseUseCase<String, UserWithProfile>() {
    override suspend fun execute(params: String): UserWithProfile {
        val user = getUserUseCase(params).getOrThrow()
        val profile = getUserProfileUseCase(user.id).getOrThrow()
        return UserWithProfile(user, profile)
    }
}
```

**收益**:
- 提供参考实现
- 降低学习成本
- 统一编码风格

---

#### 9. 缺少 UseCase 文档和最佳实践

**问题**: 缺少详细的使用文档和最佳实践指南

**优化方案**: 添加文档

```markdown
# UseCase 使用指南

## 什么是 UseCase?

UseCase 封装单一业务逻辑的可执行单元，遵循 Clean Architecture 原则。

## 何时使用 UseCase?

- 业务逻辑复杂，需要多个步骤
- 需要在多个地方复用相同的业务逻辑
- 需要独立测试业务逻辑
- 需要组合多个操作

## 何时不使用 UseCase?

- 简单的 CRUD 操作（直接在 Repository 中完成）
- 只在一个地方使用的逻辑
- UI 相关的逻辑（应该在 ViewModel 中）

## 最佳实践

1. 一个 UseCase 只做一件事
2. UseCase 不应该依赖 Android 框架
3. UseCase 应该是可测试的
4. 使用依赖注入
5. 返回 Result 类型处理错误
```

**收益**:
- 降低学习成本
- 统一编码规范
- 提高代码质量

---

## 优化优先级总结

### 🔴 立即实施（本周，约 6 小时）

1. **UseCase 组合器**（1.5 小时）
   - 串行组合器（then）
   - 并行组合器（parallel）

2. **UseCase 扩展函数**（2 小时）
   - map、filter
   - retry、cached
   - 其他实用扩展

3. **BaseFlowUseCase**（1 小时）
   - 实现基类
   - 统一错误处理

4. **BaseNoParamsUseCase**（0.5 小时）
   - 实现基类
   - 统一错误处理

5. **示例 UseCase**（1 小时）
   - 创建 4-5 个示例
   - 覆盖常见场景

### 🟡 近期实施（下周，约 4 小时）

6. **UseCase 测试工具**（2 小时）
   - BaseUseCaseTest
   - MockUseCase

7. **UseCase 调度器配置**（1 小时）
   - UseCaseDispatcher 接口
   - DispatchedUseCase 实现

8. **UseCase 执行监控**（1 小时）
   - UseCaseExecutionListener
   - MonitoredUseCase

### 🟢 可选实施（按需）

9. **UseCase 文档**（2 小时）
   - 使用指南
   - 最佳实践
   - API 文档

---

## 预期收益

### 代码质量
- 减少样板代码 40-50%
- 提高代码复用性
- 统一业务逻辑封装

### 开发效率
- 加快新功能开发
- 减少重复代码
- 提供开箱即用的工具

### 可测试性
- 简化单元测试
- 提高测试覆盖率
- 统一测试风格

### 可维护性
- 清晰的业务逻辑分层
- 易于理解和修改
- 便于团队协作

---

## 总结

当前 UseCase 层的基础架构已经很好，主要问题是：

1. ❌ 缺少实用工具（组合器、扩展函数）
2. ❌ 缺少完整的基类实现（FlowUseCase、NoParamsUseCase）
3. ❌ 缺少测试工具
4. ❌ 缺少示例实现
5. ❌ 项目中没有实际使用

建议优先实施高优先级优化，这些改进投入少、收益高，能显著提升开发效率。
