package com.swallow.fly.ext

import android.view.View

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/8 14:57
 * @UpdateRemark:   更新说明：
 * 用法：
 * //不带过滤的普通点击
 * view.click {
 *       L.e("aaron click test")
 *      }
 *   //带默认600毫秒过滤的点击事件（方法1）
 *   view.clickWithTrigger {
 *      L.e("aaron clickWithTrigger test")
 *       }
 *   //带默认600毫秒过滤的点击事件（方法2）
 *   view.withTrigger().click {
 *      L.e("aaron click test")
 *  }
 *       //带700毫秒过滤的点击事件（方法1）
 *       bind.clickWithTrigger(700) {
 *   L.e("aaron clickWithTrigger test")
 *   }
 *   //带700毫秒过滤的点击事件（方法2）
 *   view.withTrigger(700).click {
 *   L.e("aaron click test")
 *       }
 */
/***
 * 设置延迟时间的View扩展
 * @param delay Long 延迟时间，默认600毫秒
 * @return T
 */
fun <T : View> T.withTrigger(delay: Long = 600): T {
    triggerDelay = delay
    return this
}

/***
 * 点击事件的View扩展
 * @param block: (T) -> Unit 函数
 * @return Unit
 */
fun <T : View> T.click(block: (T) -> Unit) = setOnClickListener {
    block(it as T)
}

/***
 * 带延迟过滤的点击事件View扩展
 * @param delay Long 延迟时间，默认600毫秒
 * @param block: (T) -> Unit 函数
 * @return Unit
 */
fun <T : View> T.clickWithTrigger(time: Long = 600, block: (T) -> Unit) {
    triggerDelay = time
    setOnClickListener {
        if (clickEnable()) {
            block(it as T)
        }
    }
}

private var <T : View> T.triggerLastTime: Long
    get() = if (getTag(1123460103) != null) getTag(1123460103) as Long else -601
    set(value) {
        setTag(1123460103, value)
    }

private var <T : View> T.triggerDelay: Long
    get() = if (getTag(1123461123) != null) getTag(1123461123) as Long else 600
    set(value) {
        setTag(1123461123, value)
    }

fun <T : View> T.clickEnable(): Boolean {
    var flag = false
    val currentClickTime = System.currentTimeMillis()
    if (currentClickTime - triggerLastTime >= triggerDelay) {
        flag = true
    }
    triggerLastTime = currentClickTime
    return flag
}

/***
 * 带延迟过滤的点击事件监听，见[View.OnClickListener]
 * 延迟时间根据triggerDelay获取：600毫秒，不能动态设置
 */
interface OnLazyClickListener : View.OnClickListener {

    override fun onClick(v: View?) {
        if (v?.clickEnable() == true) {
            onLazyClick(v)
        }
    }

    fun onLazyClick(v: View)
}