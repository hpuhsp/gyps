package com.hnsh.core.utils

/**
 * Used to allow Singleton with arguments in Kotlin while keeping the code efficient and safe.
 *
 */
open class SingletonHolderSingleArg<out T, in A>(private val creator: (A) -> T) {

    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T =
        instance ?: synchronized(this) {
            instance ?: creator(arg).apply {
                instance = this
            }
        }
}