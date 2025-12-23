package com.swallow.fly.db

/**
 * @Description: 数据库配置构建器
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2024/12/22
 * @UpdateRemark:   现代化升级 - 类型安全的配置构建器
 * 
 * 使用示例：
 * ```kotlin
 * override fun configureDatabase(context: Context, builder: DatabaseConfigBuilder) {
 *     val db = Room.databaseBuilder(context, AppDataBase::class.java, "my_db.db")
 *         .fallbackToDestructiveMigration()
 *         .build()
 *     builder.database(db)
 * }
 * ```
 */
class DatabaseConfigBuilder {
    
    var database: AppDataBase? = null
        private set
    
    /**
     * 配置数据库实例
     * 
     * @param db Room 数据库实例
     * @return 当前构建器，支持链式调用
     */
    fun database(db: AppDataBase): DatabaseConfigBuilder {
        this.database = db
        return this
    }
}
