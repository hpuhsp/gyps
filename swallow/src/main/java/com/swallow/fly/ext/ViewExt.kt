package com.swallow.fly.ext

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible

/**
 * View 扩展函数
 * 提供 View 可见性、点击事件等扩展
 */

/**
 * 设置 View 可见
 */
fun View.visible() {
    visibility = View.VISIBLE
}

/**
 * 设置 View 不可见（占位）
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * 设置 View 隐藏（不占位）
 */
fun View.gone() {
    visibility = View.GONE
}

/**
 * 切换 View 可见性
 */
fun View.toggleVisibility() {
    visibility = if (isVisible) View.GONE else View.VISIBLE
}

/**
 * 根据条件设置可见性
 */
fun View.visibleIf(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.GONE
}

/**
 * 根据条件设置可见性（不可见时占位）
 */
fun View.visibleOrInvisible(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.INVISIBLE
}

/**
 * 防抖点击
 * 防止快速重复点击
 *
 * @param interval 防抖间隔（毫秒），默认 500ms
 * @param action 点击事件
 */
fun View.setOnClickListenerWithDebounce(interval: Long = 500, action: (View) -> Unit) {
    var lastClickTime = 0L
    setOnClickListener { view ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > interval) {
            lastClickTime = currentTime
            action(view)
        }
    }
}

/**
 * 防抖点击（简化版）
 */
fun View.onClick(interval: Long = 500, action: (View) -> Unit) {
    setOnClickListenerWithDebounce(interval, action)
}

/**
 * 单次点击
 * 点击后自动移除监听器
 */
fun View.setOnClickListenerOnce(action: (View) -> Unit) {
    setOnClickListener(object : View.OnClickListener {
        override fun onClick(v: View) {
            action(v)
            v.setOnClickListener(null)
        }
    })
}

/**
 * 设置 View 的宽度
 */
fun View.setWidth(width: Int) {
    val params = layoutParams
    params.width = width
    layoutParams = params
}

/**
 * 设置 View 的高度
 */
fun View.setHeight(height: Int) {
    val params = layoutParams
    params.height = height
    layoutParams = params
}

/**
 * 设置 View 的宽高
 */
fun View.setSize(width: Int, height: Int) {
    val params = layoutParams
    params.width = width
    params.height = height
    layoutParams = params
}

/**
 * 设置 View 的 margin
 */
fun View.setMargin(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
    val params = layoutParams as? ViewGroup.MarginLayoutParams
    params?.setMargins(left, top, right, bottom)
    layoutParams = params
}

/**
 * 设置 View 的 padding
 */
fun View.setPadding(padding: Int) {
    setPadding(padding, padding, padding, padding)
}

/**
 * 获取 View 的截图
 */
fun View.toBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    draw(canvas)
    return bitmap
}

/**
 * 测量 View
 */
fun View.measure(): Pair<Int, Int> {
    measure(
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )
    return Pair(measuredWidth, measuredHeight)
}

/**
 * 延迟执行
 */
fun View.postDelayed(delayMillis: Long, action: () -> Unit) {
    postDelayed(action, delayMillis)
}

/**
 * 在下一帧执行
 */
fun View.postOnNextFrame(action: () -> Unit) {
    post(action)
}

/**
 * 启用 View
 */
fun View.enable() {
    isEnabled = true
}

/**
 * 禁用 View
 */
fun View.disable() {
    isEnabled = false
}

/**
 * 根据条件启用/禁用 View
 */
fun View.enableIf(condition: Boolean) {
    isEnabled = condition
}

/**
 * 设置 View 的透明度（带动画）
 */
fun View.fadeIn(duration: Long = 300) {
    animate()
        .alpha(1f)
        .setDuration(duration)
        .start()
}

/**
 * 淡出 View（带动画）
 */
fun View.fadeOut(duration: Long = 300) {
    animate()
        .alpha(0f)
        .setDuration(duration)
        .start()
}

/**
 * 缩放显示 View（带动画）
 */
fun View.scaleIn(duration: Long = 300) {
    scaleX = 0f
    scaleY = 0f
    visible()
    animate()
        .scaleX(1f)
        .scaleY(1f)
        .setDuration(duration)
        .start()
}

/**
 * 缩放隐藏 View（带动画）
 */
fun View.scaleOut(duration: Long = 300) {
    animate()
        .scaleX(0f)
        .scaleY(0f)
        .setDuration(duration)
        .withEndAction { gone() }
        .start()
}

/**
 * 从底部滑入
 */
fun View.slideInFromBottom(duration: Long = 300) {
    translationY = height.toFloat()
    visible()
    animate()
        .translationY(0f)
        .setDuration(duration)
        .start()
}

/**
 * 滑出到底部
 */
fun View.slideOutToBottom(duration: Long = 300) {
    animate()
        .translationY(height.toFloat())
        .setDuration(duration)
        .withEndAction { gone() }
        .start()
}

/**
 * 从顶部滑入
 */
fun View.slideInFromTop(duration: Long = 300) {
    translationY = -height.toFloat()
    visible()
    animate()
        .translationY(0f)
        .setDuration(duration)
        .start()
}

/**
 * 滑出到顶部
 */
fun View.slideOutToTop(duration: Long = 300) {
    animate()
        .translationY(-height.toFloat())
        .setDuration(duration)
        .withEndAction { gone() }
        .start()
}

/**
 * 检查 View 是否在屏幕上可见
 */
fun View.isVisibleOnScreen(): Boolean {
    if (!isVisible) return false
    val actualPosition = IntArray(2)
    getLocationOnScreen(actualPosition)
    val screenWidth = context.getScreenWidth()
    val screenHeight = context.getScreenHeight()
    return actualPosition[0] < screenWidth &&
            actualPosition[0] + width > 0 &&
            actualPosition[1] < screenHeight &&
            actualPosition[1] + height > 0
}

/**
 * 获取 View 在屏幕上的位置
 */
fun View.getLocationOnScreen(): Pair<Int, Int> {
    val location = IntArray(2)
    getLocationOnScreen(location)
    return Pair(location[0], location[1])
}

/**
 * 获取 View 在窗口中的位置
 */
fun View.getLocationInWindow(): Pair<Int, Int> {
    val location = IntArray(2)
    getLocationInWindow(location)
    return Pair(location[0], location[1])
}
