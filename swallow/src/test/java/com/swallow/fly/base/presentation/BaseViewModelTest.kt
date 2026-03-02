package com.swallow.fly.base.presentation

import app.cash.turbine.test
import com.swallow.fly.base.presentation.state.UiEvent
import com.swallow.fly.base.presentation.state.UiState
import com.swallow.fly.test.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * BaseViewModel 单元测试
 * 
 * 测试 BaseViewModel 的核心功能：
 * - StateFlow 状态管理
 * - SharedFlow 事件管理
 * - 加载状态切换
 * - 错误处理
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {
    
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    
    private lateinit var viewModel: TestViewModel
    
    @Before
    fun setup() {
        viewModel = TestViewModel()
    }
    
    @Test
    fun `初始状态应该是 Init`() = runTest {
        // When
        val initialState = viewModel.uiState.value
        
        // Then
        assertTrue(initialState is UiState.Init)
    }
    
    @Test
    fun `showLoading 应该更新状态为 Loading`() = runTest {
        // Given
        val message = "加载中..."
        
        // When
        viewModel.testShowLoading(message)
        
        // Then
        val state = viewModel.uiState.value
        assertTrue(state is UiState.Loading)
        assertEquals(message, (state as UiState.Loading).message)
    }
    
    @Test
    fun `hideLoading 应该更新状态为 Idle`() = runTest {
        // Given
        viewModel.testShowLoading("加载中...")
        
        // When
        viewModel.testHideLoading()
        
        // Then
        val state = viewModel.uiState.value
        assertTrue(state is UiState.Idle)
    }
    
    @Test
    fun `showError 应该发送 ShowError 事件`() = runTest {
        // Given
        val errorMessage = "发生错误"
        
        // When & Then
        viewModel.uiEvent.test {
            viewModel.testShowError(errorMessage)
            
            val event = awaitItem()
            assertTrue(event is UiEvent.ShowError)
            assertEquals(errorMessage, (event as UiEvent.ShowError).message)
        }
    }
    
    @Test
    fun `showToast 应该发送 ShowToast 事件`() = runTest {
        // Given
        val toastMessage = "提示信息"
        
        // When & Then
        viewModel.uiEvent.test {
            viewModel.testShowToast(toastMessage)
            
            val event = awaitItem()
            assertTrue(event is UiEvent.ShowToast)
            assertEquals(toastMessage, (event as UiEvent.ShowToast).message)
        }
    }
    
    @Test
    fun `相同的 Loading 状态不应该重复发射`() = runTest {
        // When & Then
        viewModel.uiState.test {
            // 跳过初始值
            assertEquals(UiState.Init, awaitItem())
            
            // 第一次 showLoading
            viewModel.testShowLoading("加载中...")
            val firstLoading = awaitItem()
            assertTrue(firstLoading is UiState.Loading)
            
            // 第二次相同的 showLoading - StateFlow 会自动去重
            viewModel.testShowLoading("加载中...")
            
            // 不应该有新的发射
            expectNoEvents()
        }
    }
    
    @Test
    fun `不同的 Loading 消息应该发射新状态`() = runTest {
        // When & Then
        viewModel.uiState.test {
            // 跳过初始值
            assertEquals(UiState.Init, awaitItem())
            
            // 第一次 showLoading
            viewModel.testShowLoading("加载中...")
            val firstLoading = awaitItem()
            assertTrue(firstLoading is UiState.Loading)
            assertEquals("加载中...", (firstLoading as UiState.Loading).message)
            
            // 第二次不同的 showLoading
            viewModel.testShowLoading("正在处理...")
            val secondLoading = awaitItem()
            assertTrue(secondLoading is UiState.Loading)
            assertEquals("正在处理...", (secondLoading as UiState.Loading).message)
        }
    }
}

/**
 * 测试用的 ViewModel
 * 
 * 暴露 protected 方法用于测试
 */
class TestViewModel : BaseViewModel() {
    fun testShowLoading(message: String?) = showLoading(message)
    fun testHideLoading() = hideLoading()
    fun testShowError(message: String) = showError(message)
    fun testShowToast(message: String) = showToast(message)
}
