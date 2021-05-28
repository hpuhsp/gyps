package com.swallow.fly.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.swallow.fly.db.bean.AppCacheEntity


/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/28 14:38
 * @UpdateRemark:   更新说明：
 */
@Dao
interface AppCacheDao {

    @Query("SELECT * FROM app_cache WHERE cache_key = :key")
    fun loadCache(key: String): LiveData<AppCacheEntity>?

    @Query("SELECT * FROM app_cache WHERE cache_key = :key")
    fun loadCacheSync(key: String): AppCacheEntity?

    /**
     * 插入操作
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(itemEntry: AppCacheEntity): Long

    /**
     * 删除操作
     */
    @Delete
    fun deleteCaches(vararg cacheEntities: AppCacheEntity?)
}
