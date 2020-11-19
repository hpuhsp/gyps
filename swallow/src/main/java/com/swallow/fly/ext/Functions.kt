package com.swallow.fly.ext

typealias Supplier<T> = () -> T

interface Consumer<T> {

    fun accept(t: T)
}