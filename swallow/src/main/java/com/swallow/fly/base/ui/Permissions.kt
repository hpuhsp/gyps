package com.swallow.fly.base.ui

import android.Manifest

/**
 * 常用权限常量
 */
object Permissions {
    // 读写
    val STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    // 相机访问
    val CAMERA = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    // 通讯录
    val CONTACTS = arrayOf(
        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.GET_ACCOUNTS,
        Manifest.permission.READ_CONTACTS
    )

    // 拨号
    val PHONE_CALL = arrayOf(
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.WRITE_CALL_LOG,
        Manifest.permission.USE_SIP,
        Manifest.permission.PROCESS_OUTGOING_CALLS,
        Manifest.permission.ADD_VOICEMAIL
    )

    // 日历
    val CALENDAR = arrayOf(
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.WRITE_CALENDAR
    )

    // 录音
    val RECORD = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO
    )

    // 短信收发
    val SMS = arrayOf(
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_WAP_PUSH,
        Manifest.permission.RECEIVE_MMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.SEND_SMS
    )

    // 位置
    val GPS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_WIFI_STATE
    )
}
