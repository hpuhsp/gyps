package com.swallow.fly.http

import retrofit2.CallAdapter
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @Description: Retrofit 协程适配器工厂（已废弃）
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/14 14:43
 * @UpdateRemark:   2026/03/13 - 已废弃，Retrofit 2.6.0+ 原生支持 suspend 函数
 * 
 * @deprecated Retrofit 2.6.0+ 已原生支持 suspend 函数，不再需要此适配器
 *             请将 Service 接口中的 Deferred<T> 改为 suspend fun
 *             
 *             迁移示例：
 *             // 修改前
 *             @GET("users/{id}")
 *             fun getUser(@Path("id") id: String): Deferred<User>
 *             
 *             // 修改后
 *             @GET("users/{id}")
 *             suspend fun getUser(@Path("id") id: String): User
 */
@Deprecated(
    message = "Retrofit 2.6.0+ 原生支持 suspend 函数，不再需要此适配器",
    replaceWith = ReplaceWith("suspend fun"),
    level = DeprecationLevel.WARNING
)
class CoroutineCallAdapterFactory private constructor() : CallAdapter.Factory() {
    companion object {
        @JvmStatic
        @JvmName("create")
        operator fun invoke() = CoroutineCallAdapterFactory()
    }

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (Deferred::class.java != getRawType(returnType)) {
            return null
        }
        if (returnType !is ParameterizedType) {
            throw IllegalStateException(
                "Deferred return type must be parameterized as Deferred<Foo> or Deferred<out Foo>"
            )
        }
        val responseType = getParameterUpperBound(0, returnType)

        val rawDeferredType = getRawType(responseType)
        return if (rawDeferredType == Response::class.java) {
            if (responseType !is ParameterizedType) {
                throw IllegalStateException(
                    "Response must be parameterized as Response<Foo> or Response<out Foo>"
                )
            }
            ResponseCallAdapter<Any>(
                getParameterUpperBound(0, responseType)
            )
        } else {
            BodyCallAdapter<Any>(
                responseType
            )
        }
    }

    private class BodyCallAdapter<T>(
        private val responseType: Type
    ) : CallAdapter<T, Deferred<T>> {

        override fun responseType() = responseType

        override fun adapt(call: Call<T>): Deferred<T> {
            val deferred = CompletableDeferred<T>()

            deferred.invokeOnCompletion {
                if (deferred.isCancelled) {
                    call.cancel()
                }
            }

            call.enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    deferred.completeExceptionally(t)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful) {
                        deferred.complete(response.body()!!)
                    } else {
                        deferred.completeExceptionally(HttpException(response))
                    }
                }
            })

            return deferred
        }
    }

    private class ResponseCallAdapter<T>(
        private val responseType: Type
    ) : CallAdapter<T, Deferred<Response<T>>> {

        override fun responseType() = responseType

        override fun adapt(call: Call<T>): Deferred<Response<T>> {
            val deferred = CompletableDeferred<Response<T>>()

            deferred.invokeOnCompletion {
                if (deferred.isCancelled) {
                    call.cancel()
                }
            }

            call.enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    deferred.completeExceptionally(t)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    deferred.complete(response)
                }
            })

            return deferred
        }
    }
}