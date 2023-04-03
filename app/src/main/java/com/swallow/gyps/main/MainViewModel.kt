package com.swallow.gyps.main

import androidx.lifecycle.viewModelScope
import com.swallow.fly.base.viewmodel.BaseViewModel
import com.swallow.fly.http.result.doSuccess
import com.swallow.gyps.main.models.HealthModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/11/19 11:13
 * @UpdateRemark:   更新说明：
 */
@HiltViewModel
class MainViewModel @Inject constructor(private val repository: MainRepository) :
    BaseViewModel() {

//    val sharedFlow = MutableSharedFlow<String>()
    
    
    fun testShareFlow() {
//        viewModelScope.launch {
//            sharedFlow.emit("第一步")
//            sharedFlow.emit("第二步")
//        }
    
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