package com.swallow.fly.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.SpannedString
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.swallow.fly.ext.dp2px
import com.swallow.fly.ext.enterImmersiveFullscreen
import com.swallow.fly.ext.getColorRes
import com.swallow.fly.ext.getDrawableRes
import com.swallow.fly.ext.hideKeyboard
import com.swallow.fly.ext.px2dp
import com.swallow.fly.ext.px2sp
import com.swallow.fly.ext.setup
import com.swallow.fly.ext.sp2px
import com.swallow.fly.ext.toMD5
import java.security.MessageDigest

/**
 * @Description: 通用工具类
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/26 10:30
 * @UpdateRemark:   已拆分为独立扩展文件，本类方法均已废弃，请迁移到对应扩展函数
 *
 * 迁移指南：
 * - 单位换算  → com.swallow.fly.ext.DensityExt
 * - View 操作 → com.swallow.fly.ext.ViewExt
 * - Activity  → com.swallow.fly.ext.ActivityExt
 * - 字符串    → com.swallow.fly.ext.StringExt
 */
@Deprecated("FastUtils 已拆分为独立扩展文件，请迁移到 com.swallow.fly.ext 包下的对应扩展函数")
object FastUtils {
    var mToast: Toast? = null

    @Deprecated("Use TextView.setHintSize()", ReplaceWith("v.setHintSize(context, size, res)", "com.swallow.fly.ext.setHintSize"))
    fun setViewHintSize(context: Context, size: Int, v: TextView, res: Int) {
        val ss = SpannableString(context.resources.getString(res))
        val ass = AbsoluteSizeSpan(size, true)
        ss.setSpan(ass, 0, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        v.hint = SpannedString(ss)
    }

    @Deprecated("Use Context.dp2px()", ReplaceWith("context.dp2px(dpValue)", "com.swallow.fly.ext.dp2px"))
    fun dip2px(@NonNull context: Context, dpValue: Float): Int = context.dp2px(dpValue)

    @Deprecated("Use Context.px2dp()", ReplaceWith("context.px2dp(pxValue.toFloat())", "com.swallow.fly.ext.px2dp"))
    fun pix2dip(@NonNull context: Context, pxValue: Int): Int = context.px2dp(pxValue.toFloat())

    @Deprecated("Use Context.sp2px()", ReplaceWith("context.sp2px(spValue)", "com.swallow.fly.ext.sp2px"))
    fun sp2px(@NonNull context: Context, spValue: Float): Int = context.sp2px(spValue)

    @Deprecated("Use Context.px2sp()", ReplaceWith("context.px2sp(pxValue)", "com.swallow.fly.ext.px2sp"))
    fun px2sp(@NonNull context: Context, pxValue: Float): Int = context.px2sp(pxValue)

    private fun getResources(context: Context): Resources = context.resources

    @Deprecated("Use Context.getStringArrayRes()", ReplaceWith("context.getStringArrayRes(id)", "com.swallow.fly.ext.getStringArrayRes"))
    fun getStringArray(context: Context, id: Int): Array<String?>? = context.resources.getStringArray(id)

    @Deprecated("Use Context.getDimenRes()", ReplaceWith("context.getDimenRes(id)", "com.swallow.fly.ext.getDimenRes"))
    fun getDimens(context: Context, id: Int): Int = context.resources.getDimension(id).toInt()

    @Deprecated("Use Context.getDimenByName()", ReplaceWith("context.getDimenByName(dimenName)", "com.swallow.fly.ext.getDimenByName"))
    fun getDimens(context: Context, dimenName: String?): Float {
        val id = context.resources.getIdentifier(dimenName, "dimen", context.packageName)
        return context.resources.getDimension(id)
    }

    @Deprecated("Use Context.getString()", ReplaceWith("context.getString(stringID)"))
    fun getString(context: Context, stringID: Int): String? = context.getString(stringID)

    @Deprecated("Use Context.getStringByName()", ReplaceWith("context.getStringByName(strName)", "com.swallow.fly.ext.getStringByName"))
    fun getString(context: Context, strName: String?): String? {
        val id = context.resources.getIdentifier(strName, "string", context.packageName)
        return if (id == 0) null else context.getString(id)
    }

    @Deprecated("Use View.findViewByName()", ReplaceWith("view.findViewByName(context, viewName)", "com.swallow.fly.ext.findViewByName"))
    fun <T : View?> findViewByName(context: Context, view: View, viewName: String?): T {
        val id = context.resources.getIdentifier(viewName, "id", context.packageName)
        @Suppress("UNCHECKED_CAST")
        return view.findViewById<View>(id) as T
    }

    @Deprecated("Use Activity.findViewByName()", ReplaceWith("activity.findViewByName(viewName)", "com.swallow.fly.ext.findViewByName"))
    fun <T : View?> findViewByName(context: Context, activity: Activity, viewName: String?): T {
        val id = context.resources.getIdentifier(viewName, "id", context.packageName)
        @Suppress("UNCHECKED_CAST")
        return activity.findViewById<View>(id) as T
    }

    @Deprecated("Use Context.findLayoutId()", ReplaceWith("context.findLayoutId(layoutName)", "com.swallow.fly.ext.findLayoutId"))
    fun findLayout(context: Context, layoutName: String?): Int =
        context.resources.getIdentifier(layoutName, "layout", context.packageName)

    @Deprecated("Use View.inflate()")
    fun inflate(context: Context?, detailScreen: Int): View? = View.inflate(context, detailScreen, null)

    @SuppressLint("ShowToast")
    @Deprecated("Use Context.showToast() from ToastExt")
    fun makeText(context: Context?, string: String?) {
        if (mToast == null) {
            mToast = Toast.makeText(context, string, Toast.LENGTH_SHORT)
        }
        mToast?.setText(string)
        mToast?.show()
    }

    @Deprecated("Use Context.getDrawableRes()", ReplaceWith("context.getDrawableRes(rID)", "com.swallow.fly.ext.getDrawableRes"))
    fun getDrawablebyResource(context: Context, rID: Int): Drawable? = context.getDrawableRes(rID)

    @Deprecated("Use Activity.startActivity(clazz)", ReplaceWith("activity.startActivity(homeActivityClass)", "com.swallow.fly.ext.startActivity"))
    fun startActivity(activity: Activity, homeActivityClass: Class<*>?) {
        val intent = Intent(activity.applicationContext, homeActivityClass)
        activity.startActivity(intent)
    }

    @Deprecated("Use Activity.startActivity(intent)")
    fun startActivity(activity: Activity, intent: Intent?) {
        activity.startActivity(intent)
    }

    @Deprecated("Use Context.getScreenWidth()", ReplaceWith("context.resources.displayMetrics.widthPixels"))
    fun getScreenWidth(context: Context): Int = context.resources.displayMetrics.widthPixels

    @Deprecated("Use Context.getScreenHeight()", ReplaceWith("context.resources.displayMetrics.heightPixels"))
    fun getScreenHeight(context: Context): Int = context.resources.displayMetrics.heightPixels

    @Deprecated("Use Context.getColorRes()", ReplaceWith("context.getColorRes(rid)", "com.swallow.fly.ext.getColorRes"))
    fun getColor(context: Context, rid: Int): Int = context.getColorRes(rid)

    @Deprecated("Use Context.getColorByName()", ReplaceWith("context.getColorByName(colorName)", "com.swallow.fly.ext.getColorByName"))
    fun getColor(context: Context, colorName: String?): Int {
        val id = context.resources.getIdentifier(colorName, "color", context.packageName)
        return context.getColorRes(id)
    }

    @Deprecated("Use View.removeFromParent()", ReplaceWith("view.removeFromParent()", "com.swallow.fly.ext.removeFromParent"))
    fun removeChild(view: View) {
        val parent = view.parent
        if (parent is ViewGroup) parent.removeView(view)
    }

    @Deprecated("Use obj == null directly")
    fun isEmpty(obj: Any?): Boolean = obj == null

    @Deprecated("Use String.toMD5()", ReplaceWith("string.toMD5()", "com.swallow.fly.ext.toMD5"))
    fun encodeToMD5(string: String): String? = string.toMD5()

    @Suppress("DEPRECATION")
    @Deprecated("Use Activity.enterImmersiveFullscreen()", ReplaceWith("activity.enterImmersiveFullscreen()", "com.swallow.fly.ext.enterImmersiveFullscreen"))
    fun statusInScreen(activity: Activity) = activity.enterImmersiveFullscreen()

    @Deprecated("Use RecyclerView.setup()", ReplaceWith("recyclerView.setup(layoutManager)", "com.swallow.fly.ext.setup"))
    fun configRecycleView(recyclerView: RecyclerView, layoutManager: RecyclerView.LayoutManager?) {
        recyclerView.setup(layoutManager)
    }

    @Deprecated("Use RecyclerView.setup()", ReplaceWith("recyclerView.setup(layoutManager)", "com.swallow.fly.ext.setup"))
    fun configRecyclerView(recyclerView: RecyclerView, layoutManager: RecyclerView.LayoutManager?) {
        recyclerView.setup(layoutManager)
    }

    @Deprecated("Use View.hideKeyboard()", ReplaceWith("v?.hideKeyboard()", "com.swallow.fly.ext.hideKeyboard"))
    fun collapseSoftInputMethod(context: Context, v: View?) {
        v?.hideKeyboard()
    }
}
