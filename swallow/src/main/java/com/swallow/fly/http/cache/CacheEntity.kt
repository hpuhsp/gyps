package com.swallow.fly.http.cache

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/8/27 18:51
 * @UpdateRemark:   更新说明：
 */
@Entity
class CacheEntity {

    //    @PrimaryKey(autoGenerate = true)
    //    private long cid;
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "cachekey")
    private lateinit var key: String


    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private var data: ByteArray?=null

    @ColumnInfo(typeAffinity = ColumnInfo.TEXT)
    private var gsonData: String? = null

    fun getGsonData(): String? {
        return gsonData
    }

    fun setGsonData(gsonData: String?) {
        this.gsonData = gsonData
    }

    fun getData(): ByteArray? {
        return data
    }

    fun setData(data: ByteArray) {
        this.data = data
    }


    fun getKey(): String? {
        return key
    }

    fun setKey(key: String) {
        this.key = key
    }
}