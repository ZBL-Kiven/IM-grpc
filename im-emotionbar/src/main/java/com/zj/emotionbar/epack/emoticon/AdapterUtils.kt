package com.zj.emotionbar.epack.emoticon

import android.content.Context
import com.zj.emotionbar.R
import com.zj.emotionbar.adpater.EmoticonPacksAdapter
import com.zj.emotionbar.aemoj.DefEmoticons
import com.zj.emotionbar.data.Emoticon
import com.zj.emotionbar.data.EmoticonPack
import com.zj.emotionbar.interfaces.OnEmoticonClickListener
import com.zj.emotionbar.utils.getResourceUri

object AdapterUtils {

    fun getAdapter(context: Context, emoticonClickListener: OnEmoticonClickListener<Emoticon>?): EmoticonPacksAdapter {
        val packs = mutableListOf<EmoticonPack<out Emoticon>>()
        packs.add(getEmoji(context))
        val adapter = EmoticonPacksAdapter(packs)
        adapter.setClickListener(emoticonClickListener)
        return adapter
    }

    private fun getEmoji(context: Context): EmoticonPack<Emoticon> {
        val emojiArray = mutableListOf<Emoticon>()
        DefEmoticons.sEmojiArray.mapTo(emojiArray) {
            val emoticon = Emoticon()
            emoticon.code = it.emoji
            emoticon.uri = context.getResourceUri(it.icon)
            return@mapTo emoticon
        }
        val pack = EmoticonPack<Emoticon>()
        pack.emoticons = emojiArray
        pack.iconUri = context.getResourceUri(R.mipmap.icon_face_thumb)
        return pack
    }

    class DeleteEmoticon : Emoticon()

    class BigEmoticon: Emoticon()
}
