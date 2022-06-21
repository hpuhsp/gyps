package com.swallow.gyps.test

import java.io.Serializable

/**
 *  版本更新 返回信息
 *  @author lfc-LFC
 *  created at 2021/9/23 16:26
 */

data class VersionInfoEntity(
    var apkFileUrl: String, // apk地址
    var checkDateTime: String,
    var checkUserCode: String,
    var checkUserName: String,
    var createDateTime: String, // 创建时间
    var createUserCode: String, // 创建人
    var createUserName: String, // 创建人代码
    var enterpriseId: Long,//
    var fileSize: String, // 文件大小
    var isForceUpdate: String, // 是否强制更新
    var md5Code: String, // md5验证码
    var remark: String, //备注
    var submitDateTime: String,
    var submitUserCode: String,
    var submitUserName: String,
    var systemId: String,
    var updateLog: String, //更新内容
    var uploadDateTime: String, //上传时间
    var versionCode: Int, //版本代码
    var versionId: String, //版本ID
    var versionName: String, //版本名称
    var versionStatus: String, //
    var isShowRedTip: Boolean, // 是否展示红点
    var versionType: String, //备注
    var autoCheck: Boolean // 自动检查更新
) : Serializable