package com.swallow.fly.ext

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.SpannedString
import android.text.style.AbsoluteSizeSpan
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

/**
 * View 相关扩展函数
 *
 * 替代 FastUtils 中的 View 操作方法
 */

/** 设置 TextView hint 文字大小（sp） */
fun TextView.setHintSize(context: Context, size: Int, resId: Int) {
    val ss = SpannableString(context.getString(resId))
    ss.setSpan(AbsoluteSizeSpan(size, true), 0, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    hint = SpannedString(ss)
}

/** 通过资源名查找 View */
@Suppress("UNCHECKED_CAST")
fun <T : View?> View.findViewByName(context: Context, viewName: String?): T {
    val id = context.resources.getIdentifier(viewName, "id", context.packageName)
    return findViewById<View>(id) as T
}

/** 通过资源名查找 Activity 中的 View */
@Suppress("UNCHECKED_CAST")
fun <T : View?> Activity.findViewByName(viewName: String?): T {
    val id = resources.getIdentifier(viewName, "id", packageName)
    return findViewById<View>(id) as T
}

/** 从父容器中移除自身 */
fun View.removeFromParent() {
    val parent = parent
    if (parent is ViewGroup) {
        parent.removeView(this)
    }
}

/** 收起软键盘 */
fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

/** 配置 RecyclerView（固定高度场景） */
fun RecyclerView.setup(layoutManager: RecyclerView.LayoutManager?) {
    this.layoutManager = layoutManager
    setHasFixedSize(true)
    itemAnimator = DefaultItemAnimator()
}

/** 通过资源 id 获取 Drawable */
fun Context.getDrawableRes(rID: Int): Drawable? =
    ResourcesCompat.getDrawable(resources, rID, theme)

/** 通过颜色 id 获取颜色值 */
fun Context.getColorRes(rid: Int): Int =
    ResourcesCompat.getColor(resources, rid, theme)

/** 通过颜色名获取颜色值 */
fun Context.getColorByName(colorName: String?): Int {
    val id = resources.getIdentifier(colorName, "color", packageName)
    return getColorRes(id)
}

/** 通过 dimen id 获取尺寸（px，取整） */
fun Context.getDimenRes(id: Int): Int = resources.getDimension(id).toInt()

/** 通过 dimen 名获取尺寸（px） */
fun Context.getDimenByName(dimenName: String?): Float {
    val id = resources.getIdentifier(dimenName, "dimen", packageName)
    return resources.getDimension(id)
}

/** 通过 layout 名获取 layout id */
fun Context.findLayoutId(layoutName: String?): Int =
    resources.getIdentifier(layoutName, "layout", packageName)

/** 通过 string 名获取字符串 */
fun Context.getStringByName(strName: String?): String? {
    val id = resources.getIdentifier(strName, "string", packageName)
    return if (id == 0) null else getString(id)
}

/** 通过 string 数组 id 获取字符串数组 */
fun Context.getStringArrayRes(id: Int): Array<String?>? = resources.getStringArray(id)
