package com.zj.emotionbar.adapt2cc

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import com.zj.emotionbar.CusEmoticonsLayout
import com.zj.emotionbar.adapt2cc.func.FuncGridView
import com.zj.emotionbar.epack.emoticon.AdapterUtils.getAdapter
import com.zj.emotionbar.epack.emoticon.OnEmojiClickListener

open class CCEmojiLayout<T> @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, def: Int = 0) : CusEmoticonsLayout<T>(context, attrs, def) {

    private var onFuncListener: OnKeyboardListener<T>? = null

    private val onEmojiClickListener: OnEmojiClickListener = object : OnEmojiClickListener() {

        override fun getEt(): EditText? {
            return etChat
        }

        override fun onStickerClick(url: String, view: View) {
            onFuncListener?.onStickerClick(url, view, takeExtData())
        }
    }

    init {
        initEmoticon()
    }

    fun setOnFuncListener(funcListener: OnKeyboardListener<T>?) {
        this.onFuncListener = funcListener
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initEmoticon() {
        addFuncView(FuncGridView(context) { id: Int, view: View? -> this.onClick(id, view) })
        setAdapter(getAdapter(context, onEmojiClickListener))
        btnSend?.setOnClickListener {
            val content = etChat?.text.toString()
            if (content.isNotEmpty()) {
                etChat.setText("")
                onFuncListener?.sendText(content, takeExtData())
            }
        }
        btnVoice?.setOnTouchListener { view, motionEvent ->
            onFuncListener?.onVoiceEvent(view, motionEvent, takeExtData())
            return@setOnTouchListener false
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