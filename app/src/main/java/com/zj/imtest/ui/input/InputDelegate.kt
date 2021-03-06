package com.zj.imtest.ui.input

import android.Manifest
import android.content.pm.PackageManager
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.zj.album.AlbumIns
import com.zj.album.options.AlbumOptions
import com.zj.album.ui.preview.images.transformer.TransitionEffect
import com.zj.album.ui.views.image.easing.ScaleEffect
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.bean.ChannelRegisterInfo
import com.zj.database.entity.MessageInfoEntity
import com.zj.emotionbar.adapt2cc.CCEmojiLayout
import com.zj.emotionbar.adapt2cc.OnKeyboardListener
import com.zj.emotionbar.aemoj.DefEmoticons
import com.zj.emotionbar.data.Emoticon
import com.zj.emotionbar.data.EmoticonPack
import com.zj.emotionbar.epack.emoticon.EmoticonEntityUtils
import com.zj.emotionbar.utils.getResourceUri
import com.zj.imtest.BaseApp
import com.zj.imtest.BaseApp.Companion.context


class InputDelegate(private val inputLayout: CCEmojiLayout<MessageInfoEntity, Emoticon>?, private val groupId: Long) : OnKeyboardListener<MessageInfoEntity, Emoticon> {

    private var curChannel: ChannelRegisterInfo? = null

    fun updateCurChannel(data: ChannelRegisterInfo?) {
        this.curChannel = data
    }

//    override fun onPictureClick(view: View?, extData: MessageInfoEntity?) {
//        startAlbum(true, view, extData)
//    }
//
//    override fun onTakePhotoClick(view: View?, extData: MessageInfoEntity?) {
//    }
//
//    override fun onSelectVideoClick(view: View?, extData: MessageInfoEntity?) {
//        startAlbum(false, view, extData)
//    }

    override fun onPageEmoticonSelected(emoticonPack: EmoticonPack<Emoticon>?) {
        emoticonPack?.let { _ ->
            inputLayout?.context?.let {}
        }
    }


    override fun onPayClick(emoticonPack: EmoticonPack<Emoticon>?) {
        inputLayout?.context?.let {
            if (emoticonPack != null) {
                val emojiArray = mutableListOf<EmoticonEntityUtils.BigEmoticon>()
                emoticonPack.status = EmoticonPack.EmoticonStatus.LOADING
                emoticonPack.emoticons = emojiArray.toMutableList()
                inputLayout.updateEmoticon(emoticonPack)
                Handler().postDelayed({
                    emoticonPack.type = 0
                    emoticonPack.status = EmoticonPack.EmoticonStatus.ERROR
                    emoticonPack.emoticons = emojiArray.toMutableList()
                    inputLayout.updateEmoticon(emoticonPack)
                }, 5000)
            }
        }
    }

    override fun onRetryClick(emoticonPack: EmoticonPack<Emoticon>?) {
        if (emoticonPack != null) {
            val emojiArray = mutableListOf<EmoticonEntityUtils.BigEmoticon>()
            DefEmoticons.sEmojiArray.mapTo(emojiArray) {
                val emoticon = EmoticonEntityUtils.BigEmoticon()
                emoticon.id = it.icon
                emoticon.url = "https://obetomo.com/wp/wp-content/uploads/2018/07/nk_ice.gif"
                emoticon.icon = "https://obetomo.com/wp/wp-content/uploads/2018/07/nk_ice.gif"
                emoticon.pack = emoticonPack
                return@mapTo emoticon
            }
            emoticonPack.type = 0
            emoticonPack.status = EmoticonPack.EmoticonStatus.NORMAL
            emoticonPack.emoticons = emojiArray.toMutableList()
            inputLayout?.updateEmoticon(emoticonPack)
        }
    }


    override fun sendSticker(emoticon: Emoticon, view: View?, extData: MessageInfoEntity?) {

        curChannel?.let {
            emoticon.url?.let { it1 ->
                emoticon.pack?.let { it2 ->
                    emoticon.id?.let { it3 -> IMHelper.Sender.sendEmotion(it.groupId, it1, it2.id, it3) }
                    val emojiArray = mutableListOf<EmoticonEntityUtils.BigEmoticon>()
                    emojiArray.add(EmoticonEntityUtils.BigEmoticon().apply {
                        this.id = 34
                        this.url = "https://scpic.chinaz.net/files/pic/pic9/202009/apic27858.jpg"
                        this.icon = "https://scpic.chinaz.net/files/pic/pic9/202009/apic27858.jpg"
                        this.pack = EmoticonPack<Emoticon>().apply { this.id = id }
                    })
                    inputLayout?.updateEmoticon(getUseEmoticonPackList(emojiArray.toMutableList()))
                }

            }
        }
    }


    private fun getUseEmoticonPackList(emoticonInfo: MutableList<Emoticon>): EmoticonPack<Emoticon> {
        return EmoticonPack<Emoticon>().apply {
            this.emoticons = emoticonInfo
            this.type = EmoticonPack.EmoticonType.FREE.type
            this.id = -1
            this.image = context.getResourceUri(com.zj.emotionbar.R.mipmap.app_emo_func_ic_used)
        }
    }

    override fun sendText(content: String, extData: MessageInfoEntity?) {
        curChannel?.let {
            IMHelper.Sender.sendText(content, it.groupId, extData)
        }
    }

    override fun onVoiceEvent(view: View?, ev: MotionEvent?, extData: MessageInfoEntity?) {}

    private fun startAlbum(isImage: Boolean, v: View?, extData: MessageInfoEntity?) {
        (v?.context as? FragmentActivity)?.let {
            val i = ActivityCompat.checkSelfPermission(BaseApp.context, Manifest.permission.READ_EXTERNAL_STORAGE)
            val i1 = ActivityCompat.checkSelfPermission(BaseApp.context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (i == PackageManager.PERMISSION_GRANTED && i1 == PackageManager.PERMISSION_GRANTED) {
                val maxSelectCount = if (isImage) 9 else 1
                val mimeType = if (isImage) AlbumOptions.ofImage() else AlbumOptions.ofVideo()
                AlbumIns.with(it).setOriginalPolymorphism(true).simultaneousSelection(true).maxSelectedCount(maxSelectCount).mimeTypes(mimeType).sortWithDesc(true).useOriginDefault(false).imgSizeRange(1, 20000000).videoSizeRange(1, 200000000).imageScaleEffect(ScaleEffect.QUAD).pagerTransitionEffect(TransitionEffect.Zoom).start { isOk, data ->
                    if (isOk) {
                        data?.forEach { f ->
                            if (f.isImage) {
                                IMHelper.Sender.sendImg(f.path, 200, 200, groupId, extData)
                            }
                            if (f.isVideo) {
                                IMHelper.Sender.sendVideo(f.path, 200, 200, f.duration, groupId, extData)
                            }
                        }
                    } else inputLayout?.reset()
                }
            } else {
                ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
            }
        }
    }
}