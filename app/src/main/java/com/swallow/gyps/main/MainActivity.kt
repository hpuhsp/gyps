package com.swallow.gyps.main

import android.os.Bundle
import android.view.View
import com.hsp.resource.ext.initBlueActionBar
import com.swallow.fly.base.view.BaseActivity
import com.swallow.gyps.R
import com.swallow.gyps.databinding.ActivityMainBinding
import com.swallow.gyps.msc.SignUpActivity
import com.swallow.gyps.msc.VoiceInputActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(), View.OnClickListener {
    override val modelClass: Class<MainViewModel>
        get() = MainViewModel::class.java

    override fun initView(savedInstanceState: Bundle?) {
        initBlueActionBar(false, "Gyps")
    }

    override fun initData(savedInstanceState: Bundle?) {
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
        }
    }
}