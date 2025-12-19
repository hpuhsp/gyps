package com.swallow.fly.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.swallow.fly.db.bean.AppCacheEntity
import kotlinx.coroutines.flow.Flow

/**
 * 应用缓存 DAO - 现代化版本
 * 
 * 使用 Flow 和 suspend 函数替代 LiveData 和同步方法
 * 
 * @Description: 应用缓存数据访问对象（现代化升级）
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2020/8/28 14:38
 * @UpdateRemark: 2024 - 现代化升级：使用 Flow 和 suspend 函数
 */
@Dao
interface AppCacheDao {

    /**
     * 使用 Flow 加载缓存（推荐）
     * 
     * Flow 会自动观察数据库变化并发射新值
     * 
     * @param key 缓存键
     * @return 缓存实体的 Flow
     */
    @Query("SELECT * FROM app_cache WHERE cache_key = :key")
    fun loadCacheFlow(key: String): Flow<AppCacheEntity?>

    /**
     * 使用 suspend 函数加载缓存
     * 
     * 适用于一次性查询
     * 
     * @param key 缓存键
     * @return 缓存实体，如果不存在则返回 null
     */
    @Query("SELECT * FROM app_cache WHERE cache_key = :key")
    suspend fun loadCache(key: String): AppCacheEntity?

    /**
     * 查询所有缓存（Flow）
     * 
     * @return 所有缓存实体的 Flow
     */
    @Query("SELECT * FROM app_cache")
    fun loadAllCachesFlow(): Flow<List<AppCacheEntity>>

    /**
     * 查询所有缓存（suspend）
     * 
     * @return 所有缓存实体列表
     */
    @Query("SELECT * FROM app_cache")
    suspend fun loadAllCaches(): List<AppCacheEntity>

    /**
     * 插入或更新缓存
     * 
     * @param cache 缓存实体
     * @return 插入的行 ID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cache: AppCacheEntity): Long

    /**
     * 批量插入或更新缓存
     * 
     * @param caches 缓存实体列表
     * @return 插入的行 ID 列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg caches: AppCacheEntity): List<Long>

    /**
     * 更新缓存
     * 
     * @param cache 缓存实体
     * @return 更新的行数
     */
    @Update
    suspend fun update(cache: AppCacheEntity): Int

    /**
     * 删除缓存
     * 
     * @param caches 要删除的缓存实体
     * @return 删除的行数
     */
    @Delete
    suspend fun delete(vararg caches: AppCacheEntity): Int

    /**
     * 根据键删除缓存
     * 
     * @param key 缓存键
     * @return 删除的行数
     */
    @Query("DELETE FROM app_cache WHERE cache_key = :key")
    suspend fun deleteByKey(key: String): Int

    /**
     * 清空所有缓存
     * 
     * @return 删除的行数
     */
    @Query("DELETE FROM app_cache")
    suspend fun deleteAll(): Int

    /**
     * 根据时间戳删除过期缓存
     * 
     * @param timestamp 时间戳阈值
     * @return 删除的行数
     */
    @Query("DELETE FROM app_cache WHERE cache_timestamp < :timestamp")
    suspend fun deleteExpired(timestamp: Long): Int

    // ========== 兼容旧代码的方法（已废弃） ==========

    /**
     * 使用 LiveData 加载缓存（已废弃）
     * 
     * @deprecated 使用 loadCacheFlow() 替代
     */
    @Deprecated("使用 loadCacheFlow() 替代", ReplaceWith("loadCacheFlow(key)"))
    @Query("SELECT * FROM app_cache WHERE cache_key = :key")
    fun loadCacheLiveData(key: String): LiveData<AppCacheEntity>?

    /**
     * 同步加载缓存（已废弃）
     * 
     * @deprecated 使用 suspend loadCache() 替代
     */
    @Deprecated("使用 suspend loadCache() 替代", ReplaceWith("loadCache(key)"))
    @Query("SELECT * FROM app_cache WHERE cache_key = :key")
    fun loadCacheSync(key: String): AppCacheEntity?

    /**
     * 同步插入（已废弃）
     * 
     * @deprecated 使用 suspend insert() 替代
     */
    @Deprecated("使用 suspend insert() 替代", ReplaceWith("insert(itemEntry)"))
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSync(itemEntry: AppCacheEntity): Long

    /**
     * 同步删除（已废弃）
     * 
     * @deprecated 使用 suspend delete() 替代
     */
    @Deprecated("使用 suspend delete() 替代", ReplaceWith("delete(*cacheEntities)"))
    @Delete
    fun deleteCaches(vararg cacheEntities: AppCacheEntity)
}
