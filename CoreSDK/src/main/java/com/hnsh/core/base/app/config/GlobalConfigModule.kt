package com.hnsh.core.base.app.config

import com.hnsh.core.base.app.AppModule
import com.hnsh.core.http.BaseUrl
import com.hnsh.core.http.Preconditions
import com.hnsh.core.http.ResponseErrorListener
import com.hnsh.core.http.di.ClientModule
import com.hnsh.core.http.interceptor.GlobalHttpHandler
import com.hnsh.core.http.interceptor.RequestInterceptor
import com.hnsh.core.http.printer.DefaultFormatPrinter
import com.hnsh.core.http.printer.FormatPrinter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import java.io.File
import javax.inject.Singleton
import kotlin.collections.ArrayList

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
    private var mCacheFile: File? = null
    private var mRetrofitConfiguration: ClientModule.RetrofitConfiguration? = null
    private var mOkhttpConfiguration: ClientModule.OkhttpConfiguration? = null
    private var mGsonConfiguration: AppModule.GsonConfiguration? = null
    private var mPrintHttpLogLevel: RequestInterceptor.Level? = null
    private var mFormatPrinter: FormatPrinter? = null


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
        return this
    }

    class Builder {
        internal var apiUrl: HttpUrl? = null
        internal var baseUrl: BaseUrl? = null
        internal var handler: GlobalHttpHandler? = null
        internal var interceptors: MutableList<Interceptor>? = null
        internal var responseErrorListener: ResponseErrorListener? = null
        internal var cacheFile: File? = null
        internal var retrofitConfiguration: ClientModule.RetrofitConfiguration? = null
        internal var okhttpConfiguration: ClientModule.OkhttpConfiguration? = null
        internal var gsonConfiguration: AppModule.GsonConfiguration? = null
        internal var printHttpLogLevel: RequestInterceptor.Level? = null
        internal var formatPrinter: FormatPrinter? = null

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
            if (interceptors == null) interceptors = ArrayList()
            interceptors!!.add(interceptor)
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
    }

    /**
     * ==================================Dagger Hilt实例===============================================
     */
    @Singleton
    @Provides
    fun provideInterceptors(): List<Interceptor> {
        return mInterceptors ?: ArrayList()
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
}