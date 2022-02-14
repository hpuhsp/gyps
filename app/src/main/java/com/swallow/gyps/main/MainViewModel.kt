package com.swallow.gyps.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.swallow.fly.base.viewmodel.BaseViewModel
import com.swallow.fly.ext.logd
import com.swallow.fly.http.result.doSuccess
import com.swallow.gyps.main.models.HealthModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/11/19 11:13
 * @UpdateRemark:   更新说明：
 */
class MainViewModel @ViewModelInject constructor(private val repository: MainRepository) :
    BaseViewModel() {

    val sharedFlow = MutableSharedFlow<String>()


    fun testShareFlow() {
        viewModelScope.launch {
            sharedFlow.emit("第一步")
            sharedFlow.emit("第二步")
        }

    }


    /**
     * 健康上报
     */
    fun reportHealthyStatus() {
        val model = HealthModel()
        model.answer10 = String.format("%.1f", Random.nextDouble(36.4, 36.9))
        viewModelScope.launch {
            repository.reportHealthyStatus(model)
                .onStart {
                    showLoading(-1, true)
                }
                .catch {
                    showError(-1, "")
                }
                .onCompletion {
                    hideAllDialog()
                }
                .collectLatest { result ->
                    result.doSuccess {
                        if (it.isSuccessful()) {
                            it.message?.let { str ->
                                showToast(str)
                            }
                        } else {
                            showError(it.code, it.message)
                        }
                    }
                }
        }
    }
}