package com.zj.emotionbar.interfaces

import android.content.Context
import android.view.View
import com.zj.emotionbar.data.Emoticon
import com.zj.emotionbar.data.EmoticonPack

interface PageFactory<T :EmoticonPack<E> ,E: Emoticon> {
    /**
     * Create an emoji View
     */
    fun create(context: Context, pack: T, clickListener: OnEmoticonClickListener<E>? = null, payClickListener: OnPayClickListener<T>? = null): View
}