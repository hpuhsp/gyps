package com.hsp.resource.ext

import android.text.TextUtils
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import com.hsp.resource.R

/**
 * 初始化新的白色风格的ActionBar
 * @param hasBackButton 是否显示的返回按钮
 * @param title         标题
 */
fun AppCompatActivity.initActionBar(toolbar: Toolbar, hasBackButton: Boolean, title: String?) {
    toolbar.title = ""
    setSupportActionBar(toolbar)
    val mActionBar = supportActionBar
    if (hasBackButton) {
        mActionBar?.setHomeAsUpIndicator(R.drawable.ic_toolbar_back)
        mActionBar?.setDisplayHomeAsUpEnabled(true)
        mActionBar?.setHomeButtonEnabled(true)
    } else {
        mActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_TITLE
        mActionBar?.setDisplayUseLogoEnabled(false)
        if (!TextUtils.isEmpty(title)) {
            mActionBar?.setTitle(title)
        } else {
//            mActionBar?.setTitle(resources.getString(R.string.app_name))
        }
    }
    mActionBar?.setBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.bg_white_tool_bar, theme))
    val tvTitle = toolbar.findViewById<TextView>(R.id.tv_title)
    tvTitle.setTextColor(ResourcesCompat.getColor(resources, R.color.text_black, theme))
    tvTitle.text = title ?: ""
}

fun AppCompatActivity.initBlueActionBar(toolbar: Toolbar, hasBackButton: Boolean, title: String?) {
    toolbar.title = ""
    setSupportActionBar(toolbar)
    val mActionBar = supportActionBar
    if (hasBackButton) {
        mActionBar?.setHomeAsUpIndicator(R.drawable.toolbar_back)
        mActionBar?.setDisplayHomeAsUpEnabled(true)
        mActionBar?.setHomeButtonEnabled(true)
    } else {
        mActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_TITLE
        mActionBar?.setDisplayUseLogoEnabled(false)
//        if (!TextUtils.isEmpty(title)) {
//            mActionBar?.setTitle(title)
//        } else {
////            mActionBar?.setTitle(resources.getString(R.string.app_name))
//        }
    }
    mActionBar?.setBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.bg_blue_tool_bar, theme))
    val tvTitle = toolbar.findViewById<TextView>(R.id.tv_title)
    tvTitle.setTextColor(ResourcesCompat.getColor(resources, R.color.white, theme))
    tvTitle.text = title ?: ""
}