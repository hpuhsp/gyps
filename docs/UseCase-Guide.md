# UseCase 使用指南

## 概述

UseCase（用例）是 Clean Architecture 中的核心概念，用于封装单一业务逻辑的可执行单元。通过引入 UseCase 层，我们可以：

- **解耦业务逻辑**：将业务逻辑从 ViewModel 和 Repository 中分离
- **提高可测试性**：UseCase 可以独立测试，无需依赖 Android 框架
- **提高可复用性**：同一个 UseCase 可以在多个 ViewModel 中使用
- **清晰的职责划分**：每个 UseCase 只负责一个具体的业务操作

## 架构层次

```
Presentation Layer (UI)
    ↓
Domain Layer (UseCase)  ← 我们在这里
    ↓
Data Layer (Repository)
    ↓
Framework Layer (Network/DB/Cache)
```

## UseCase 接口

框架提供了三种 UseCase 接口：

### 1. UseCase<P, R>

标准的 UseCase 接口，接受参数并返回 Result。

```kotlin
interface UseCase<in P, out R> {
    suspend operator fun invoke(params: P): Result<R>
}
```

**使用场景**：大多数业务逻辑

### 2. NoParamsUseCase<R>

无参数的 UseCase 接口。

```kotlin
interface NoParamsUseCase<out R> : UseCase<Unit, R> {
    suspend operator fun invoke(): Result<R> = invoke(Unit)
}
```

**使用场景**：不需要输入参数的业务逻辑，如获取当前用户信息

### 3. FlowUseCase<P, R>

返回 Flow 流的 UseCase 接口。

```kotlin
interface FlowUseCase<in P, out R> {
    operator fun invoke(params: P): Flow<Result<R>>
}
```

**使用场景**：需要持续观察数据变化的业务逻辑，如实时监听用户状态

## 创建 UseCase

### 方式 1：继承 BaseUseCase（推荐）

`BaseUseCase` 提供了自动的错误处理和日志记录功能。

```kotlin
class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) : BaseUseCase<String, UserProfile>() {
    
    override suspend fun execute(params: String): UserProfile {
        // 只需要实现业务逻辑，异常会自动处理
        return userRepository.getUserProfile(params).getOrThrow()
    }
}
```

**优点**：
- 自动异常处理和转换
- 自动日志记录
- 代码更简洁

### 方式 2：直接实现 UseCase 接口

如果需要自定义错误处理逻辑，可以直接实现接口。

```kotlin
class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) : UseCase<String, UserProfile> {
    
    override suspend fun invoke(params: String): Result<UserProfile> {
        return try {
            val profile = userRepository.getUserProfile(params).getOrThrow()
            Result.success(profile)
        } catch (e: Exception) {
            // 自定义错误处理
            Result.failure(e.toAppException())
        }
    }
}
```

## 在 ViewModel 中使用 UseCase

### 基本用法

```kotlin
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfile: GetUserProfileUseCase
) : BaseViewModel() {
    
    private val _profileState = MutableStateFlow<DataState<UserProfile>>(DataState.Idle)
    val profileState: StateFlow<DataState<UserProfile>> = _profileState.asStateFlow()
    
    fun loadProfile(userId: String) {
        viewModelScope.launch {
            _profileState.value = DataState.Loading()
            
            getUserProfile(userId)
                .onSuccess { profile ->
                    _profileState.value = DataState.Success(profile)
                }
                .onFailure { error ->
                    _profileState.value = DataState.Error(error.toAppException())
                }
        }
    }
}
```

### 使用 Flow UseCase

```kotlin
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val observeUserProfile: ObserveUserProfileUseCase
) : BaseViewModel() {
    
    val profileState: StateFlow<DataState<UserProfile>> = 
        observeUserProfile(userId)
            .map { result ->
                result.fold(
                    onSuccess = { DataState.Success(it) },
                    onFailure = { DataState.Error(it.toAppException()) }
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = DataState.Loading()
            )
}
```

## 最佳实践

### 1. 单一职责原则

每个 UseCase 只负责一个具体的业务操作。

✅ **好的示例**：
```kotlin
class GetUserProfileUseCase  // 获取用户资料
class UpdateUserProfileUseCase  // 更新用户资料
class DeleteUserAccountUseCase  // 删除用户账号
```

❌ **不好的示例**：
```kotlin
class UserManagementUseCase  // 太宽泛，职责不清晰
```

### 2. 命名规范

UseCase 的命名应该清晰地表达其功能：

- 使用动词开头：`Get`, `Update`, `Delete`, `Create`, `Fetch`, `Observe`
- 使用名词描述对象：`UserProfile`, `OrderList`, `PaymentInfo`
- 以 `UseCase` 结尾

**示例**：
- `GetUserProfileUseCase`
- `UpdateOrderStatusUseCase`
- `ObserveNetworkStateUseCase`

### 3. 参数封装

当 UseCase 需要多个参数时，使用数据类封装。

✅ **好的示例**：
```kotlin
data class UpdateProfileParams(
    val userId: String,
    val name: String,
    val email: String,
    val avatar: String?
)

class UpdateUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) : BaseUseCase<UpdateProfileParams, UserProfile>() {
    override suspend fun execute(params: UpdateProfileParams): UserProfile {
        return userRepository.updateProfile(
            userId = params.userId,
            name = params.name,
            email = params.email,
            avatar = params.avatar
        ).getOrThrow()
    }
}
```

❌ **不好的示例**：
```kotlin
// 参数太多，难以维护
class UpdateUserProfileUseCase : UseCase<Tuple4<String, String, String, String?>, UserProfile>
```

### 4. 依赖注入

始终使用 Hilt 进行依赖注入。

```kotlin
class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) : BaseUseCase<String, UserProfile>() {
    // ...
}
```

### 5. 测试 UseCase

UseCase 应该易于测试，无需依赖 Android 框架。

```kotlin
class GetUserProfileUseCaseTest {
    
    private lateinit var fakeRepository: FakeUserRepository
    private lateinit var useCase: GetUserProfileUseCase
    
    @Before
    fun setup() {
        fakeRepository = FakeUserRepository()
        useCase = GetUserProfileUseCase(fakeRepository)
    }
    
    @Test
    fun `should return user profile when repository succeeds`() = runTest {
        // Given
        val userId = "123"
        val expectedProfile = UserProfile(id = userId, name = "Test User")
        fakeRepository.setUserProfile(userId, expectedProfile)
        
        // When
        val result = useCase(userId)
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedProfile, result.getOrNull())
    }
    
    @Test
    fun `should return failure when repository fails`() = runTest {
        // Given
        val userId = "123"
        fakeRepository.shouldFail = true
        
        // When
        val result = useCase(userId)
        
        // Then
        assertTrue(result.isFailure)
    }
}
```

## 迁移指南

### 从直接调用 Repository 迁移到 UseCase

**旧方式（直接调用 Repository）**：
```kotlin
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepository
) : BaseViewModel() {
    
    fun loadProfile(userId: String) {
        viewModelScope.launch {
            showLoading()
            repository.getUserProfile(userId)
                .onSuccess { profile -> /* 处理成功 */ }
                .onFailure { error -> /* 处理错误 */ }
            hideLoading()
        }
    }
}
```

**新方式（使用 UseCase）**：
```kotlin
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfile: GetUserProfileUseCase
) : BaseViewModel() {
    
    fun loadProfile(userId: String) {
        viewModelScope.launch {
            showLoading()
            getUserProfile(userId)
                .onSuccess { profile -> /* 处理成功 */ }
                .onFailure { error -> /* 处理错误 */ }
            hideLoading()
        }
    }
}
```

**优势**：
- 业务逻辑从 ViewModel 中分离，ViewModel 更简洁
- UseCase 可以在多个 ViewModel 中复用
- UseCase 可以独立测试，无需模拟 ViewModel

## 常见问题

### Q: 什么时候应该创建 UseCase？

**A**: 当业务逻辑满足以下条件之一时，应该创建 UseCase：

1. 业务逻辑需要在多个地方复用
2. 业务逻辑比较复杂，需要多个步骤
3. 业务逻辑需要独立测试
4. 业务逻辑涉及多个 Repository 的协调

### Q: UseCase 可以调用其他 UseCase 吗？

**A**: 可以，但要谨慎使用。如果一个 UseCase 需要调用另一个 UseCase，说明可能存在更高层次的业务逻辑。

```kotlin
class ComplexBusinessUseCase @Inject constructor(
    private val getUserProfile: GetUserProfileUseCase,
    private val getOrderList: GetOrderListUseCase
) : BaseUseCase<String, ComplexBusinessResult>() {
    
    override suspend fun execute(params: String): ComplexBusinessResult {
        val profile = getUserProfile(params).getOrThrow()
        val orders = getOrderList(params).getOrThrow()
        return ComplexBusinessResult(profile, orders)
    }
}
```

### Q: UseCase 应该返回 Result 还是直接抛出异常？

**A**: 推荐返回 `Result<T>`，因为：

1. 调用方可以明确知道操作可能失败
2. 可以使用 `onSuccess` 和 `onFailure` 优雅地处理结果
3. 符合 Kotlin 的函数式编程风格

### Q: UseCase 可以访问 Android 框架类吗？

**A**: 不推荐。UseCase 应该是纯 Kotlin 代码，不依赖 Android 框架。如果需要访问 Android 资源，应该通过 Repository 或其他抽象层。

## 总结

UseCase 是 Clean Architecture 的核心，通过引入 UseCase 层，我们可以：

- ✅ 清晰的职责划分
- ✅ 更好的可测试性
- ✅ 更高的可复用性
- ✅ 更容易维护和扩展

遵循本指南的最佳实践，可以帮助你构建更加健壮和可维护的应用程序。
