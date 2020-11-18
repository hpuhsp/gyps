package com.hnsh.core.base.viewmodel

import androidx.lifecycle.*
import com.hnsh.core.base.ViewBehavior
import com.hnsh.core.base.event.BaseStateEvent
import com.hnsh.core.base.event.EventArgs

/**
 * @Description:
 * @Author: Hsp
 * @Email:  1101121039@qq.com
 * @CreateTime: 2020/8/24 10:25
 * @UpdateRemark:
 */
open class BaseViewModel : ViewModel(), ViewBehavior, LifecycleObserver {
    /**
     * 可基于此类进行扩展
     */
    private val _pageStateEvent = MutableLiveData<BaseStateEvent>()
    val pageStateEvent: LiveData<BaseStateEvent> = _pageStateEvent

    val _failure = MutableLiveData<String>()
    val failure = _failure

    override fun showLoading(msg: Int) {
        _pageStateEvent.value = BaseStateEvent(event = EventArgs.SHOW_LOADING, message = msg)
    }

    override fun showConfirmDialog(content: String) {
        _pageStateEvent.value = BaseStateEvent(
            event = EventArgs.SHOW_CONFIRM,
            content = content
        )
    }

    override fun hideAllDialog() {
        _pageStateEvent.value = BaseStateEvent(event = EventArgs.HIDE_DIALOG)
    }

    override fun showError(code: Int, message: String?) {
        _pageStateEvent.value = BaseStateEvent(
            event = EventArgs.SHOW_ERROR,
            code = code,
            errorMsg = message
        )
    }

    override fun showToast(message: String) {
        _pageStateEvent.value = BaseStateEvent(
            event = EventArgs.SHOW_TOAST,
            toastMsg = message
        )
    }

    override fun showToast(message: Int) {
        _pageStateEvent.value = BaseStateEvent(
            event = EventArgs.SHOW_TOAST,
            message = message
        )
    }

    override fun startActivity(cls: Class<*>?) {

    }

    override fun killApp() {
        _pageStateEvent.value = BaseStateEvent(event = EventArgs.KILL_APP)
    }
}