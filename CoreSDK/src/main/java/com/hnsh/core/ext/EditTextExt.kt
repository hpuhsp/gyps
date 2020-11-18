package com.hnsh.core.ext

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.EditText
import android.widget.TextView
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.whileSelect
import java.util.concurrent.Flow
import java.util.concurrent.TimeUnit


/**
 * @Description: Extension function to simplify setting an afterTextChanged action to EditText components.
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/26 9:35
 * @UpdateRemark:   更新说明：
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

fun TextView.onEditorAction(editorAction: (TextView, Int, KeyEvent) -> Boolean) {
    this.setOnEditorActionListener { p0, p1, p2 ->
        if (p2 == null) {
            editorAction(p0, p1, KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_UNKNOWN))
        } else {
            editorAction(p0, p1, p2)
        }
    }
}

/**
 * 简单的实现控制输入速度（可以考虑利用Kotlin Flow或者协程Channel类去实现）
 */
fun EditText.afterChangedFilter(afterTextChanged: (String) -> Unit) {
    val debounceLimit = 300L
    this.tag = 0
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            if (editable.isNullOrEmpty()) {
                afterTextChanged.invoke("")
                return
            }
            val offTimeout = System.currentTimeMillis()
            if (offTimeout - tag.toString().toLong() > debounceLimit) {
                tag = offTimeout
                afterTextChanged.invoke(editable.toString())
            }
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

