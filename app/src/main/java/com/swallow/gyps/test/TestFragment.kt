package com.swallow.gyps.test

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.swallow.fly.base.view.BaseFragment
import com.swallow.fly.base.view.BaseLazyFragment
import com.swallow.fly.image.GlideApp
import com.swallow.gyps.R
import com.swallow.gyps.databinding.FragmentTestBinding
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * @Description:
 * @Author: Hsp
 * @Email:  1101121039@qq.com
 * @CreateTime: 2021/4/22 14:19
 * @UpdateRemark:
 */
@AndroidEntryPoint
class TestFragment : BaseLazyFragment<TestFViewModel, FragmentTestBinding>() {
    private var param1: String? = null
    private var param2: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TestFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    
    override val modelClass: Class<TestFViewModel>?
        get() = TestFViewModel::class.java
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTestBinding
        get() = FragmentTestBinding::inflate
    
    override fun initView() {
    }
    
    override fun onFirstVisibleToUser() {
//        mViewModel?.checkAppVersion(false)
        val url =
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2Fedpic%2Fcd%2F05%2Fdc%2Fcd05dc90a63739a47d7b301a02205b7e.jpg&refer=http%3A%2F%2Fup.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1658393333&t=29ab5e4d07c8752242b78a0d9c7378ed"
        val glideUrl = GlideUrl(url, LazyHeaders.Builder().addHeader("token", "haahahhaha").build())
        GlideApp.with(mContext).load(url).into(binding.ivTest)
    }
    
    override fun onVisibleToUser() {
    }
    
    override fun onInvisibleToUser() {
    }
}