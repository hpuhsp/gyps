package com.swallow.gyps

import android.os.Bundle
import com.hnsh.gyps.databinding.ActivityMainBinding
import com.swallow.fly.base.view.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    override val modelClass: Class<MainViewModel>
        get() = MainViewModel::class.java

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initData(savedInstanceState: Bundle?) {
    }
}