package com.swallow.fly.http.result

import org.junit.Assert.*
import org.junit.Test
import java.io.IOException

/**
 * HttpResult 单元测试
 *
 * 测试覆盖：
 * - Success 和 Failure 的创建
 * - isSuccess 和 isFailure 属性
 * - getOrNull 方法
 * - onSuccess 和 onFailure 扩展方法
 * - map 扩展方法
 * - 链式调用
 */
class HttpResultTest {

    @Test
    fun `Success should have correct properties`() {
        // Given
        val data = "test data"

        // When
        val result = HttpResult.Success(data)

        // Then
        assertTrue(result.isSuccess)
        assertFalse(result.isFailure)
        assertEquals(data, result.value)
        assertEquals(data, result.getOrNull())
    }

    @Test
    fun `Failure should have correct properties`() {
        // Given
        val exception = IOException("Network error")

        // When
        val result = HttpResult.Failure(exception)

        // Then
        assertFalse(result.isSuccess)
        assertTrue(result.isFailure)
        assertEquals(exception, result.throwable)
        assertNull(result.getOrNull())
    }

    @Test
    fun `onSuccess should execute action for Success result`() {
        // Given
        val data = "test data"
        val result = HttpResult.Success(data)
        var executed = false
        var receivedData: String? = null

        // When
        result.onSuccess {
            executed = true
            receivedData = it
        }

        // Then
        assertTrue(executed)
        assertEquals(data, receivedData)
    }

    @Test
    fun `onSuccess should not execute action for Failure result`() {
        // Given
        val result = HttpResult.Failure(IOException("Error"))
        var executed = false

        // When
        result.onSuccess { executed = true }

        // Then
        assertFalse(executed)
    }

    @Test
    fun `onFailure should execute action for Failure result`() {
        // Given
        val exception = IOException("Network error")
        val result = HttpResult.Failure(exception)
        var executed = false
        var receivedException: Throwable? = null

        // When
        result.onFailure {
            executed = true
            receivedException = it
        }

        // Then
        assertTrue(executed)
        assertEquals(exception, receivedException)
    }

    @Test
    fun `onFailure should not execute action for Success result`() {
        // Given
        val result = HttpResult.Success("data")
        var executed = false

        // When
        result.onFailure { executed = true }

        // Then
        assertFalse(executed)
    }

    @Test
    fun `map should transform Success data`() {
        // Given
        val result = HttpResult.Success(5)

        // When
        val mapped = result.map { it * 2 }

        // Then
        assertTrue(mapped is HttpResult.Success)
        assertEquals(10, (mapped as HttpResult.Success).value)
    }

    @Test
    fun `map should preserve Failure`() {
        // Given
        val exception = IOException("Error")
        val result: HttpResult<Int> = HttpResult.Failure(exception)

        // When
        val mapped = result.map { it * 2 }

        // Then
        assertTrue(mapped is HttpResult.Failure)
        assertEquals(exception, (mapped as HttpResult.Failure).throwable)
    }

    @Test
    fun `chaining onSuccess and onFailure should work correctly for Success`() {
        // Given
        val data = "test data"
        val result = HttpResult.Success(data)
        var successExecuted = false
        var failureExecuted = false

        // When
        result
            .onSuccess { successExecuted = true }
            .onFailure { failureExecuted = true }

        // Then
        assertTrue(successExecuted)
        assertFalse(failureExecuted)
    }

    @Test
    fun `chaining onSuccess and onFailure should work correctly for Failure`() {
        // Given
        val result = HttpResult.Failure(IOException("Error"))
        var successExecuted = false
        var failureExecuted = false

        // When
        result
            .onSuccess { successExecuted = true }
            .onFailure { failureExecuted = true }

        // Then
        assertFalse(successExecuted)
        assertTrue(failureExecuted)
    }

    @Test
    fun `map can be chained with onSuccess`() {
        // Given
        val result = HttpResult.Success(5)
        var finalValue: String? = null

        // When
        result
            .map { it * 2 }
            .map { "Value: $it" }
            .onSuccess { finalValue = it }

        // Then
        assertEquals("Value: 10", finalValue)
    }

    @Test
    fun `getOrNull should return null for Failure`() {
        // Given
        val result = HttpResult.Failure(IOException("Error"))

        // When
        val value = result.getOrNull()

        // Then
        assertNull(value)
    }

    @Test
    fun `getOrNull should return value for Success`() {
        // Given
        val data = "test data"
        val result = HttpResult.Success(data)

        // When
        val value = result.getOrNull()

        // Then
        assertEquals(data, value)
    }

    @Test
    fun `doSuccess deprecated method should still work`() {
        // Given
        val data = "test data"
        val result = HttpResult.Success(data)
        var executed = false

        // When
        @Suppress("DEPRECATION")
        result.doSuccess { executed = true }

        // Then
        assertTrue(executed)
    }

    @Test
    fun `doFailure deprecated method should still work`() {
        // Given
        val result: HttpResult<String> = HttpResult.Failure(IOException("Error"))
        var executed = false

        // When
        @Suppress("DEPRECATION")
        result.doFailure { executed = true }

        // Then
        assertTrue(executed)
    }
}
