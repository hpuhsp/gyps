package com.swallow.fly.http

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2022/2/14 17:10
 * @UpdateRemark:   更新说明：
 */
import java.util.concurrent.TimeUnit

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Timeout(
    val value: Long,
    val unit: TimeUnit
)