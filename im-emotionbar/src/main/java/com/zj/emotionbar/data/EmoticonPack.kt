package com.zj.emotionbar.data

import android.content.Context
import android.view.View
import com.zj.emotionbar.interfaces.*

class EmoticonPack<T : Emoticon> {
    enum class EmoticonType(var type: Int) {
        FREE(0), PAY(1), USED(-1) // 最近使用的
    }

    enum class EmoticonStatus(var type: Int) {
        LOADING(-1), ERROR(0), NORMAL(1)
    }

    var image: String? = null
    var name: String? = null
    var type = 0
    var status: EmoticonStatus = EmoticonStatus.LOADING
    var id: Int = 0
    var price: Int = 0
    var tag: Any? = null
    var emoticons: MutableList<T>? = mutableListOf()

    fun getView(context: Context, pack: EmoticonPack<T>, listener: OnEmoticonClickListener<T>?, payClickListener: OnPayClickListener<EmoticonPack<T>>?, retryClickListener: OnRetryClickListener<EmoticonPack<T>>?): View {
        return GridPageFactory<EmoticonPack<T>, T>().create(context, pack, listener, payClickListener, retryClickListener)
    }
}