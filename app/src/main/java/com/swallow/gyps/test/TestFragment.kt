package com.swallow.gyps.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.swallow.fly.base.view.BaseLazyFragment
import com.swallow.gyps.databinding.FragmentTestBinding
import com.swallow.gyps.glide.GlideApp
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
            "https://t7.baidu.com/it/u=3569419905,626536365&fm=193&f=GIF"
        val glideUrl = GlideUrl(url, LazyHeaders.Builder().addHeader("token", "xxxxxxxxxx").build())
        GlideApp.with(mContext).load(url).into(binding.ivTest)
    }
    
    override fun onVisibleToUser() {
    }
    
    override fun onInvisibleToUser() {
    }
}