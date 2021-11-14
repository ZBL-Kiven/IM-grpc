package com.zj.emotionbar.interfaces

import com.zj.emotionbar.data.EmoticonPack

interface EmoticonsIndicator {
    /**
     * Move to an emoticon
     * @param position Pages in emoticons
     * @param pack the pack to be move
     */
    fun playTo(position: Int, pack: EmoticonPack<*>)

    fun notifyDataChanged()
}
