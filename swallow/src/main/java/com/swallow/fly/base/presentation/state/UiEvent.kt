package com.swallow.fly.base.presentation.state

import android.content.Intent
import android.os.Bundle

/**
 * Activity 启动模式
 * 
 * 对应 Android 的四种启动模式
 * 
 * @since 1.0.0
 */
enum class LaunchMode {
    /**
     * 标准模式（默认）
     * 
     * 每次启动都会创建新的 Activity 实例
     */
    STANDARD,
    
    /**
     * 栈顶复用模式
     * 
     * 如果目标 Activity 已经在栈顶，则复用该实例，调用 onNewIntent()
     * 对应 Intent.FLAG_ACTIVITY_SINGLE_TOP
     */
    SINGLE_TOP,
    
    /**
     * 栈内复用模式
     * 
     * 如果目标 Activity 已经在栈中，则复用该实例，并清除其上的所有 Activity
     * 对应 Intent.FLAG_ACTIVITY_CLEAR_TOP
     */
    SINGLE_TASK,
    
    /**
     * 单实例模式
     * 
     * 目标 Activity 会在新的任务栈中创建，且该任务栈中只有这一个 Activity
     * 对应 Intent.FLAG_ACTIVITY_NEW_TASK
     */
    SINGLE_INSTANCE;

    /**
     * 转换为 Intent Flags
     */
    fun toIntentFlags(): Int {
        return when (this) {
            STANDARD -> 0
            SINGLE_TOP -> Intent.FLAG_ACTIVITY_SINGLE_TOP
            SINGLE_TASK -> Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            SINGLE_INSTANCE -> Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }
}

/**
 * UI 事件密封类
 * 
 * 用于表示一次性 UI 事件，通过 SharedFlow 发送
 * 
 * @since 1.0.0
 * @author Hsp
 */
sealed class UiEvent {
    /**
     * 显示 Toast 消息
     * 
     * @param message 消息内容
     */
    data class ShowToast(val message: String) : UiEvent()
    
    /**
     * 显示错误消息
     * 
     * @param message 错误消息
     */
    data class ShowError(val message: String) : UiEvent()
    
    /**
     * 导航事件
     * 
     * 支持多种导航方式：
     * - 路由导航（如 "/home", "/user/123"）
     * - Activity 类名导航
     * - Deep Link 导航
     * 
     * @param route 导航路由或目标
     * @param args 导航参数（可选）
     * @param launchMode Activity 启动模式（默认 STANDARD）
     * @param popUpTo 返回到指定路由（可选，用于清空返回栈）
     * @param inclusive 是否包含 popUpTo 的目标（默认 false）
     */
    data class Navigate(
        val route: String,
        val args: Bundle? = null,
        val launchMode: LaunchMode = LaunchMode.STANDARD,
        val popUpTo: String? = null,
        val inclusive: Boolean = false
    ) : UiEvent()
    
    /**
     * 返回上一页
     * 
     * @param result 返回结果（可选）
     */
    data class NavigateBack(val result: Bundle? = null) : UiEvent()
    
    /**
     * 显示对话框
     * 
     * @param title 标题
     * @param message 消息内容
     * @param positiveButton 确定按钮文本（可选）
     * @param negativeButton 取消按钮文本（可选）
     * @param tag 对话框标识（用于区分不同对话框）
     */
    data class ShowDialog(
        val title: String? = null,
        val message: String,
        val positiveButton: String? = "确定",
        val negativeButton: String? = null,
        val tag: String? = null
    ) : UiEvent()
    
    /**
     * 显示 SnackBar
     * 
     * @param message 消息内容
     * @param actionText 操作按钮文本（可选）
     * @param duration 显示时长（可选）
     */
    data class ShowSnackBar(
        val message: String,
        val actionText: String? = null,
        val duration: Int = 0  // 0: SHORT, 1: LONG, -1: INDEFINITE
    ) : UiEvent()
}
