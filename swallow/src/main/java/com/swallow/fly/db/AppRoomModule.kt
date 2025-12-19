package com.swallow.fly.db

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/27 17:25
 * @UpdateRemark:   更新说明：
 */
//@Module
//@InstallIn(ApplicationComponent::class)
//object AppRoomModule {
//    /**
//     * @Provides 常用于被 @Module 注解标记类的内部的方法，并提供依赖项对象。
//     * @Singleton 提供单例
//     */
//    @Provides
//    @Singleton
//    fun provideAppDataBase(application: Application): AppDataBase {
//        return Room
//            .databaseBuilder(application, AppDataBase::class.java, "digital_app.db")
//            .fallbackToDestructiveMigration()
//            .allowMainThreadQueries()
//            .build()
//    }
//}

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Room 数据库模块 - 已废弃
 * 
 * @Description: Room 数据库依赖注入模块（旧版本，已废弃）
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2020/8/27 17:25
 * @UpdateRemark: 2024 - 已废弃，请使用 DatabaseModule 替代
 * 
 * @Deprecated("使用 DatabaseModule 替代，该模块将在未来版本中移除",
 *             ReplaceWith("DatabaseModule", "com.swallow.fly.db.DatabaseModule"))
 */
@Deprecated(
    message = "使用 DatabaseModule 替代，该模块将在未来版本中移除",
    replaceWith = ReplaceWith("DatabaseModule", "com.swallow.fly.db.DatabaseModule"),
    level = DeprecationLevel.WARNING
)
@Module
@InstallIn(SingletonComponent::class)
object AppRoomModule {
    
    @Provides
    @Singleton
    fun provideAppDataBaseManager(
        dataBase: AppDataBase
    ): AppDataBaseManager {
        return AppDataBaseManager.getInstance(dataBase)
    }
}