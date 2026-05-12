package com.swallow.fly.ext

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * 判断当前网络状态
 */
fun Context.networkConnected(): Boolean = applicationContext.networkConnected()

fun Application.networkConnected(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return false
    val nc = cm.getNetworkCapabilities(network) ?: return false
    
    return nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
           nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
           nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
}
