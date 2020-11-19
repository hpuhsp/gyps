package com.swallow.fly.ext.prefs

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

const val SP_NAME_DEFAULT = "default_sp"

fun Context.sharedPreferences(spName: String = SP_NAME_DEFAULT): SharedPreferences =
    getSharedPreferences(spName, Context.MODE_PRIVATE)

private inline fun <T> SharedPreferences.delegate(
    key: String? = null,
    defaultValue: T,
    crossinline getter: SharedPreferences.(String, T) -> T,
    crossinline setter: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor
): ReadWriteProperty<Any, T> =
    object : ReadWriteProperty<Any, T> {
        override fun getValue(thisRef: Any, property: KProperty<*>): T =
            getter(key ?: property.name, defaultValue)!!

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) =
            edit().setter(key ?: property.name, value).apply()
    }

fun SharedPreferences.int(key: String? = null, defValue: Int = 0): ReadWriteProperty<Any, Int> {
    return delegate(key, defValue, SharedPreferences::getInt, SharedPreferences.Editor::putInt)
}

fun SharedPreferences.long(key: String? = null, defValue: Long = 0): ReadWriteProperty<Any, Long> {
    return delegate(key, defValue, SharedPreferences::getLong, SharedPreferences.Editor::putLong)
}

fun SharedPreferences.float(
    key: String? = null,
    defValue: Float = 0f
): ReadWriteProperty<Any, Float> {
    return delegate(key, defValue, SharedPreferences::getFloat, SharedPreferences.Editor::putFloat)
}

fun SharedPreferences.boolean(
    key: String? = null,
    defValue: Boolean = false
): ReadWriteProperty<Any, Boolean> {
    return delegate(
        key,
        defValue,
        SharedPreferences::getBoolean,
        SharedPreferences.Editor::putBoolean
    )
}

fun SharedPreferences.stringSet(
    key: String? = null,
    defValue: Set<String> = emptySet()
): ReadWriteProperty<Any, Set<String>> {
    return delegate(
        key,
        defValue,
        { it, set -> getStringSet(it, set) as Set<String> },
        SharedPreferences.Editor::putStringSet
    )
}

fun SharedPreferences.string(
    key: String? = null,
    defValue: String = ""
): ReadWriteProperty<Any, String> {
    return delegate(
        key,
        defValue,
        { str1, str2 -> getString(str1, str2) as String },
        SharedPreferences.Editor::putString
    )
}