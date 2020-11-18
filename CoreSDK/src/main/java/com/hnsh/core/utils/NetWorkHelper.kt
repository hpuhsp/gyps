package com.hnsh.core.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import android.telephony.TelephonyManager

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/3 10:00
 * @UpdateRemark:   更新说明：
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
            val connectivity = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivity == null) {
            } else {
                val info = connectivity.activeNetworkInfo
                if (info != null && info.type == ConnectivityManager.TYPE_MOBILE) {
                    val tm = context.getSystemService(
                        Context.TELEPHONY_SERVICE
                    ) as TelephonyManager
                    if (tm != null && tm.isNetworkRoaming) {
                        return true
                    } else {
                    }
                } else {
                }
            }
            return false
        }

        /**
         * 判断是否是wifi链接
         *
         * @param context
         * @return
         */
        fun isWifi(context: Context): Boolean {
            val cm =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = cm.activeNetworkInfo
            if (networkInfo != null && networkInfo.isConnected) {
                if (networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                    return true
                }
            }
            return false
        }

        // 网络状态
        fun isNetworkConnected(context: Context?): Boolean {
            if (context != null) {
                val mConnectivityManager = context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val mNetworkInfo = mConnectivityManager.activeNetworkInfo
                if (mNetworkInfo != null) {
                    return mNetworkInfo.isAvailable
                }
            }
            return false
        }
    }
}