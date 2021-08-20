package com.zj.base.utils.storage.sp

import android.content.Context
import android.content.SharedPreferences

internal object Preference {

    private var sp: SharedPreferences? = null

    @JvmStatic
    fun init(name: String, context: Context) {
        sp = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    @JvmStatic
    fun <T> put(key: String, t: T?) {

        requireNotNull().let {
            val editor = it.edit()
            try {
                when (t) {
                    is String -> {
                        editor.putString(key, t)
                    }
                    is Int -> {
                        editor.putInt(key, t)
                    }
                    is Long -> {
                        editor.putLong(key, t)
                    }
                    is Float -> {
                        editor.putFloat(key, t)
                    }
                    is Boolean -> {
                        editor.putBoolean(key, t)
                    }
                    is Set<*> -> {
                        @Suppress("UNCHECKED_CAST") editor.putStringSet(key, t as? Set<String>)
                    }
                    else -> {
                    }
                }
            } finally {
                editor.apply()
            }
        }
    }

    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String, def: Any): T? {
        if (isSupportedType(def)) requireNotNull().let {
            try {
                return when (def) {
                    is String -> {
                        it.getString(key, def) as? T
                    }
                    is Int -> {
                        it.getInt(key, def) as? T
                    }
                    is Long -> {
                        it.getLong(key, def) as? T
                    }
                    is Float -> {
                        it.getFloat(key, def) as? T
                    }
                    is Boolean -> {
                        it.getBoolean(key, def) as? T
                    }
                    is Set<*> -> {
                        def as Set<String>
                        it.getStringSet(key, def) as? T
                    }
                    else -> null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

        } else return null
    }

    private fun isSupportedType(t: Any): Boolean {
        return when (t) {
            is String, is Int, is Long, is Float, is Boolean, is Set<*> -> true
            else -> false
        }
    }

    @JvmStatic
    fun clear() {
        sp?.edit()?.clear()?.apply()
    }

    private fun requireNotNull(): SharedPreferences {
        return sp ?: throw NullPointerException("bad call ,you can't use Preferences without init")
    }
}