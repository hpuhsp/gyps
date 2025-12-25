package com.swallow.fly.domain.usecase

/**
 * UseCase 基础接口
 * 
 * UseCase 封装单一业务逻辑的可执行单元，遵循 Clean Architecture 原则。
 * 
 * @param P 输入参数类型
 * @param R 返回结果类型
 * 
 * @author Swallow Framework
 * @since 2.0.0
 * 
 * 使用示例：
 * ```kotlin
 * class GetUserProfileUseCase @Inject constructor(
 *     private val userRepository: UserRepository
 * ) : UseCase<String, UserProfile> {
 *     override suspend fun invoke(params: String): Result<UserProfile> {
 *         return userRepository.getUserProfile(params)
 *     }
 * }
 * 
 * // 在 ViewModel 中使用
 * @HiltViewModel
 * class ProfileViewModel @Inject constructor(
 *     private val getUserProfile: GetUserProfileUseCase
 * ) : BaseViewModel() {
 *     fun loadProfile(userId: String) {
 *         viewModelScope.launch {
 *             getUserProfile(userId)
 *                 .onSuccess { profile -> /* 更新 UI */ }
 *                 .onFailure { error -> /* 处理错误 */ }
 *         }
 *     }
 * }
 * ```
 */
interface UseCase<in P, out R> {
    /**
     * 执行 UseCase
     * 
     * @param params 输入参数
     * @return Result<R> 执行结果，成功返回 Result.success，失败返回 Result.failure
     */
    suspend operator fun invoke(params: P): Result<R>
}

/**
 * 无参数 UseCase 接口
 * 
 * 用于不需要输入参数的业务逻辑。
 * 
 * @param R 返回结果类型
 * 
 * 使用示例：
 * ```kotlin
 * class GetCurrentUserUseCase @Inject constructor(
 *     private val userRepository: UserRepository
 * ) : NoParamsUseCase<User> {
 *     override suspend fun execute(): User {
 *         return userRepository.getCurrentUser().getOrThrow()
 *     }
 * }
 * 
 * // 调用
 * viewModelScope.launch {
 *     getCurrentUser()
 *         .onSuccess { user -> /* 处理用户 */ }
 *         .onFailure { error -> /* 处理错误 */ }
 * }
 * ```
 */
interface NoParamsUseCase<out R> : UseCase<Unit, R> {
    /**
     * 执行无参数 UseCase
     * 
     * @return Result<R> 执行结果
     */
    suspend operator fun invoke(): Result<R> = invoke(Unit)
}

/**
 * Flow UseCase 接口
 * 
 * 用于返回 Flow 流的业务逻辑，适合需要持续观察数据变化的场景。
 * 
 * @param P 输入参数类型
 * @param R 返回结果类型
 * 
 * 使用示例：
 * ```kotlin
 * class ObserveUserProfileUseCase @Inject constructor(
 *     private val userRepository: UserRepository
 * ) : FlowUseCase<String, UserProfile> {
 *     override fun invoke(params: String): Flow<Result<UserProfile>> {
 *         return userRepository.observeUserProfile(params)
 *     }
 * }
 * 
 * // 在 ViewModel 中使用
 * init {
 *     observeUserProfile(userId)
 *         .onEach { result ->
 *             result.onSuccess { profile -> /* 更新 UI */ }
 *                   .onFailure { error -> /* 处理错误 */ }
 *         }
 *         .launchIn(viewModelScope)
 * }
 * ```
 */
interface FlowUseCase<in P, out R> {
    /**
     * 执行 Flow UseCase
     * 
     * @param params 输入参数
     * @return Flow<Result<R>> 结果流
     */
    operator fun invoke(params: P): kotlinx.coroutines.flow.Flow<Result<R>>
}
