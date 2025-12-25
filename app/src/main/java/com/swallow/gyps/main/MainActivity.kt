package com.swallow.gyps.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.hsp.resource.ext.initBlueActionBar
import com.swallow.fly.base.ui.activity.BaseActivity
import com.swallow.fly.ext.logd
import com.swallow.gyps.R
import com.swallow.gyps.databinding.ActivityMainBinding
import com.swallow.gyps.msc.SignUpActivity
import com.swallow.gyps.msc.VoiceInputActivity
import com.swallow.gyps.test.TestActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(), View.OnClickListener {
    // 使用 by viewModels() 委托获取 ViewModel
    override val viewModel: MainViewModel by viewModels()
    
    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate
    
    override fun initView(savedInstanceState: Bundle?) {
        initBlueActionBar(binding.includeTitle.toolbar, false, "Gyps")
    }
    
    override fun initData(savedInstanceState: Bundle?) {
        // BaseActivity 会自动调用 observeViewModel()
        // 无需手动调用
    }
    
    private fun test() {
        lifecycleScope.launch(Dispatchers.IO) {
            val flow2 = (1..10).asFlow().onEach { delay(1000) }
            val job: Job = lifecycleScope.launch {
                logd { "-------------------------->lifecycleScope.launch" }
                flow2.flowOn(Dispatchers.IO)//设定它运行时所使用的调度器
                    .collect { // 消费Flow
                        logd { "-------------------------->flow2:$it" }
                    }
            }
            delay(2200)
            job.cancelAndJoin()
        }
    }
    
    override fun getStatusBarColor(): Int {
        return com.hsp.resource.R.color.toolbar_blue
    }
    
    override fun showDarkToolBar(): Boolean {
        return false
    }
    
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_face_identify -> SignUpActivity.start(this)
            
            R.id.btn_voice_input -> VoiceInputActivity.start(this)
            
            R.id.btn_report -> { // 测试支持组件
//                mViewModel.reportHealthyStatus()
            }
            R.id.btn_test -> TestActivity.start(this)
        }
    }
}