package com.zj.emotionbar.data

import android.content.Context
import android.view.View
import com.zj.emotionbar.interfaces.GridPageFactory
import com.zj.emotionbar.interfaces.OnEmoticonClickListener
import com.zj.emotionbar.interfaces.OnPayClickListener
import com.zj.emotionbar.interfaces.PayPageFactory

class EmoticonPack<T : Emoticon> {
    enum class EmoticonType(var type: Int) {
        FREE(0), PAY(1), USED(-1), LOADING(-2)// 最近使用的
    }

    var image: String? = null
    var name: String? = null
    var type = 0
    var id: Int = 0
    var price: Int = 0
    var tag: Any? = null
    var emoticons: MutableList<T>? = mutableListOf()

    fun getView(context: Context, pack: EmoticonPack<T>, listener: OnEmoticonClickListener<T>?, payClickListener: OnPayClickListener<EmoticonPack<T>>?): View {

        return when (type) {
            EmoticonType.PAY.type -> PayPageFactory<EmoticonPack<T>, T>().create(context, pack, payClickListener = payClickListener)
            else -> GridPageFactory<EmoticonPack<T>, T>().create(context, pack, listener)
        }

    }
}