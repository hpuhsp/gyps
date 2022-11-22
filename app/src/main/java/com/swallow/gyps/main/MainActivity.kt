package com.swallow.gyps.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.hsp.resource.ext.initBlueActionBar
import com.swallow.fly.base.view.BaseActivity
import com.swallow.fly.ext.logd
import com.swallow.gyps.R
import com.swallow.gyps.databinding.ActivityMainBinding
import com.swallow.gyps.msc.SignUpActivity
import com.swallow.gyps.msc.VoiceInputActivity
import com.swallow.gyps.test.TestActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(), View.OnClickListener {
    override val modelClass: Class<MainViewModel>
        get() = MainViewModel::class.java
    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    override fun initView(savedInstanceState: Bundle?) {
        initBlueActionBar(false, "Gyps")
    }

    override fun initData(savedInstanceState: Bundle?) {
//        lifecycleScope.launch {
//            mViewModel.sharedFlow.collect {
//            }
//        }
//        mViewModel.testShareFlow()
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
        return R.color.toolbar_blue
    }

    override fun showDarkToolBar(): Boolean {
        return false
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_face_identify -> SignUpActivity.start(this)

            R.id.btn_voice_input -> VoiceInputActivity.start(this)

            R.id.btn_report -> {
                mViewModel.reportHealthyStatus()
            }
            R.id.btn_test -> TestActivity.start(this)
        }
    }
}