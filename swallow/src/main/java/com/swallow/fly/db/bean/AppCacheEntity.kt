package com.swallow.fly.db.bean

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * @Description: App全局缓存数据库表
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/28 14:39
 * @UpdateRemark:   更新说明：
 */
@Entity(tableName = "app_cache")
data class AppCacheEntity(
    @NonNull
    @ColumnInfo
    val cacheId: Int,
    @PrimaryKey
    @ColumnInfo(name = "cache_key")
    val key: String,
    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    val jsonData: String,
    @ColumnInfo
    val createTime: Long
) : Serializable