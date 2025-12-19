package com.swallow.fly.db

import com.google.gson.GsonBuilder
import com.swallow.fly.db.bean.AppCacheEntity
import com.swallow.fly.utils.SingletonHolderSingleArg
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 应用数据库管理器 - 现代化版本
 * 
 * 提供便捷的数据库操作方法，支持协程和 Flow
 * 
 * @Description: 应用数据库管理器（现代化升级）
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2021/3/19 17:38
 * @UpdateRemark: 2024 - 现代化升级：使用 suspend 函数和 Flow
 */
@Singleton
class AppDataBaseManager @Inject constructor(val database: AppDataBase) {
    private val cacheDao = database.getAppCacheDao()
    private val gson = GsonBuilder().disableHtmlEscaping().create()

    // ========== 现代化方法（使用 suspend 和 Flow） ==========

    /**
     * 保存缓存（suspend）
     * 
     * @param key 缓存键
     * @param jsonData JSON 数据
     * @param expireTimeMillis 过期时间（毫秒），默认为 0（永不过期）
     * @return 插入的行 ID
     */
    suspend fun saveCache(
        key: String,
        jsonData: String,
        expireTimeMillis: Long = 0L
    ): Long {
        val entity = AppCacheEntity.create(key, jsonData, expireTimeMillis)
        return cacheDao.insert(entity)
    }

    /**
     * 保存对象为缓存（suspend）
     * 
     * @param key 缓存键
     * @param item 要缓存的对象
     * @param expireTimeMillis 过期时间（毫秒），默认为 0（永不过期）
     * @return 插入的行 ID
     */
    suspend fun saveObject(
        key: String,
        item: Any,
        expireTimeMillis: Long = 0L
    ): Long {
        val jsonData = toJson(item)
        return saveCache(key, jsonData, expireTimeMillis)
    }

    /**
     * 获取缓存（suspend）
     * 
     * @param key 缓存键
     * @return JSON 字符串，如果不存在则返回空字符串
     */
    suspend fun getCache(key: String): String {
        val entity = cacheDao.loadCache(key)
        return if (entity != null && !entity.isExpired()) {
            entity.jsonData
        } else {
            if (entity?.isExpired() == true) {
                cacheDao.deleteByKey(key)
            }
            ""
        }
    }

    /**
     * 获取缓存对象（suspend）
     * 
     * @param key 缓存键
     * @param classOfT 对象类型
     * @return 缓存对象，如果不存在或已过期则返回 null
     */
    suspend fun <T> getCacheObject(key: String, classOfT: Class<T>): T? {
        val entity = cacheDao.loadCache(key)
        return if (entity != null && !entity.isExpired() && entity.jsonData.isNotEmpty()) {
            toObject(entity.jsonData, classOfT)
        } else {
            if (entity?.isExpired() == true) {
                cacheDao.deleteByKey(key)
            }
            null
        }
    }

    /**
     * 使用 Flow 观察缓存
     * 
     * @param key 缓存键
     * @return 缓存数据的 Flow
     */
    fun getCacheFlow(key: String): Flow<String> {
        return cacheDao.loadCacheFlow(key).map { entity ->
            if (entity != null && !entity.isExpired()) {
                entity.jsonData
            } else {
                ""
            }
        }
    }

    /**
     * 使用 Flow 观察缓存对象
     * 
     * @param key 缓存键
     * @param classOfT 对象类型
     * @return 缓存对象的 Flow
     */
    fun <T> getCacheObjectFlow(key: String, classOfT: Class<T>): Flow<T?> {
        return cacheDao.loadCacheFlow(key).map { entity ->
            if (entity != null && !entity.isExpired() && entity.jsonData.isNotEmpty()) {
                toObject(entity.jsonData, classOfT)
            } else {
                null
            }
        }
    }

    /**
     * 删除缓存（suspend）
     * 
     * @param key 缓存键
     * @return 删除的行数
     */
    suspend fun deleteCache(key: String): Int {
        return cacheDao.deleteByKey(key)
    }

    /**
     * 清空所有缓存（suspend）
     * 
     * @return 删除的行数
     */
    suspend fun clearAllCache(): Int {
        return cacheDao.deleteAll()
    }

    /**
     * 清理过期缓存（suspend）
     * 
     * @return 删除的行数
     */
    suspend fun clearExpiredCache(): Int {
        return cacheDao.deleteExpired(System.currentTimeMillis())
    }

    // ========== JSON 转换方法 ==========

    private fun toJson(obj: Any): String {
        return gson.toJson(obj)
    }

    private fun <T> toObject(json: String, classOfT: Class<T>): T {
        return gson.fromJson(json, classOfT)
    }

    fun <T> toList(json: String, clazz: Class<out Array<T>>): MutableList<Array<T>> {
        val array: Array<T> = gson.fromJson(json, clazz)
        return mutableListOf(array)
    }

    // ========== 兼容旧代码的同步方法（已废弃） ==========

    /**
     * 保存缓存（同步，已废弃）
     * 
     * @deprecated 使用 suspend saveCache() 替代
     */
    @Deprecated("使用 suspend saveCache() 替代", ReplaceWith("saveCache(key, jsonData)"))
    fun save(cacheId: Int, key: String, jsonData: String) {
        val entity = AppCacheEntity(
            key = key,
            jsonData = jsonData,
            timestamp = System.currentTimeMillis(),
            cacheId = cacheId
        )
        cacheDao.insertSync(entity)
    }

    /**
     * 保存对象（同步，已废弃）
     * 
     * @deprecated 使用 suspend saveObject() 替代
     */
    @Deprecated("使用 suspend saveObject() 替代", ReplaceWith("saveObject(key, item)"))
    fun save(cacheId: Int, key: String, item: Any): Long {
        val entity = AppCacheEntity(
            key = key,
            jsonData = toJson(item),
            timestamp = System.currentTimeMillis(),
            cacheId = cacheId
        )
        return cacheDao.insertSync(entity)
    }

    /**
     * 获取缓存（同步，已废弃）
     * 
     * @deprecated 使用 suspend getCache() 替代
     */
    @Deprecated("使用 suspend getCache() 替代", ReplaceWith("getCache(key)"))
    fun get(key: String): String {
        return cacheDao.loadCacheSync(key)?.jsonData ?: ""
    }

    /**
     * 获取缓存对象（同步，已废弃）
     * 
     * @deprecated 使用 suspend getCacheObject() 替代
     */
    @Deprecated("使用 suspend getCacheObject() 替代", ReplaceWith("getCacheObject(key, classOfT)"))
    fun <T> get(key: String, classOfT: Class<T>): T? {
        val entity = cacheDao.loadCacheSync(key)
        return if (!entity?.jsonData.isNullOrEmpty()) {
            toObject(entity?.jsonData!!, classOfT)
        } else {
            null
        }
    }

    /**
     * 删除缓存（同步，已废弃）
     * 
     * @deprecated 使用 suspend deleteCache() 替代
     */
    @Deprecated("使用 suspend deleteCache() 替代", ReplaceWith("deleteCache(key)"))
    fun delete(key: String) {
        val cacheEntity = cacheDao.loadCacheSync(key)
        if (cacheEntity != null) {
            cacheDao.deleteCaches(cacheEntity)
        }
    }

    companion object :
        SingletonHolderSingleArg<AppDataBaseManager, AppDataBase>(::AppDataBaseManager)
}