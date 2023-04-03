package com.swallow.gyps.msc

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.drawToBitmap
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.hsp.resource.ext.initBlueActionBar
import com.iflytek.cloud.*
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.swallow.fly.base.view.BaseActivity
import com.swallow.fly.ext.logd
import com.swallow.fly.utils.FileUtils
import com.swallow.gyps.R
import com.swallow.gyps.databinding.ActivitySignUpBinding
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
    
    
    override val bindingInflater: (LayoutInflater) -> ActivitySignUpBinding
        get() = ActivitySignUpBinding::inflate
    
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SignUpActivity::class.java))
        }
    }
    
    override fun initView(savedInstanceState: Bundle?) {
        initBlueActionBar(binding.includeTitle.toolbar, true, "用户注册与验证")
        mIdVerifier = IdentityVerifier.createVerifier(this) {
            logd { "------------onInit--------->" }
            ToastUtils.showShort("SDK初始化成功")
        }
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
                111 -> {
                    val selectList = PictureSelector.obtainMultipleResult(data)
                    if (!selectList.isNullOrEmpty()) {
                        binding.ivExample.tag = selectList[0].compressPath
                        Glide.with(this).load(selectList[0].compressPath).into(binding.ivExample)
                    }
                }
                112 -> {
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
    
    /**
     * 打开相册
     */
    private fun openAlbum(requestCode: Int) {
        PictureSelector.create(this)
            .openGallery(PictureMimeType.ofAll())
            .isWithVideoImage(false)
            .isAndroidQTransform(true) // 必须添加，适配Android Q版本
            .theme(com.luck.picture.lib.R.style.picture_default_style)
            .imageEngine(GlideEngine.getInstance())
            .isCamera(true)
            .maxSelectNum(1)
            .isCompress(true)
            .compressSavePath(FileUtils.getCompressPicPath(this)) // 图片压缩路径，不包括视频
            .imageSpanCount(4)
            .isQuickCapture(false)
            .forResult(requestCode)
    }
    
    
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_upload1 -> {
//                PictureSelector.create(this)
//                    .openCamera(PictureMimeType.ofImage())
//                    .isCompress(true)
//                    .compressSavePath(FileUtils.getCompressPicPath(this))
//                    .isCameraAroundState(true)
//                    .imageEngine(GlideEngine.getInstance())
//                    .forResult(PictureConfig.REQUEST_CAMERA)
                openAlbum(111)
            }
            R.id.btn_upload2 -> {
//                PictureSelector.create(this)
//                    .openCamera(PictureMimeType.ofImage())
//                    .isCompress(true)
//                    .compressSavePath(FileUtils.getCompressPicPath(this))
//                    .isCameraAroundState(true)
//                    .imageEngine(GlideEngine.getInstance())
//                    .forResult(PictureConfig.REQUEST_CAMERA)
                openAlbum(112)
            }
            R.id.btn_sign_up -> {
                startSignUp()
            }
            R.id.btn_verify -> {
                startVerify()
            }
            R.id.next_person -> {
                authorId = "id_${System.currentTimeMillis()}"
                binding.ivExample.setImageResource(R.mipmap.boy1)
                binding.ivFace.setImageResource(R.mipmap.boy2)
                binding.tvScore.text = "人脸相似度为：0.00%"
            }
        }
    }
    
    /**
     * 开始验证
     */
    private fun startVerify() {
        showLoading("开始人脸验证...", true)
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
                hideDialog()
                p0?.resultString?.let {
                    val result = JSONObject(it)
                    if (result.has("face_score")) {
                        binding.tvScore.text = "人脸相似度为：${result.getString("face_score")}%"
                    }
                }
                ToastUtils.showShort("人脸验证成功！")
            }
            
            override fun onError(p0: SpeechError?) {
                logd { "------------------onError------->${p0?.errorDescription}" }
                logd { "------------------onError----code--->${p0?.errorCode}" }
                hideDialog()
                ToastUtils.showShort("人脸验证失败！")
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
        if (null == binding.ivExample.tag) {
            ToastUtils.showShort("请先选择图片或者拍照!")
            return
        }
        showLoading("开始人脸注册...", true)
        
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
                hideDialog()
                ToastUtils.showShort("人脸注册成功！")
            }
            
            override fun onError(p0: SpeechError?) {
                logd { "------------------onError------->${p0?.errorDescription}" }
                logd { "------------------onError----code--->${p0?.errorCode}" }
                hideDialog()
                ToastUtils.showShort("人脸注册失败！")
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
        val drawToBitmap = binding.ivExample.drawToBitmap(Bitmap.Config.ARGB_8888)
        val bitmap2Bytes = ImageUtils.bitmap2Bytes(drawToBitmap)
        mIdVerifier.writeData("ifr", params.toString(), bitmap2Bytes, 0, bitmap2Bytes.size)
        // 停止写入
        mIdVerifier.stopWrite("ifr")
    }
    
    override fun onStop() {
        super.onStop()
        
    }
    
}