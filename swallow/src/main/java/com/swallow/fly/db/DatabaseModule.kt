package com.swallow.fly.db

import android.content.Context
import androidx.room.Room
import com.swallow.fly.base.lifecycle.config.SwallowConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @Description: 数据库依赖注入模块
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2024/12/22
 * @UpdateRemark:   现代化升级 - 类型安全的 Builder + 优先级控制
 *
 * 职责：只负责提供 Room 数据库实例
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DEFAULT_DB_NAME = "gyps-base.db"

    /**
     * 提供 Room 数据库
     * ✅ 使用合并后的配置
     */
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context, config: SwallowConfig): AppDataBase {
        val builder = config.database
        return builder.database ?: Room.databaseBuilder(
            context,
            AppDataBase::class.java,
            DEFAULT_DB_NAME
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }
}
