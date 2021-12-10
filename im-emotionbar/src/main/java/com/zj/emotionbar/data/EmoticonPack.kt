package com.zj.emotionbar.data

import android.content.Context
import android.view.View
import com.zj.emotionbar.interfaces.GridPageFactory
import com.zj.emotionbar.interfaces.OnEmoticonClickListener
import com.zj.emotionbar.interfaces.OnPayClickListener
import com.zj.emotionbar.interfaces.PayPageFactory

class EmoticonPack<T : Emoticon> {
    var iconUri: String? = null
    var name: String? = null
    var isDataChanged = false
    var payType = 0
    var id: Int = 0
    var tag: Any? = null
    lateinit var emoticons: MutableList<T>

    fun getView(context: Context, pack: EmoticonPack<T>, listener: OnEmoticonClickListener<Emoticon>?, payClickListener: OnPayClickListener<EmoticonPack<Emoticon>>?): View {

        return when (payType) {
            1 -> PayPageFactory<EmoticonPack<T>,T>().create(context, pack, payClickListener = payClickListener)
            else -> GridPageFactory<EmoticonPack<T>,T>().create(context, pack, listener)
        }

    }
}