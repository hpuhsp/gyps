package com.swallow.fly.base.ui.delegate

import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

/**
 * 权限请求代理接口
 */
interface PermissionDelegate {
    fun initPermissionLauncher(caller: ActivityResultCaller, callback: (Map<String, Boolean>) -> Unit)
    fun requestPermissions(permissions: Array<String>)
}

/**
 * 权限请求默认实现
 */
class PermissionDelegateImpl : PermissionDelegate {
    private var permissionLauncher: ActivityResultLauncher<Array<String>>? = null

    override fun initPermissionLauncher(
        caller: ActivityResultCaller,
        callback: (Map<String, Boolean>) -> Unit
    ) {
        permissionLauncher = caller.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            callback(permissions)
        }
    }

    override fun requestPermissions(permissions: Array<String>) {
        permissionLauncher?.launch(permissions)
    }
}
