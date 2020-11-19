package com.hnsh.gyps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hnsh.core.base.view.BaseActivity
import com.hnsh.gyps.databinding.ActivityMainBinding
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