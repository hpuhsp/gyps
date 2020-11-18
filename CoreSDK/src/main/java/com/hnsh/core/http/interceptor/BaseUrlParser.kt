package com.hnsh.core.http.interceptor

import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import me.jessyan.retrofiturlmanager.parser.AdvancedUrlParser
import me.jessyan.retrofiturlmanager.parser.DomainUrlParser
import me.jessyan.retrofiturlmanager.parser.SuperUrlParser
import me.jessyan.retrofiturlmanager.parser.UrlParser
import okhttp3.HttpUrl

/**
 * @Description:
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/1 20:00
 * @UpdateRemark:   更新说明：
 */
class BaseUrlParser : UrlParser {
    private var mDomainUrlParser: UrlParser? = null

    @Volatile
    private var mAdvancedUrlParser: UrlParser? = null

    @Volatile
    private var mSuperUrlParser: UrlParser? = null
    private var mRetrofitUrlManager: RetrofitUrlManager? = null

    override fun init(retrofitUrlManager: RetrofitUrlManager?) {
        mRetrofitUrlManager = retrofitUrlManager
        mDomainUrlParser = DomainUrlParser()
        mDomainUrlParser?.init(retrofitUrlManager)
    }

    override fun parseUrl(domainUrl: HttpUrl?, url: HttpUrl): HttpUrl? {
        if (null == domainUrl) return url
        if (domainUrl.toString().contains(RetrofitUrlManager.IDENTIFICATION_PATH_SIZE)) {
            if (mSuperUrlParser == null) {
                synchronized(this) {
                    if (mSuperUrlParser == null) {
                        mSuperUrlParser = SuperUrlParser()
                        mSuperUrlParser?.init(mRetrofitUrlManager)
                    }
                }
            }
            return mSuperUrlParser!!.parseUrl(domainUrl, url)
        }

        //如果是高级模式则使用高级解析器
        if (mRetrofitUrlManager!!.isAdvancedModel) {
            if (mAdvancedUrlParser == null) {
                synchronized(this) {
                    if (mAdvancedUrlParser == null) {
                        mAdvancedUrlParser = AdvancedUrlParser()
                        mAdvancedUrlParser?.init(mRetrofitUrlManager)
                    }
                }
            }
            return mAdvancedUrlParser!!.parseUrl(domainUrl, url)
        }
        return mDomainUrlParser!!.parseUrl(domainUrl, url)
    }
}