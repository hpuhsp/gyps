package com.swallow.fly.http

import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * SSL 配置工具类 - 安全版本
 * 
 * 提供安全的 SSL/TLS 配置，支持 Debug 和 Release 模式
 * 
 * @Description: SSL 证书验证配置
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2026/03/13
 * @UpdateRemark: 
 *   - 修复安全漏洞：生产环境使用系统默认证书验证
 *   - Debug 模式可选择信任所有证书（仅用于开发测试）
 *   - 符合 Google Play 安全要求
 */
object SSLConfig {
    
    /**
     * 获取 SSLSocketFactory
     * 
     * @param trustAll 是否信任所有证书（仅 Debug 模式使用）
     * @return SSLSocketFactory 实例
     */
    @JvmStatic
    fun getSSLSocketFactory(trustAll: Boolean = false): SSLSocketFactory {
        return if (trustAll) {
            getUnsafeSSLSocketFactory()
        } else {
            getSecureSSLSocketFactory()
        }
    }
    
    /**
     * 获取 TrustManager
     * 
     * @param trustAll 是否信任所有证书（仅 Debug 模式使用）
     * @return X509TrustManager 实例
     */
    @JvmStatic
    fun getTrustManager(trustAll: Boolean = false): X509TrustManager {
        return if (trustAll) {
            getUnsafeTrustManager()
        } else {
            getDefaultTrustManager()
        }
    }
    
    /**
     * 获取 HostnameVerifier
     * 
     * @param trustAll 是否信任所有主机名（仅 Debug 模式使用）
     * @return HostnameVerifier 实例
     */
    @JvmStatic
    fun getHostnameVerifier(trustAll: Boolean = false): HostnameVerifier {
        return if (trustAll) {
            HostnameVerifier { _, _ -> true }
        } else {
            // 使用默认的主机名验证
            javax.net.ssl.HttpsURLConnection.getDefaultHostnameVerifier()
        }
    }
    
    /**
     * 获取安全的 SSLSocketFactory（生产环境使用）
     * 使用系统默认的证书验证
     */
    private fun getSecureSSLSocketFactory(): SSLSocketFactory {
        return SSLContext.getInstance("TLS").apply {
            init(null, null, SecureRandom())
        }.socketFactory
    }
    
    /**
     * 获取默认的 TrustManager（生产环境使用）
     */
    private fun getDefaultTrustManager(): X509TrustManager {
        val trustManagerFactory = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm()
        )
        trustManagerFactory.init(null as KeyStore?)
        return trustManagerFactory.trustManagers
            .first { it is X509TrustManager } as X509TrustManager
    }
    
    /**
     * 获取不安全的 SSLSocketFactory（仅 Debug 模式使用）
     * 
     * ⚠️ 警告：此方法会信任所有证书，存在安全风险
     * 仅用于开发测试环境，禁止在生产环境使用
     */
    private fun getUnsafeSSLSocketFactory(): SSLSocketFactory {
        val trustAllCerts = arrayOf<TrustManager>(getUnsafeTrustManager())
        return SSLContext.getInstance("TLS").apply {
            init(null, trustAllCerts, SecureRandom())
        }.socketFactory
    }
    
    /**
     * 获取不安全的 TrustManager（仅 Debug 模式使用）
     * 
     * ⚠️ 警告：此 TrustManager 不验证任何证书
     */
    private fun getUnsafeTrustManager(): X509TrustManager {
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                // 不验证客户端证书
            }
            
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                // 不验证服务器证书
            }
            
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }
    }
}
