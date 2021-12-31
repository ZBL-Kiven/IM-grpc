package com.zj.emotionbar.adapt2cc

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import com.zj.emotionbar.CusEmoticonsLayout
import com.zj.emotionbar.adapt2cc.func.FuncGridView
import com.zj.emotionbar.adpater.EmoticonPacksAdapter
import com.zj.emotionbar.data.Emoticon
import com.zj.emotionbar.data.EmoticonPack
import com.zj.emotionbar.epack.emoticon.OnEmojiClickListener
import com.zj.emotionbar.interfaces.EmoticonsFuncListener
import com.zj.emotionbar.interfaces.OnPayClickListener
import com.zj.emotionbar.interfaces.OnRetryClickListener

open class CCEmojiLayout<T, E : Emoticon> @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, def: Int = 0) : CusEmoticonsLayout<T, E>(context, attrs, def) {
    private var emoticonPacksAdapter: EmoticonPacksAdapter<E>? = null
    private var onFuncListener: OnKeyboardListener<T, E>? = null

    private val onEmojiClickListener = object : OnEmojiClickListener<E>() {

        override fun getEt(): EditText? {
            return etChat
        }


        override fun onStickerClick(emoticon: E, view: View?) {
            onFuncListener?.sendSticker(emoticon, view, takeExtData())
        }
    }

    private val pageEmoticonSelectedListener = object : EmoticonsFuncListener<E> {

        override fun onCurrentEmoticonPackChanged(currentPack: EmoticonPack<E>?) {
            onFuncListener?.onPageEmoticonSelected(currentPack)
        }

        override fun onPageSelected(position: Int) {
        }

    }
    private val payClickListener = OnPayClickListener<EmoticonPack<E>> { onFuncListener?.onPayClick(it) }
    private val retryClickListener=OnRetryClickListener<EmoticonPack<E>>{onFuncListener?.onRetryClick(it)}
    init {
        initEmoticon()
    }

    fun setOnFuncListener(funcListener: OnKeyboardListener<T, E>?) {
        this.onFuncListener = funcListener
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initEmoticon() {
        addFuncView(FuncGridView(context) { id: Int, view: View? -> this.onClick(id, view) })
        btnSend?.setOnClickListener {
            val content = etChat?.text.toString()
            if (content.isNotEmpty()) {
                etChat.setText("")
                onFuncListener?.sendText(content, takeExtData())
                setExtData(null)
            }
        }
        btnVoice?.setOnTouchListener { view, motionEvent ->
            onFuncListener?.onVoiceEvent(view, motionEvent, takeExtData())
            return@setOnTouchListener false
        }
    }

    fun updateEmoticon(pack: EmoticonPack<E>) {
        emoticonPacksAdapter?.let {
            var selectIndex = 0
            it.packList.forEachIndexed { index, packItem ->
                if (packItem.id == pack.id) {
                    it.packList[index] = pack
                    selectIndex = index
                    return@forEachIndexed
                }
            }
            emoticonPacksAdapter?.notifyDataIndexChanged(selectIndex)
        }
    }

    fun setEmoticon(packList: MutableList<EmoticonPack<E>>) {
        setOnPageEmoticonSelectedListener(pageEmoticonSelectedListener)
        emoticonPacksAdapter = EmoticonPacksAdapter(packList)
        emoticonPacksAdapter?.setClickListener(onEmojiClickListener)
        emoticonPacksAdapter?.setPayClickListener(payClickListener)
        emoticonPacksAdapter?.setRetryClickListenerListener(retryClickListener)
        emoticonPacksAdapter?.let {
            setAdapter(it)
        }
    }

    private fun onClick(id: Int, view: View?) {
        when (id) {
            FuncGridView.FUNC_ITEM_ID_PIC -> onFuncListener?.onPictureClick(view, takeExtData())
            FuncGridView.FUNC_ITEM_ID_TAKE_PIC -> onFuncListener?.onTakePhotoClick(view, takeExtData())
            FuncGridView.FUNC_ITEM_ID_VIDEO -> onFuncListener?.onSelectVideoClick(view, takeExtData())
            FuncGridView.FUNC_ITEM_ID_FILE -> onFuncListener?.onSelectFileClick(view, takeExtData())
            else -> {
            }
        }
    }
}