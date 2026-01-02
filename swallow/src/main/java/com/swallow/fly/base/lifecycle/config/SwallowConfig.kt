package com.swallow.fly.base.lifecycle.config

import com.swallow.fly.http.di.NetworkConfigBuilder
import com.swallow.fly.db.DatabaseConfigBuilder
import com.swallow.fly.image.ImageConfigBuilder
import com.swallow.fly.log.LogConfigBuilder

/**
 * 框架最终配置快照
 */
data class SwallowConfig(
    val network: NetworkConfigBuilder = NetworkConfigBuilder(),
    val database: DatabaseConfigBuilder = DatabaseConfigBuilder(),
    val image: ImageConfigBuilder = ImageConfigBuilder(),
    val log: LogConfigBuilder = LogConfigBuilder()
)
