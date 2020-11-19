package com.swallow.fly.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.SpannedString
import android.text.style.AbsoluteSizeSpan
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import okhttp3.internal.and
import java.security.MessageDigest

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/26 10:30
 * @UpdateRemark:   更新说明：
 */
object FastUtils {
    var mToast: Toast? = null

    /**
     * 设置hint大小
     *
     * @param size
     * @param v
     * @param res
     */
    fun setViewHintSize(
        context: Context,
        size: Int,
        v: TextView,
        res: Int
    ) {
        val ss = SpannableString(
            getResources(context).getString(
                res
            )
        )
        // 新建一个属性对象,设置文字的大小
        val ass = AbsoluteSizeSpan(size, true)
        // 附加属性到文本
        ss.setSpan(ass, 0, ss.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // 设置hint
        v.hint = SpannedString(ss) // 一定要进行转换,否则属性会消失
    }

    /**
     * dp 转 px
     * @param context [Context]
     * @param dpValue `dpValue`
     * @return `pxValue`
     */
    fun dip2px(@NonNull context: Context, dpValue: Float): Int {
        val scale = getResources(context).displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * px 转 dp
     *
     * @param context [Context]
     * @param pxValue `pxValue`
     * @return `dpValue`
     */
    fun pix2dip(@NonNull context: Context, pxValue: Int): Int {
        val scale = getResources(context).displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * sp 转 px
     *
     * @param context [Context]
     * @param spValue `spValue`
     * @return `pxValue`
     */
    fun sp2px(@NonNull context: Context, spValue: Float): Int {
        val fontScale = getResources(context).displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    /**
     * px 转 sp
     *
     * @param context [Context]
     * @param pxValue `pxValue`
     * @return `spValue`
     */
    fun px2sp(@NonNull context: Context, pxValue: Float): Int {
        val fontScale = getResources(context).displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }

    /**
     * 获得资源
     */
    private fun getResources(context: Context): Resources {
        return context.resources
    }

    /**
     * 得到字符数组
     */
    fun getStringArray(
        context: Context,
        id: Int
    ): Array<String?>? {
        return getResources(context).getStringArray(id)
    }

    /**
     * 从 dimens 中获得尺寸
     * @param context
     * @param id
     * @return
     */
    fun getDimens(context: Context, id: Int): Int {
        return getResources(context).getDimension(id).toInt()
    }

    /**
     * 从 dimens 中获得尺寸
     * @param context
     * @param dimenName
     * @return
     */
    fun getDimens(context: Context, dimenName: String?): Float {
        return getResources(context).getDimension(
            getResources(context).getIdentifier(
                dimenName,
                "dimen",
                context.packageName
            )
        )
    }

    /**
     * 从String 中获得字符
     *
     * @return
     */
    fun getString(context: Context, stringID: Int): String? {
        return getResources(context).getString(stringID)
    }

    /**
     * 从String 中获得字符
     *
     * @return
     */
    fun getString(context: Context, strName: String?): String? {
        return getString(
            context,
            getResources(context).getIdentifier(strName, "string", context.packageName)
        )
    }

    /**
     * findview
     *
     * @param view
     * @param viewName
     * @param <T>
     * @return
    </T> */
    fun <T : View?> findViewByName(
        context: Context,
        view: View,
        viewName: String?
    ): T {
        val id = getResources(context).getIdentifier(viewName, "id", context.packageName)
        return view.findViewById<View>(id) as T
    }

    /**
     * findview
     *
     * @param activity
     * @param viewName
     * @param <T>
     * @return
    </T> */
    fun <T : View?> findViewByName(
        context: Context,
        activity: Activity,
        viewName: String?
    ): T {
        val id = getResources(context).getIdentifier(viewName, "id", context.packageName)
        return activity.findViewById<View>(id) as T
    }

    /**
     * 根据 layout 名字获得 id
     *
     * @param layoutName
     * @return
     */
    fun findLayout(context: Context, layoutName: String?): Int {
        return getResources(context).getIdentifier(layoutName, "layout", context.packageName)
    }

    /**
     * 填充view
     *
     * @param detailScreen
     * @return
     */
    fun inflate(context: Context?, detailScreen: Int): View? {
        return View.inflate(context, detailScreen, null)
    }

    /**
     * 单例 toast
     *
     * @param string
     */
    @SuppressLint("ShowToast")
    fun makeText(context: Context?, string: String?) {
        if (mToast == null) {
            mToast = Toast.makeText(context, string, Toast.LENGTH_SHORT)
        }
        mToast?.setText(string)
        mToast?.show()
    }

    /**
     * 通过资源id获得drawable
     *
     * @param rID
     * @return
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    fun getDrawablebyResource(context: Context, rID: Int): Drawable? {
        return getResources(context).getDrawable(rID)
    }

    /**
     * 跳转界面 3
     *
     * @param activity
     * @param homeActivityClass
     */
    fun startActivity(
        activity: Activity,
        homeActivityClass: Class<*>?
    ) {
        val intent = Intent(activity.applicationContext, homeActivityClass)
        activity.startActivity(intent)
    }

    /**
     * 跳转界面 4
     *
     * @param
     */
    fun startActivity(activity: Activity, intent: Intent?) {
        activity.startActivity(intent)
    }

    /**
     * 获得屏幕的宽度
     *
     * @return
     */
    fun getScreenWidth(context: Context): Int {
        return getResources(context).displayMetrics.widthPixels
    }

    /**
     * 获得屏幕的高度
     *
     * @return
     */
    fun getScreenHeight(context: Context): Int {
        return getResources(context).displayMetrics.heightPixels
    }

    /**
     * 获得颜色
     */
    fun getColor(context: Context, rid: Int): Int {
        return getResources(context).getColor(rid)
    }

    /**
     * 获得颜色
     */
    fun getColor(context: Context, colorName: String?): Int {
        return getColor(
            context,
            getResources(context).getIdentifier(colorName, "color", context.packageName)
        )
    }

    /**
     * 移除孩子
     *
     * @param view
     */
    fun removeChild(view: View) {
        val parent = view.parent
        if (parent is ViewGroup) {
            parent.removeView(view)
        }
    }

    fun isEmpty(obj: Any?): Boolean {
        return obj == null
    }

    /**
     * MD5
     *
     * @param string
     * @return
     * @throws Exception
     */
    fun encodeToMD5(string: String): String? {
        var hash = ByteArray(0)
        try {
            hash = MessageDigest.getInstance("MD5").digest(
                string.toByteArray(charset("UTF-8"))
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val hex = StringBuilder(hash.size * 2)
        for (b in hash) {
            if (b and 0xFF < 0x10) {
                hex.append("0")
            }
            hex.append(Integer.toHexString(b and 0xFF))
        }
        return hex.toString()
    }

    /**
     * 全屏,并且沉侵式状态栏
     *
     * @param activity
     */
    fun statusInScreen(activity: Activity) {
        val attrs = activity.window.attributes
        attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
        activity.window.attributes = attrs
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    /**
     * 配置 RecyclerView
     *
     * @param recyclerView
     * @param layoutManager
     */
    @Deprecated("Use {@link #configRecyclerView(RecyclerView, RecyclerView.LayoutManager)} instead")
    fun configRecycleView(
        recyclerView: RecyclerView
        , layoutManager: RecyclerView.LayoutManager?
    ) {
        recyclerView.layoutManager = layoutManager
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = DefaultItemAnimator()
    }

    /**
     * 配置 RecyclerView
     *
     * @param recyclerView
     * @param layoutManager
     */
    fun configRecyclerView(
        recyclerView: RecyclerView
        , layoutManager: RecyclerView.LayoutManager?
    ) {
        recyclerView.setLayoutManager(layoutManager)
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = DefaultItemAnimator()
    }

    /**
     * 收起软键盘
     */
    fun collapseSoftInputMethod(
        context: Context,
        v: View?
    ) {
        if (v != null) {
            val imm =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }
}