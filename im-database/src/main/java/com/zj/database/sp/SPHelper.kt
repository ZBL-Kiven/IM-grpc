package com.zj.database.sp

import android.content.Context

internal object SPHelper {

    operator fun <T : Any> get(key: String, defaultValue: T): T? {
        return Preference.get(key, defaultValue)
    }

    fun <T> put(key: String, t: T) {
        Preference.put(key, t)
    }

    fun clear() {
        Preference.clear()
    }

    fun init(name: String, context: Context?) {
        Preference.init(name, context!!)
    }
}