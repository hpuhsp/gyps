package com.hnsh.core.utils

/**
 * Used to allow Singleton with arguments in Kotlin while keeping the code efficient and safe.
 */
open class SingletonHolderDoubleArg<out T, in A, in B>(private val creator: (A, B) -> T) {

    @Volatile
    private var instance: T? = null

    fun getInstance(arg1: A, arg2: B): T =
        instance ?: synchronized(this) {
            instance ?: creator(arg1, arg2).apply {
                instance = this
            }
        }

    /**
     * Used to force [SingletonHolderDoubleArg.getInstance] to create a new instance next time it's called.
     * Used in tests.
     */
    fun clearInstance() {
        instance = null
    }
}