package com.swallow.fly.base.presentation.state

/**
 * @Description: UI 事件密封类
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2024/12
 * @UpdateRemark: 用于表示一次性 UI 事件
 */
sealed class UiEvent {
    /**
     * 显示 Toast 消息
     * @param message 消息内容
     */
    data class ShowToast(val message: String) : UiEvent()
    
    /**
     * 显示错误消息
     * @param message 错误消息
     */
    data class ShowError(val message: String) : UiEvent()
    
    /**
     * 导航事件
     * @param route 导航路由
     */
    data class Navigate(val route: String) : UiEvent()
}
