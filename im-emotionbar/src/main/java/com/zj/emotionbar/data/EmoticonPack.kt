package com.zj.emotionbar.data

import android.content.Context
import android.view.View
import com.zj.emotionbar.interfaces.GridPageFactory
import com.zj.emotionbar.interfaces.OnEmoticonClickListener
import com.zj.emotionbar.interfaces.PageFactory

class EmoticonPack<T : Emoticon> {
    var iconUri: String? = null
    var name: String? = null
    private var pageFactory: PageFactory<T> = GridPageFactory()
    var isDataChanged = false
    var tag: Any? = null
    lateinit var emoticons: MutableList<T>

    fun getView(context: Context, listener: OnEmoticonClickListener<Emoticon>?): View {
        return pageFactory.create(context, emoticons, listener)
    }
}