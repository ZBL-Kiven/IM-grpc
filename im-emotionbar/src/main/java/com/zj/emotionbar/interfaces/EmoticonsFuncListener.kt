package com.zj.emotionbar.interfaces

import com.zj.emotionbar.data.Emoticon
import com.zj.emotionbar.data.EmoticonPack

interface EmoticonsFuncListener<T : Emoticon> {

    fun onCurrentEmoticonPackChanged(currentPack: EmoticonPack<T>?)

    fun onPageSelected(position: Int)
}