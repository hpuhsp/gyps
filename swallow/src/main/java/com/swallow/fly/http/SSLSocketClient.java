/*
 * Copyright 2018 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.swallow.fly.http;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @Description: SSL Socket 客户端配置（已废弃）
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2020/9/16 17:43
 * @UpdateRemark: 2026/03/13 - 已废弃，请使用 SSLConfig.kt
 *
 * @deprecated 此类存在安全漏洞（信任所有证书），已被 {@link SSLConfig} 替代
 *             请使用 SSLConfig.getSSLSocketFactory() 和 SSLConfig.getTrustManager()
 */
@Deprecated
public class SSLSocketClient {

    private SSLSocketClient() {
        throw new IllegalStateException("Utility class");
    }

    /** 获取 SSLSocketFactory */
    public static javax.net.ssl.SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, getTrustManagers(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** 获取 TrustManager 数组 */
    private static TrustManager[] getTrustManagers() {
        return new TrustManager[]{getTrustManager()};
    }

    /** 获取 HostnameVerifier */
    public static HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }

    /** 获取 X509TrustManager */
    public static X509TrustManager getTrustManager() {
        return new MyTrustManager();
    }

    private static final class MyTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            // 信任所有客户端证书（仅用于开发/调试，生产环境请勿使用）
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            // 信任所有服务端证书（仅用于开发/调试，生产环境请勿使用）
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
