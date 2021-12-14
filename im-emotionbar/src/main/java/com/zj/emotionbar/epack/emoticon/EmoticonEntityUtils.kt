package com.zj.emotionbar.epack.emoticon

import android.content.Context
import com.zj.emotionbar.R
import com.zj.emotionbar.adpater.EmoticonPacksAdapter
import com.zj.emotionbar.aemoj.DefEmoticons
import com.zj.emotionbar.data.Emoticon
import com.zj.emotionbar.data.EmoticonPack
import com.zj.emotionbar.interfaces.OnEmoticonClickListener
import com.zj.emotionbar.utils.getResourceUri

object EmoticonEntityUtils {

    fun getEmoji(context: Context): EmoticonPack<Emoticon> {
        val emojiArray = mutableListOf<Emoticon>()
        DefEmoticons.sEmojiArray.mapTo(emojiArray) {
            val emoticon = Emoticon()
            emoticon.code = it.emoji
            emoticon.icon = context.getResourceUri(it.icon)
            emoticon.uri = context.getResourceUri(it.icon)
            return@mapTo emoticon
        }
        val pack = EmoticonPack<Emoticon>()
        pack.emoticons = emojiArray
        pack.iconUri = context.getResourceUri(R.mipmap.app_emo_func_ic_used)
        return pack
    }

    class DeleteEmoticon : Emoticon()

    class BigEmoticon : Emoticon()
}
