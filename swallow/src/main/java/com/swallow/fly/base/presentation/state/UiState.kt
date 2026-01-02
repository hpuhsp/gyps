package com.swallow.fly.base.presentation.state

/**
 * @Description: UI 状态密封类
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2024/12
 * @UpdateRemark: 用于表示 UI 的各种状态
 */
sealed class UiState {
    /**
     * 初始状态
     * 用于 StateFlow 初始化，不产生任何副作用
     */
    object Init : UiState()

    /**
     * 空闲状态
     * 通常表示操作完成或重置，会触发隐藏 Loading
     */
    object Idle : UiState()
    
    /**
     * 加载中状态
     * @param message 加载提示消息
     */
    data class Loading(val message: String? = null) : UiState()
    
    /**
     * 成功状态
     * @param data 成功返回的数据
     */
    data class Success<T>(val data: T) : UiState()
    
    /**
     * 错误状态
     * @param message 错误消息
     */
    data class Error(val message: String) : UiState()
}
