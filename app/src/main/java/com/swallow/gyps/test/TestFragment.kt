package com.swallow.gyps.test

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.swallow.fly.base.view.BaseFragment
import com.swallow.fly.base.view.BaseLazyFragment
import com.swallow.gyps.R
import com.swallow.gyps.databinding.FragmentTestBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * @Description:
 * @Author: Hsp
 * @Email:  1101121039@qq.com
 * @CreateTime: 2021/4/22 14:19
 * @UpdateRemark:
 */
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

    }

    override fun onVisibleToUser() {
    }

    override fun onInvisibleToUser() {
    }
}