package com.hnsh.core.ext

typealias Supplier<T> = () -> T

interface Consumer<T> {

    fun accept(t: T)
}