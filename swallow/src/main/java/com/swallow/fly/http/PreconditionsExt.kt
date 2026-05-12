package com.swallow.fly.http

/**
 * Kotlin 标准库扩展 - 替代 Preconditions.java
 * 
 * 使用 Kotlin 标准库的 require/check/requireNotNull 函数
 * 提供更简洁、类型安全的参数验证
 * 
 * @Description: 参数验证扩展函数
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2026/03/13
 * @UpdateRemark: 
 *   - 替代 Preconditions.java
 *   - 使用 Kotlin 标准库
 *   - 提供更好的类型推断
 */

/**
 * 检查参数是否为 null，如果为 null 则抛出 NullPointerException
 * 
 * 替代: Preconditions.checkNotNull(value, message)
 * 
 * @param lazyMessage 延迟计算的错误消息
 * @return 非 null 的值
 * @throws NullPointerException 如果值为 null
 */
inline fun <T : Any> T?.checkNotNull(lazyMessage: () -> String): T {
    return requireNotNull(this, lazyMessage)
}

/**
 * 检查参数条件是否为 true，如果为 false 则抛出 IllegalArgumentException
 * 
 * 替代: Preconditions.checkArgument(condition, message)
 * 
 * @param lazyMessage 延迟计算的错误消息
 * @throws IllegalArgumentException 如果条件为 false
 */
inline fun checkArgument(value: Boolean, lazyMessage: () -> String) {
    require(value, lazyMessage)
}

/**
 * 检查状态是否为 true，如果为 false 则抛出 IllegalStateException
 * 
 * 替代: Preconditions.checkState(condition, message)
 * 
 * @param lazyMessage 延迟计算的错误消息
 * @throws IllegalStateException 如果状态为 false
 */
inline fun checkState(value: Boolean, lazyMessage: () -> String) {
    check(value, lazyMessage)
}

/**
 * 检查索引是否在有效范围内
 * 
 * 替代: Preconditions.checkElementIndex(index, size)
 * 
 * @param size 集合大小
 * @param desc 索引描述（用于错误消息）
 * @return 有效的索引
 * @throws IndexOutOfBoundsException 如果索引无效
 */
fun Int.checkElementIndex(size: Int, desc: String = "index"): Int {
    if (this < 0 || this >= size) {
        throw IndexOutOfBoundsException("$desc ($this) must be in range [0, $size)")
    }
    return this
}

/**
 * 检查位置索引是否在有效范围内
 * 
 * 替代: Preconditions.checkPositionIndex(index, size)
 * 
 * @param size 集合大小
 * @param desc 索引描述（用于错误消息）
 * @return 有效的位置索引
 * @throws IndexOutOfBoundsException 如果索引无效
 */
fun Int.checkPositionIndex(size: Int, desc: String = "index"): Int {
    if (this < 0 || this > size) {
        throw IndexOutOfBoundsException("$desc ($this) must be in range [0, $size]")
    }
    return this
}

/**
 * 检查范围索引是否有效
 * 
 * 替代: Preconditions.checkPositionIndexes(start, end, size)
 * 
 * @param end 结束索引
 * @param size 集合大小
 * @throws IndexOutOfBoundsException 如果范围无效
 */
fun Int.checkPositionIndexes(end: Int, size: Int) {
    if (this < 0 || end < this || end > size) {
        throw IndexOutOfBoundsException(
            "Invalid range: start=$this, end=$end, size=$size"
        )
    }
}

// ==================== 迁移指南 ====================

/**
 * 迁移示例：
 * 
 * // 修改前（Java）
 * Preconditions.checkNotNull(user, "User must not be null");
 * Preconditions.checkArgument(age > 0, "Age must be positive");
 * Preconditions.checkState(isInitialized, "Not initialized");
 * 
 * // 修改后（Kotlin - 推荐使用标准库）
 * requireNotNull(user) { "User must not be null" }
 * require(age > 0) { "Age must be positive" }
 * check(isInitialized) { "Not initialized" }
 * 
 * // 或使用扩展函数（与旧代码风格更接近）
 * user.checkNotNull { "User must not be null" }
 * checkArgument(age > 0) { "Age must be positive" }
 * checkState(isInitialized) { "Not initialized" }
 */

