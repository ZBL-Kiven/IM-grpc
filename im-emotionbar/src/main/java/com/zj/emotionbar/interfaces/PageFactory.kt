package com.zj.emotionbar.interfaces

import android.content.Context
import android.view.View
import com.zj.emotionbar.data.Emoticon

interface PageFactory<T: Emoticon> {
    /**
     * Create an emoji View
     */
    fun create(context: Context, emoticons: List<T>, clickListener: OnEmoticonClickListener<Emoticon>?): View
}