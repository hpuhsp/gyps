package com.swallow.fly.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.swallow.fly.db.bean.AppCacheEntity
import com.swallow.fly.db.dao.AppCacheDao

/**
 * @Description: 全局数据库
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/27 17:48
 * @UpdateRemark:   更新说明：
 */
@Database(
    entities = [AppCacheEntity::class],
    version = 1, exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun getAppCacheDao(): AppCacheDao
}