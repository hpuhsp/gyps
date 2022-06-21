package com.swallow.gyps.test

import java.io.Serializable

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2021/12/30 16:44
 * @UpdateRemark:   更新说明：
 */
data class UpdateInfoModel(
    val versionInfo: VersionInfoEntity? = null,
    var autoCheck: Boolean = true
) : Serializable