package com.swallow.gyps.test

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.hsp.resource.ext.initActionBar
import com.swallow.fly.base.view.BaseActivity
import com.swallow.gyps.R
import com.swallow.gyps.databinding.ActivityTestBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestActivity : BaseActivity<TestViewModel, ActivityTestBinding>() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, TestActivity::class.java))
        }
    }

    override val modelClass: Class<TestViewModel>
        get() = TestViewModel::class.java
    override val bindingInflater: (LayoutInflater) -> ActivityTestBinding
        get() = ActivityTestBinding::inflate

    override fun initView(savedInstanceState: Bundle?) {
        initActionBar(true, "测试页面")
        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction
            .replace(R.id.content_view, TestFragment.newInstance("1", "2"))
        beginTransaction.commitAllowingStateLoss()
    }

    override fun initData(savedInstanceState: Bundle?) {
    }
}