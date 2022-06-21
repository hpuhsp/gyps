package com.swallow.gyps.test

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.swallow.fly.base.viewmodel.BaseViewModel
import com.swallow.fly.http.result.doSuccess
import com.swallow.gyps.R
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2021/6/2 11:27
 * @UpdateRemark:   更新说明：
 */
class TestFViewModel @ViewModelInject constructor(private val repository: LoginRepository) :
    BaseViewModel() {
    
    /**
     * 检查App版本
     */
    private val _versionData = MutableLiveData<UpdateInfoModel>()
    val versionData: LiveData<UpdateInfoModel> = _versionData
    fun checkAppVersion(autoCheck: Boolean) {
        viewModelScope.launch {
            repository.getVersionInfo("ZC")
                .onStart {
                    if (!autoCheck) {
                        showLoading(R.string.loading_data)
                    }
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
                            _versionData.value = UpdateInfoModel(it.data, autoCheck)
                        } else {
                            if (!autoCheck) {
                                showError(it.code, it.message)
                            }
                        }
                    }
                }
        }
    }
}