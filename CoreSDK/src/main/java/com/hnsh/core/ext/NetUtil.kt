package com.hnsh.core.ext

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

/**
 * 判断当前网络状态
 */
fun Context.networkConnected():Boolean=applicationContext.networkConnected()

fun Application.networkConnected(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        val mWiFiNetworkInfo = cm.activeNetworkInfo
        if (mWiFiNetworkInfo != null) {
            if (mWiFiNetworkInfo.type == ConnectivityManager.TYPE_WIFI) { //WIFI
                return true
            } else if (mWiFiNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) { //移动数据
                return true
            }
        }
    } else {
        val network = cm.activeNetwork
        if (network != null) {
            val nc = cm.getNetworkCapabilities(network)
            if (nc != null) {
                if (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) { //WIFI
                    return true
                } else if (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) { //移动数据
                    return true
                }
            }
        }
    }
    return false
}
