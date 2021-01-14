package com.swallow.gyps.msc

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.NetworkUtils
import com.hsp.resource.ext.initBlueActionBar
import com.iflytek.cloud.*
import com.iflytek.cloud.ui.RecognizerDialog
import com.iflytek.cloud.ui.RecognizerDialogListener
import com.iflytek.cloud.util.VolumeUtil
import com.swallow.fly.base.view.BaseActivity
import com.swallow.fly.ext.logd
import com.swallow.fly.ext.txt
import com.swallow.fly.utils.FastUtils
import com.swallow.fly.utils.NetWorkHelper
import com.swallow.gyps.R
import com.swallow.gyps.databinding.ActivityVoiceInputBinding
import com.swallow.gyps.msc.viewmodel.VoiceInputViewModel
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.EasyPermissions


/**
 * @Description:  语音输入测试
 * @Author: Hsp
 * @Email:  1101121039@qq.com
 * @CreateTime: 2020/11/21 15:33
 * @UpdateRemark:
 */
@AndroidEntryPoint
class VoiceInputActivity : BaseActivity<VoiceInputViewModel, ActivityVoiceInputBinding>(),
    View.OnClickListener, EasyPermissions.PermissionCallbacks {
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, VoiceInputActivity::class.java))
        }
    }

    override val modelClass: Class<VoiceInputViewModel>
        get() = VoiceInputViewModel::class.java

    private lateinit var mIatDialog: RecognizerDialog

    override fun initView(savedInstanceState: Bundle?) {
        initBlueActionBar(true, "语音听写")
        checkCameraPermission()
    }

    private fun initNoneUiConfig() {
        //初始化识别无UI识别对象
        //使用SpeechRecognizer对象，可根据回调消息自定义界面
        val mIat = SpeechRecognizer.createRecognizer(this, object : InitListener {
            override fun onInit(p0: Int) {
                logd { "-------------------初始化成功------------>" }
            }
        });

        //设置语法ID和 SUBJECT 为空，以免因之前有语法调用而设置了此参数；
        // 或直接清空所有参数，具体可参考 DEMO 的示例。
        mIat.setParameter(SpeechConstant.CLOUD_GRAMMAR, null)
        mIat.setParameter(SpeechConstant.SUBJECT, null)
        //设置返回结果格式，目前支持json,xml以及plain 三种格式，其中plain为纯听写文本内容
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json")
        //此处engineType为“cloud”
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, "cloud")
        //设置语音输入语言，zh_cn为简体中文
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn")
        //设置结果返回语言
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin")
        // 设置语音前端点:静音超时时间，单位ms，即用户多长时间不说话则当做超时处理
        //取值范围{1000～10000}
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000")
        //设置语音后端点:后端点静音检测时间，单位ms，即用户停止说话多长时间内即认为不再输入，
        //自动停止录音，范围{0~10000}
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000")
        //设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "1")

        //开始识别，并设置监听器
        mIat.startListening(object : RecognizerListener {
            override fun onVolumeChanged(p0: Int, p1: ByteArray?) {
                val computeVolume = VolumeUtil.computeVolume(p1, p0)
                logd { "----------------当前音量---------->${computeVolume}" }
            }

            override fun onBeginOfSpeech() {
                logd { "------------开始听写------->" }
            }

            override fun onEndOfSpeech() {
                logd { "------------结束听写------->" }
            }

            override fun onResult(p0: RecognizerResult?, p1: Boolean) {
                logd { "-----------------------onResult----------->${p0?.resultString}" }
            }

            override fun onError(p0: SpeechError?) {
            }

            override fun onEvent(p0: Int, p1: Int, p2: Int, p3: Bundle?) {
            }
        })
    }

    private fun checkCameraPermission() {
        if (EasyPermissions.hasPermissions(this, *RECORD_PERMISSIONS)) {
            initInputUI()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "应用需要获取录音权限!",
                1081,
                *RECORD_PERMISSIONS
            )
        }
    }

    private fun initInputUI() {
        mIatDialog = RecognizerDialog(this, object : InitListener {
            override fun onInit(p0: Int) {
                logd { "-----------初始化UI组件----------->" }
            }
        })
        if (null == mIatDialog) {
            showToast("初始化SDK失败~")
            return
        }
        mIatDialog.setParameter(SpeechConstant.PARAMS, null)
        //设置语法ID和 SUBJECT 为空，以免因之前有语法调用而设置了此参数；或直接清空所有参数，具体可参考 DEMO 的示例。
        mIatDialog.setParameter(SpeechConstant.CLOUD_GRAMMAR, null)
        mIatDialog.setParameter(SpeechConstant.SUBJECT, null)
        //设置返回结果格式，目前支持json,xml以及plain 三种格式，其中plain为纯听写文本内容
//        mIatDialog.setParameter(SpeechConstant.RESULT_TYPE, "json")
        mIatDialog.setParameter(SpeechConstant.RESULT_TYPE, "plain")
        // 设置离线（local）、在线(cloud) 语音听写
        mIatDialog.setParameter(SpeechConstant.ENGINE_MODE, SpeechConstant.MODE_AUTO)
//        //设置语音输入语言，zh_cn为简体中文
//        mIatDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn")
//        //设置结果返回语言
//        mIatDialog.setParameter(SpeechConstant.ACCENT, "mandarin")
//        // 设置语音前端点:静音超时时间，单位ms，即用户多长时间不说话则当做超时处理
//        //取值范围{1000～10000}
//        mIatDialog.setParameter(SpeechConstant.VAD_BOS, "4000")
//        //设置语音后端点:后端点静音检测时间，单位ms，即用户停止说话多长时间内即认为不再输入，
//        //自动停止录音，范围{0~10000}
//        mIatDialog.setParameter(SpeechConstant.VAD_EOS, "1000")
//        //设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
//        mIatDialog.setParameter(SpeechConstant.ASR_PTT, "1")

        //开始识别，并设置监听器
        mIatDialog.setListener(object : RecognizerDialogListener {
            @SuppressLint("SetTextI18n")
            override fun onResult(p0: RecognizerResult?, p1: Boolean) {
                logd { "----------------onResult-------->${p0?.resultString}" }
                binding.etContent.setText("${binding.etContent.txt() ?: ""}${p0?.resultString ?: ""}")
            }

            override fun onError(p0: SpeechError?) {
                logd { "-------------errorCode------>${p0?.errorCode}" }
                logd { "-------------errorDescription------>${p0?.errorDescription}" }
            }
        })
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_input -> {
                startVoiceInput()
            }
            R.id.btn_clear -> binding.etContent.setText("")
        }
    }

    /**
     * 开始听写
     */
    private fun startVoiceInput() {
        if (!mIatDialog.isShowing) {
            val engineType = if (NetWorkHelper.isNetworkConnected(this)) "cloud" else "local"
            mIatDialog.setParameter(SpeechConstant.ENGINE_TYPE, engineType)
            mIatDialog.show()
        }
    }


    override fun getStatusBarColor(): Int {
        return R.color.toolbar_blue
    }

    override fun showDarkToolBar(): Boolean {
        return false
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        initInputUI()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        FastUtils.makeText(this, "应用没有录音权限，请在手机设置中授予！")
    }

    override val bindingInflater: (LayoutInflater) -> ActivityVoiceInputBinding
        get() = ActivityVoiceInputBinding::inflate
}