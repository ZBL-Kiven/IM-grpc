package com.zj.emotionbar.interfaces

import android.view.View
import com.zj.emotionbar.data.Emoticon
import com.zj.emotionbar.data.EmoticonPack

interface EmoticonsToolBar<E : Emoticon> {

    fun setToolBarItemClickListener(listener: OnToolBarItemClickListener<E>?)

    fun selectEmotionPack(pack: EmoticonPack<E>)

    fun setPackList(packs: List<EmoticonPack<E>>)

    fun addFixedToolItemView(view: View?, isRight: Boolean)

    fun notifyDataChanged()
}

interface OnToolBarItemClickListener<E : Emoticon> {
    fun onToolBarItemClick(pack: EmoticonPack<E>)
}