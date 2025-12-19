package com.swallow.fly.db

import android.content.Context
import androidx.room.Room
import com.swallow.fly.db.dao.AppCacheDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 数据库模块 - 现代化版本
 * 
 * 使用 Kotlin DSL 提供 Room 数据库配置
 * 
 * @Description: 数据库依赖注入模块（现代化升级）
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2020/8/27 17:25
 * @UpdateRemark: 2024 - 现代化升级：使用 Kotlin DSL，添加迁移策略，优化配置
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * 数据库名称
     */
    private const val DATABASE_NAME = "swallow_app.db"
    
    /**
     * 提供 AppDataBase 实例
     * 
     * 配置包括：
     * - 数据库名称
     * - 破坏性迁移策略（开发阶段）
     * - 禁止主线程查询（生产环境最佳实践）
     * 
     * @param context 应用上下文
     * @return AppDataBase 实例
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDataBase {
        return Room.databaseBuilder(
            context,
            AppDataBase::class.java,
            DATABASE_NAME
        ).apply {
            // 开发阶段使用破坏性迁移，生产环境应该提供迁移策略
            fallbackToDestructiveMigration()
            
            // 生产环境最佳实践：禁止主线程查询
            // 如果需要在主线程查询，请使用 .allowMainThreadQueries()
            // 但不推荐，应该使用协程或 Flow
            
            // 添加数据库回调（可选）
            // addCallback(object : RoomDatabase.Callback() {
            //     override fun onCreate(db: SupportSQLiteDatabase) {
            //         super.onCreate(db)
            //         // 数据库创建时的初始化操作
            //     }
            // })
        }.build()
    }
    
    /**
     * 提供 AppCacheDao 实例
     * 
     * @param database AppDataBase 实例
     * @return AppCacheDao 实例
     */
    @Provides
    @Singleton
    fun provideAppCacheDao(database: AppDataBase): AppCacheDao {
        return database.getAppCacheDao()
    }
    
    /**
     * 提供 AppDataBaseManager 实例
     * 
     * @param database AppDataBase 实例
     * @return AppDataBaseManager 实例
     */
    @Provides
    @Singleton
    fun provideAppDataBaseManager(database: AppDataBase): AppDataBaseManager {
        return AppDataBaseManager.getInstance(database)
    }
}
