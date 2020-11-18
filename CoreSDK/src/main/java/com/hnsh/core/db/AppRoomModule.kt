package com.hnsh.core.db

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

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