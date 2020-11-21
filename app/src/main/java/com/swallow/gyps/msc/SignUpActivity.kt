package com.swallow.gyps.msc

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.core.view.drawToBitmap
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.NetworkUtils
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.hnsh.gyps.R
import com.hnsh.gyps.databinding.ActivitySignUpBinding
import com.hsp.resource.ext.initBlueActionBar
import com.iflytek.cloud.*
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.swallow.fly.base.view.BaseActivity
import com.swallow.fly.ext.logd
import com.swallow.fly.utils.FileUtils
import com.swallow.gyps.glide.GlideEngine
import com.swallow.gyps.msc.viewmodel.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject


/**
 * @Description: 注册页面
 * @Author: Hsp
 * @Email:  1101121039@qq.com
 * @CreateTime: 2020/11/21 9:54
 * @UpdateRemark:
 */
@AndroidEntryPoint
class SignUpActivity : BaseActivity<SignUpViewModel, ActivitySignUpBinding>(),
    View.OnClickListener {
    private lateinit var mIdVerifier: IdentityVerifier

    private lateinit var authorId: String

    override val modelClass: Class<SignUpViewModel>
        get() = SignUpViewModel::class.java

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SignUpActivity::class.java))
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        initBlueActionBar(true, "用户注册与验证")
        mIdVerifier = IdentityVerifier.createVerifier(this, object : InitListener {
            override fun onInit(p0: Int) {
                logd { "------------onInit--------->" }
            }
        })
        authorId = "id_${System.currentTimeMillis()}"
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun getStatusBarColor(): Int {
        return R.color.toolbar_blue
    }

    override fun showDarkToolBar(): Boolean {
        return false
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PictureConfig.REQUEST_CAMERA -> {
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    if (!selectList.isNullOrEmpty()) {
                        binding.ivFace.tag = selectList[0].compressPath
                        Glide.with(this).load(selectList[0].compressPath).into(binding.ivFace)
                    }
                }
                else -> {
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_capture -> {
                PictureSelector.create(this)
                    .openCamera(PictureMimeType.ofImage())
                    .isCompress(true)
                    .compressSavePath(FileUtils.getCompressPicPath(this))
                    .isCameraAroundState(true)
                    .imageEngine(GlideEngine.getInstance())
                    .forResult(PictureConfig.REQUEST_CAMERA)
            }
            R.id.btn_sign_up -> {
                startSignUp()
            }
            R.id.btn_verify -> {
                startVerify()
            }
            R.id.next_person -> {
                authorId = "id_${System.currentTimeMillis()}"
                binding.ivFace.setImageDrawable(null)
            }
        }
    }

    /**
     * 开始验证
     */
    private fun startVerify() {
        // 清空参数
        mIdVerifier.setParameter(SpeechConstant.PARAMS, null)
        // 设置会话场景
        mIdVerifier.setParameter(SpeechConstant.MFV_SCENES, "ifr")
        // 设置会话类型
        mIdVerifier.setParameter(SpeechConstant.MFV_SST, "verify")
        // 设置验证模式，单一验证模式：sin
        mIdVerifier.setParameter(SpeechConstant.MFV_VCM, "sin")
        // 用户id
        mIdVerifier.setParameter(SpeechConstant.AUTH_ID, authorId)
        // 注册监听器（IdentityListener）mVerifyListener，开始会话。
        mIdVerifier.startWorking(object : IdentityListener {
            override fun onResult(p0: IdentityResult?, p1: Boolean) {
                logd { "--------------onResult---------->${p0?.resultString}" }
                p0?.resultString?.let {
                    val result = JSONObject(it)
                    if (result.has("face_score")) {
                        binding.tvScore.text = "人脸相似度为：${result.getString("face_score")}%"
                    }
                }
            }

            override fun onError(p0: SpeechError?) {
                logd { "------------------onError------->${p0?.errorDescription}" }
                logd { "------------------onError----code--->${p0?.errorCode}" }

            }

            override fun onEvent(p0: Int, p1: Int, p2: Int, p3: Bundle?) {
                logd { "------------------p0------->${p0}" }
                logd { "------------------p1------->${p1}" }
                logd { "------------------p2------->${p2}" }
                if (null != p3) {
                    logd { "-------------------p3------>${Gson().toJson(p3)}" }
                }
            }
        })
        // 子业务执行参数，若无可以传空字符传
        val params = StringBuffer()
        // 写入数据，mImageData为图片的二进制数据
        val drawToBitmap = binding.ivFace.drawToBitmap(Bitmap.Config.ARGB_8888)
        val bitmap2Bytes = ImageUtils.bitmap2Bytes(drawToBitmap)
        mIdVerifier.writeData("ifr", params.toString(), bitmap2Bytes, 0, bitmap2Bytes.size)
        // 停止写入
        mIdVerifier.stopWrite("ifr")
    }

    /**
     * 开始注册
     */
    private fun startSignUp() {
        logd { "------------------startSignUp------->${NetworkUtils.isConnected()}" }
        if (null == binding.ivFace.tag) {
            return
        }
        mIdVerifier.setParameter(SpeechConstant.PARAMS, null)
        // 设置会话场景
        mIdVerifier.setParameter(SpeechConstant.MFV_SCENES, "ifr")
        // 设置会话类型
        mIdVerifier.setParameter(SpeechConstant.MFV_SST, "enroll")
        // 设置用户id
        mIdVerifier.setParameter(SpeechConstant.AUTH_ID, authorId)
        // 注册监听器（IdentityListener）mEnrollListener，开始会话。
        // 并通过mEnrollListener中onResult回调中获得json格式结果
        mIdVerifier.startWorking(object : IdentityListener {
            override fun onResult(p0: IdentityResult?, p1: Boolean) {
                logd { "--------------onResult---------->${p0?.resultString}" }
            }

            override fun onError(p0: SpeechError?) {
                logd { "------------------onError------->${p0?.errorDescription}" }
                logd { "------------------onError----code--->${p0?.errorCode}" }
            }

            override fun onEvent(p0: Int, p1: Int, p2: Int, p3: Bundle?) {
                logd { "------------------p0------->${p0}" }
                logd { "------------------p1------->${p1}" }
                logd { "------------------p2------->${p2}" }
                if (null != p3) {
                    logd { "-------------------p3------>${Gson().toJson(p3)}" }
                }
            }
        })
        // 子业务执行参数，若无可以传空字符传
        val params = StringBuffer()
        // 写入数据，mImageData为图片的二进制数据
        val drawToBitmap = binding.ivFace.drawToBitmap(Bitmap.Config.ARGB_8888)
        val bitmap2Bytes = ImageUtils.bitmap2Bytes(drawToBitmap)
        mIdVerifier.writeData("ifr", params.toString(), bitmap2Bytes, 0, bitmap2Bytes.size)
        // 停止写入
        mIdVerifier.stopWrite("ifr")
    }

    override fun onStop() {
        super.onStop()

    }
}