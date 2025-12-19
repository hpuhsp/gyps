package com.swallow.fly.db.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * 应用缓存实体 - 现代化版本
 * 
 * 用于存储应用的全局缓存数据
 * 
 * @Description: App 全局缓存数据库表（现代化升级）
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2020/8/28 14:39
 * @UpdateRemark: 2024 - 现代化升级：优化字段定义，添加时间戳字段
 */
@Entity(tableName = "app_cache")
data class AppCacheEntity(
    /**
     * 缓存键（主键）
     */
    @PrimaryKey
    @ColumnInfo(name = "cache_key")
    val key: String,
    
    /**
     * 缓存数据（JSON 格式）
     */
    @ColumnInfo(name = "cache_data", typeAffinity = ColumnInfo.TEXT)
    val jsonData: String,
    
    /**
     * 创建时间戳（毫秒）
     */
    @ColumnInfo(name = "cache_timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    
    /**
     * 过期时间（毫秒），0 表示永不过期
     */
    @ColumnInfo(name = "cache_expire_time")
    val expireTime: Long = 0L,
    
    /**
     * 缓存 ID（可选，用于排序或其他用途）
     */
    @ColumnInfo(name = "cache_id")
    val cacheId: Int = 0
) : Serializable {
    
    /**
     * 判断缓存是否过期
     * 
     * @return true 表示已过期
     */
    fun isExpired(): Boolean {
        if (expireTime == 0L) return false
        return System.currentTimeMillis() > expireTime
    }
    
    companion object {
        /**
         * 创建缓存实体
         * 
         * @param key 缓存键
         * @param jsonData JSON 数据
         * @param expireTimeMillis 过期时间（毫秒），默认为 0（永不过期）
         * @return 缓存实体
         */
        fun create(
            key: String,
            jsonData: String,
            expireTimeMillis: Long = 0L
        ): AppCacheEntity {
            return AppCacheEntity(
                key = key,
                jsonData = jsonData,
                timestamp = System.currentTimeMillis(),
                expireTime = if (expireTimeMillis > 0) {
                    System.currentTimeMillis() + expireTimeMillis
                } else {
                    0L
                }
            )
        }
    }
}