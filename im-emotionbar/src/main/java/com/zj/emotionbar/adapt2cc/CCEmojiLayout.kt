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

class CCEmojiLayout<T>(context: Context?, attrs: AttributeSet?) : CusEmoticonsLayout<T>(context, attrs) {

    private var onFuncListener: OnKeyboardListener<T>? = null

    private val onEmojiClickListener: OnEmojiClickListener = object : OnEmojiClickListener() {

        override fun getEt(): EditText? {
            return etChat
        }

        override fun onStickerClick(url: String, view: View) {
            onFuncListener?.onStickerClick(url, view, extData)
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
                onFuncListener?.sendText(content, extData)
            }
        }
        btnVoice?.setOnTouchListener { view, motionEvent ->
            onFuncListener?.onVoiceEvent(view, motionEvent, extData)
            return@setOnTouchListener false
        }
    }

    private fun onClick(id: Int, view: View?) {
        when (id) {
            FuncGridView.FUNC_ITEM_ID_PIC -> onFuncListener?.onPictureClick(view, extData)
            FuncGridView.FUNC_ITEM_ID_TAKE_PIC -> onFuncListener?.onTakePhotoClick(view, extData)
            FuncGridView.FUNC_ITEM_ID_VIDEO -> onFuncListener?.onSelectVideoClick(view, extData)
            FuncGridView.FUNC_ITEM_ID_FILE -> onFuncListener?.onSelectFileClick(view, extData)
            else -> {
            }
        }
    }
}