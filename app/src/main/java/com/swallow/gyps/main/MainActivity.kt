package com.swallow.gyps.main

import android.content.Intent
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
import com.therouter.TheRouter
import com.therouter.router.RouteItem
import com.therouter.router.action.interceptor.ActionInterceptor
import com.therouter.router.interceptor.RouterReplaceInterceptor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(), View.OnClickListener {

    override val viewModel: MainViewModel by viewModels()

    override val bindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    override fun initView(savedInstanceState: Bundle?) {
        initBlueActionBar(binding.includeTitle.toolbar, false, "Gyps")
    }

    override fun initData(savedInstanceState: Bundle?) {
        // BaseActivity 会自动调用 observeViewModel()
        TheRouter.isDebug = true

    }

    private fun test() {
        lifecycleScope.launch(Dispatchers.IO) {
            val flow2 = (1..10).asFlow().onEach { delay(1000) }
            val job: Job = lifecycleScope.launch {
                logd { "-------------------------->lifecycleScope.launch" }
                flow2.flowOn(Dispatchers.IO) // 设定它运行时所使用的调度器
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
            R.id.btn_test -> {
                TheRouter.build("/test/TestActivity").withString("title", "测试页面").navigation()
            }

            binding.btnBrower.id -> {
                openBrowserTestPage()
            }

            else -> {

            }
        }
    }

    private fun openBrowserTestPage() {
        try {
            // 1. 在应用的缓存目录中创建一个临时文件
            //    我们使用 "route_test.html"作为文件名
            val tempFile = File(cacheDir, "route_test.html")

            // 2. 调用 createTestHtml() 获取HTML内容，并将其写入临时文件
            //    use {} 块可以确保文件流在使用后自动关闭
            tempFile.writer().use {
                it.write(createTestHtml())
            }

            // 3. 使用 FileProvider 为创建的临时文件生成一个安全的 Uri
            val uri = androidx.core.content.FileProvider.getUriForFile(
                this,
                "$packageName.fileprovider", // 确保这个 authority 与你的 Manifest 文件中定义的一致
                tempFile
            )

            // 4. 创建 Intent，用于在浏览器或其他能处理HTML的应用中打开
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "text/html")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                // FLAG_ACTIVITY_NEW_TASK 不是必须的，但如果从非 Activity 的 Context 启动，则需要
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            startActivity(intent)
        } catch (e: Exception) {
            // 打印详细的错误日志，方便调试
            e.printStackTrace()
        }
    }

    /**
     * 从 assets 加载并创建测试 HTML 页面
     * 这个方法保持不变，它的逻辑是正确的。
     */
    private fun createTestHtml(): String {
        val scheme = "gyps"
        val host = "test.com"

        // 从 assets 读取 HTML 模板文件
        val template = assets.open("route_test_template.html").bufferedReader().use {
            it.readText()
        }

        // 替换模板中的占位符并返回最终的 HTML 字符串
        return template
            .replace("{scheme}", scheme)
            .replace("{host}", host)
    }

}