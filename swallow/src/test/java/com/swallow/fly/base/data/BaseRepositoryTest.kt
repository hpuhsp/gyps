package com.swallow.fly.base.data

import app.cash.turbine.test
import com.swallow.fly.http.manager.RepositoryManager
import com.swallow.fly.http.result.HttpResult
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.IOException

/**
 * BaseRepository 单元测试
 *
 * 测试覆盖：
 * - executeRequest 成功和失败场景
 * - flowRequest 的 Flow 行为
 * - cachedFlowRequest 的缓存策略
 * - obtainService 的服务获取
 */
class BaseRepositoryTest {

    private lateinit var repository: TestRepository
    private lateinit var mockRepositoryManager: RepositoryManager

    @Before
    fun setup() {
        mockRepositoryManager = mockk(relaxed = true)
        repository = TestRepository().apply {
            repositoryManager = mockRepositoryManager
        }
    }

    @Test
    fun `executeRequest should return Success when request succeeds`() = runTest {
        // Given
        val expectedData = "test data"

        // When
        val result = repository.executeRequest { expectedData }

        // Then
        assertTrue(result is HttpResult.Success)
        assertEquals(expectedData, (result as HttpResult.Success).value)
    }

    @Test
    fun `executeRequest should return Failure when request throws exception`() = runTest {
        // Given
        val exception = IOException("Network error")
        every { mockRepositoryManager.handleResponseError(any()) } returns exception

        // When
        val result = repository.executeRequest<String> { throw exception }

        // Then
        assertTrue(result is HttpResult.Failure)
        assertEquals(exception, (result as HttpResult.Failure).throwable)
    }

    @Test
    fun `flowRequest should emit Success result`() = runTest {
        // Given
        val expectedData = "test data"

        // When & Then
        repository.testFlowRequest { expectedData }.test {
            val result = awaitItem()
            assertTrue(result is HttpResult.Success)
            assertEquals(expectedData, (result as HttpResult.Success).value)
            awaitComplete()
        }
    }

    @Test
    fun `flowRequest should emit Failure result when exception occurs`() = runTest {
        // Given
        val exception = IOException("Network error")
        every { mockRepositoryManager.handleResponseError(any()) } returns exception

        // When & Then
        repository.testFlowRequest<String> { throw exception }.test {
            val result = awaitItem()
            assertTrue(result is HttpResult.Failure)
            assertEquals(exception, (result as HttpResult.Failure).throwable)
            awaitComplete()
        }
    }

    @Test
    fun `cachedFlowRequest should emit cached data first then network data`() = runTest {
        // Given
        val cachedData = "cached data"
        val networkData = "network data"
        val cacheKey = "test_key"

        coEvery { mockRepositoryManager.getCache<String>(cacheKey) } returns cachedData
        coEvery { mockRepositoryManager.saveCache(cacheKey, networkData) } returns Unit

        // When & Then
        repository.testCachedFlowRequest(cacheKey) { networkData }.test {
            // First emission: cached data
            val cachedResult = awaitItem()
            assertTrue(cachedResult is HttpResult.Success)
            assertEquals(cachedData, (cachedResult as HttpResult.Success).value)

            // Second emission: network data
            val networkResult = awaitItem()
            assertTrue(networkResult is HttpResult.Success)
            assertEquals(networkData, (networkResult as HttpResult.Success).value)

            awaitComplete()
        }

        // Verify cache was updated
        verify { mockRepositoryManager.saveCache(cacheKey, networkData) }
    }

    @Test
    fun `cachedFlowRequest should only emit network data when cache is null`() = runTest {
        // Given
        val networkData = "network data"
        val cacheKey = "test_key"

        coEvery { mockRepositoryManager.getCache<String>(cacheKey) } returns null
        coEvery { mockRepositoryManager.saveCache(cacheKey, networkData) } returns Unit

        // When & Then
        repository.testCachedFlowRequest(cacheKey) { networkData }.test {
            // Only one emission: network data
            val result = awaitItem()
            assertTrue(result is HttpResult.Success)
            assertEquals(networkData, (result as HttpResult.Success).value)

            awaitComplete()
        }
    }

    @Test
    fun `cachedFlowRequest should continue when cache read fails`() = runTest {
        // Given
        val networkData = "network data"
        val cacheKey = "test_key"

        coEvery { mockRepositoryManager.getCache<String>(cacheKey) } throws IOException("Cache error")
        coEvery { mockRepositoryManager.saveCache(cacheKey, networkData) } returns Unit

        // When & Then
        repository.testCachedFlowRequest(cacheKey) { networkData }.test {
            // Should still emit network data despite cache error
            val result = awaitItem()
            assertTrue(result is HttpResult.Success)
            assertEquals(networkData, (result as HttpResult.Success).value)

            awaitComplete()
        }
    }

    @Test
    fun `obtainService should delegate to repositoryManager`() {
        // Given
        val mockService = mockk<TestService>()
        every { mockRepositoryManager.obtainService(TestService::class.java) } returns mockService

        // When
        val service = repository.testObtainService(TestService::class.java)

        // Then
        assertEquals(mockService, service)
        verify { mockRepositoryManager.obtainService(TestService::class.java) }
    }

    // Test implementations
    private class TestRepository : BaseRepository() {
        fun <T> testFlowRequest(request: suspend () -> T) = flowRequest(request)
        fun <T> testCachedFlowRequest(cacheKey: String, request: suspend () -> T) =
            cachedFlowRequest(cacheKey, request)
        fun <T> testObtainService(service: Class<T>) = obtainService(service)
    }

    private interface TestService
}
