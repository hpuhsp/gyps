package com.swallow.gyps.test

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.hsp.resource.ext.initActionBar
import com.swallow.fly.base.ui.activity.BaseActivity
import com.swallow.fly.ext.logd
import com.swallow.gyps.R
import com.swallow.gyps.databinding.ActivityTestBinding
import com.therouter.router.Autowired
import com.therouter.router.Route
import dagger.hilt.android.AndroidEntryPoint

@Route(path = "/test/TestActivity")
@AndroidEntryPoint
class TestActivity : BaseActivity<TestViewModel, ActivityTestBinding>() {

    @Autowired(name = "title")
    var mTitle: String? = null

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, TestActivity::class.java))
        }
    }

    // 使用 by viewModels() 委托获取 ViewModel
    override val viewModel: TestViewModel by viewModels()

    override val bindingInflater: (LayoutInflater) -> ActivityTestBinding
        get() = ActivityTestBinding::inflate

    override fun initView(savedInstanceState: Bundle?) {
        initActionBar(binding.includeTitle.toolbar, true, "测试页面")
        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction
            .replace(R.id.content_view, TestFragment.newInstance("1", "2"))
        beginTransaction.commitAllowingStateLoss()
    }

    override fun initData(savedInstanceState: Bundle?) {
        val bundle = intent.getBundleExtra("data")

        if (bundle != null) {
            mTitle = bundle.getString("title")
        }
        logd { "------2222-->${mTitle}" }


    }

    override fun shouldInject(): Boolean {
        return true
    }
}