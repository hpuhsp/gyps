package com.swallow.fly.base

interface IFragment {
    fun useEventBus(): Boolean

    fun showSystemProgress(): Boolean
}