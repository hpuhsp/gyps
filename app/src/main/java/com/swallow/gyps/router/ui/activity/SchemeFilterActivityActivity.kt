package com.swallow.gyps.router.ui.activity

import android.app.Activity
import android.os.Bundle
import com.therouter.TheRouter

/**
 * @Description：
 * @Author：Hsp
 * @Email：1101121039@qq.com
 * @CreateTime：2026/1/16 16:22
 * @Update：
 **/
class SchemeFilterActivityActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = intent.data
        if (null != uri) {
            val path = uri.path
            val bundle = Bundle(uri.queryParameterNames.size)
            for (key in uri.queryParameterNames) {
                uri.getQueryParameter(key)?.let { value ->
                    bundle.putString(key, value)
                }
            }
            TheRouter.build(path).withBundle("data", bundle).navigation()
        }

        finish()
    }
}