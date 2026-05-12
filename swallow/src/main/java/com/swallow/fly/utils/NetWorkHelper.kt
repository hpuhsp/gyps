package com.swallow.fly.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.telephony.TelephonyManager

/**
 * @Description: 网络工具类
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/3 10:00
 * @UpdateRemark:   更新说明：使用新的网络 API
 */
class NetWorkHelper {

    companion object {
        private val uri = Uri.parse("content://telephony/carriers")

        /**
         * 判断网络是否为漫游
         *
         * @param context ApplicationContext
         */
        fun isNetworkRoaming(context: Context): Boolean {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            return tm?.isNetworkRoaming ?: false
        }

        /**
         * 判断是否是wifi链接
         *
         * @param context
         * @return
         */
        fun isWifi(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return false
            val network = cm.activeNetwork ?: return false
            val capabilities = cm.getNetworkCapabilities(network) ?: return false
            
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        }

        /**
         * 网络状态检查
         */
        fun isNetworkConnected(context: Context?): Boolean {
            if (context == null) return false
            
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return false
            val network = cm.activeNetwork ?: return false
            val capabilities = cm.getNetworkCapabilities(network) ?: return false
            
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                   capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }
    }
}