package com.swallow.fly.base.app.config

import android.app.Application
import androidx.annotation.Nullable
import androidx.room.Room
import com.swallow.fly.base.app.AppModule
import com.swallow.fly.db.AppDataBase
import com.swallow.fly.http.BaseUrl
import com.swallow.fly.http.Preconditions
import com.swallow.fly.http.ResponseErrorListener
import com.swallow.fly.http.di.ClientModule
import com.swallow.fly.http.di.ImageLoaderInterceptor
import com.swallow.fly.http.interceptor.GlobalHttpHandler
import com.swallow.fly.http.interceptor.RequestInterceptor
import com.swallow.fly.http.printer.DefaultFormatPrinter
import com.swallow.fly.http.printer.FormatPrinter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.ElementsIntoSet
import dagger.multibindings.IntoSet
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import java.io.File
import javax.inject.Singleton

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/16 15:53
 * @UpdateRemark:   更新说明：
 */
@Module
@InstallIn(ApplicationComponent::class)
object GlobalConfigModule {
    private var mApiUrl: HttpUrl? = null
    private var mBaseUrl: BaseUrl? = null
    private var mHandler: GlobalHttpHandler? = null
    private var mInterceptors: List<Interceptor>? = null
    private var mErrorListener: ResponseErrorListener? = null
    private var mRetrofitConfiguration: ClientModule.RetrofitConfiguration? = null
    private var mOkhttpConfiguration: ClientModule.OkhttpConfiguration? = null
    private var mGsonConfiguration: AppModule.GsonConfiguration? = null
    private var mPrintHttpLogLevel: RequestInterceptor.Level? = null
    private var mFormatPrinter: FormatPrinter? = null
    
    // 数据缓存
    private var mCacheFile: File? = null
    private var dataBase: AppDataBase? = null
    
    /**
     * Glide使用OkHttp方式图片加载自定义拦截器
     */
    private var mImageLoaderInterceptor: Interceptor? = null
    
    
    /**
     * 空实现，不做任何处理
     */
    private val EMPTY: ResponseErrorListener = object :
        ResponseErrorListener {
        override fun handleResponseError(
            t: Throwable?
        ): Throwable? {
            
            return RuntimeException()
        }
    }
    
    fun builder(): Builder {
        return Builder()
    }
    
    fun getInstance(builder: Builder): GlobalConfigModule {
        mApiUrl = builder.apiUrl
        mBaseUrl = builder.baseUrl
        mHandler = builder.handler
        mInterceptors = builder.interceptors
        mErrorListener = builder.responseErrorListener
        mCacheFile = builder.cacheFile
        mRetrofitConfiguration = builder.retrofitConfiguration
        mOkhttpConfiguration = builder.okhttpConfiguration
        mGsonConfiguration = builder.gsonConfiguration
        mPrintHttpLogLevel = builder.printHttpLogLevel
        mFormatPrinter = builder.formatPrinter
        dataBase = builder.dataBase
        mImageLoaderInterceptor = builder.imageLoaderInterceptor
        return this
    }
    
    class Builder {
        internal var apiUrl: HttpUrl? = null
        internal var baseUrl: BaseUrl? = null
        internal var handler: GlobalHttpHandler? = null
        internal var interceptors = ArrayList<Interceptor>()
        internal var responseErrorListener: ResponseErrorListener? = null
        internal var retrofitConfiguration: ClientModule.RetrofitConfiguration? = null
        internal var okhttpConfiguration: ClientModule.OkhttpConfiguration? = null
        internal var gsonConfiguration: AppModule.GsonConfiguration? = null
        internal var printHttpLogLevel: RequestInterceptor.Level? = null
        internal var formatPrinter: FormatPrinter? = null
        internal var cacheFile: File? = null
        internal var dataBase: AppDataBase? = null
        internal var imageLoaderInterceptor: Interceptor? = null
        
        fun baseurl(baseUrl: String?): Builder {
            if (baseUrl.isNullOrEmpty()) {
                throw NullPointerException("BaseUrl can not be empty")
            }
            apiUrl = baseUrl.toHttpUrlOrNull()
            return this
        }
        
        fun baseurl(baseUrl: BaseUrl?): Builder {
            this.baseUrl = Preconditions.checkNotNull(
                baseUrl,
                BaseUrl::class.java.canonicalName + "can not be null."
            )
            return this
        }
        
        fun globalHttpHandler(handler: GlobalHttpHandler?): Builder { //用来处理http响应结果
            this.handler = handler
            return this
        }
        
        fun addInterceptor(interceptor: Interceptor): Builder { //动态添加任意个interceptor
            interceptors.clear()
            interceptors.add(interceptor)
            return this
        }
        
        fun responseErrorListener(listener: ResponseErrorListener?): Builder { //处理onError逻辑
            responseErrorListener = listener
            return this
        }
        
        fun cacheFile(cacheFile: File?): Builder {
            this.cacheFile = cacheFile
            return this
        }
        
        fun cacheDB(dataBase: AppDataBase): Builder {
            this.dataBase = dataBase
            return this
        }
        
        fun retrofitConfiguration(retrofitConfiguration: ClientModule.RetrofitConfiguration): Builder {
            this.retrofitConfiguration = retrofitConfiguration
            return this
        }
        
        fun okhttpConfiguration(okhttpConfiguration: ClientModule.OkhttpConfiguration): Builder {
            this.okhttpConfiguration = okhttpConfiguration
            return this
        }
        
        fun gsonConfiguration(gsonConfiguration: AppModule.GsonConfiguration?): Builder {
            this.gsonConfiguration = gsonConfiguration
            return this
        }
        
        fun printHttpLogLevel(printHttpLogLevel: RequestInterceptor.Level?): Builder { //是否让框架打印 Http 的请求和响应信息
            this.printHttpLogLevel = Preconditions.checkNotNull(
                printHttpLogLevel,
                "The printHttpLogLevel can not be null, use RequestInterceptor.Level.NONE instead."
            )
            return this
        }
        
        fun formatPrinter(formatPrinter: FormatPrinter?): Builder {
            this.formatPrinter = Preconditions.checkNotNull(
                formatPrinter,
                FormatPrinter::class.java.canonicalName + "can not be null."
            )
            return this
        }
        
        fun imageLoaderInterceptor(interceptor: Interceptor): Builder {
            this.imageLoaderInterceptor = interceptor
            return this
        }
    }
    
    /**
     * ==================================Dagger Hilt实例===============================================
     */
    @Singleton
    @Provides
    @Nullable
    fun provideInterceptors(): List<Interceptor>? {
        return mInterceptors
    }
    
    /**
     * 提供 BaseUrl,默认使用 <"https://api.github.com/">
     *
     * @return
     */
    @Singleton
    @Provides
    fun provideBaseUrl(): HttpUrl {
        if (mBaseUrl != null) {
            val httpUrl = mBaseUrl!!.url()
            if (httpUrl != null) {
                return httpUrl
            }
        }
        return if (mApiUrl == null) "https://api.github.com/".toHttpUrlOrNull()!! else mApiUrl!!
    }
    
    /**
     * 提供处理 Http 请求和响应结果的处理类
     *
     * @return
     */
    @Singleton
    @Provides
    fun provideGlobalHttpHandler(): GlobalHttpHandler? {
        return mHandler
    }
    
    /**
     * 提供处理 RxJava 错误的管理器的回调
     *
     * @return
     */
    @Singleton
    @Provides
    fun provideResponseErrorListener(): ResponseErrorListener {
        return mErrorListener
            ?: EMPTY
    }
    
    @Singleton
    @Provides
    fun provideRetrofitConfiguration(): ClientModule.RetrofitConfiguration? {
        return mRetrofitConfiguration
    }
    
    @Singleton
    @Provides
    fun provideOkhttpConfiguration(): ClientModule.OkhttpConfiguration? {
        return mOkhttpConfiguration
    }
    
    @Singleton
    @Provides
    fun provideGsonConfiguration(): AppModule.GsonConfiguration? {
        return mGsonConfiguration
    }
    
    @Singleton
    @Provides
    fun providePrintHttpLogLevel(): RequestInterceptor.Level {
        return if (mPrintHttpLogLevel == null) RequestInterceptor.Level.ALL else mPrintHttpLogLevel!!
    }
    
    @Singleton
    @Provides
    fun provideFormatPrinter(): FormatPrinter? {
        return if (mFormatPrinter == null) DefaultFormatPrinter() else mFormatPrinter
    }
    
    @Singleton
    @Provides
    fun provideRoomDatabase(application: Application): AppDataBase {
        return if (null == dataBase) getAppCacheDB(application) else dataBase!!
    }
    
    @ImageLoaderInterceptor
    @Singleton
    @Provides
    fun provideImageLoaderInterceptor(): Interceptor? {
        return mImageLoaderInterceptor
    }
    
    
    /**
     * 本地缓存数据库默认配置
     */
    private fun getAppCacheDB(application: Application): AppDataBase {
        return Room
            .databaseBuilder(application, AppDataBase::class.java, "fly_cache.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }
}