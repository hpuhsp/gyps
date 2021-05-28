package com.swallow.fly.db

import com.google.gson.GsonBuilder
import com.swallow.fly.db.bean.AppCacheEntity
import com.swallow.fly.utils.SingletonHolderSingleArg
import javax.inject.Inject
import javax.inject.Singleton


/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2021/3/19 17:38
 * @UpdateRemark:   更新说明：
 */
@Singleton
class AppDataBaseManager @Inject constructor(val database: AppDataBase) {
    private val repository = database.getAppCacheDao()

    /**
     * 设置缓存
     *
     * @param key
     * @param jsonData
     */
    fun save(cacheId: Int, key: String, jsonData: String) {
        repository.insert(AppCacheEntity(cacheId, key, jsonData, System.currentTimeMillis()))
    }

    fun save(cacheId: Int, key: String, item: Any): Long {
        return repository.insert(
            AppCacheEntity(
                cacheId,
                key,
                toJson(item),
                System.currentTimeMillis()
            )
        )
    }

    fun get(key: String): String {
        return repository.loadCacheSync(key)?.jsonData ?: ""
    }

    fun <T> get(key: String, classOfT: Class<T>): T? {
        val entity = repository.loadCacheSync(key)
        return if (!entity?.jsonData.isNullOrEmpty()) toObject(
            entity?.jsonData!!,
            classOfT
        ) else null
    }

    /**
     * 删除
     *
     * @param key
     */
    fun delete(key: String) {
        val cacheEntity = repository.loadCacheSync(key)
        if (cacheEntity != null) {
            repository.deleteCaches(cacheEntity)
        }
    }

    private val convert = GsonBuilder().disableHtmlEscaping().create()

    private fun toJson(o: Any): String {
        return convert.toJson(o)
    }

    private fun <T> toObject(json: String, classOfT: Class<T>): T {
        return convert.fromJson(json, classOfT)
    }

    fun <T> toList(json: String, clazz: Class<out Array<T>>): MutableList<Array<T>> {
        val array: Array<T> = convert.fromJson(json, clazz)
        return mutableListOf(array)
    }

    companion object :
        SingletonHolderSingleArg<AppDataBaseManager, AppDataBase>(::AppDataBaseManager)
}