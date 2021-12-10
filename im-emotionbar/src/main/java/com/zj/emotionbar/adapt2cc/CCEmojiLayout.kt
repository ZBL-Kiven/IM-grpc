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
import com.zj.emotionbar.interfaces.OnPayClickListener
import com.zj.emotionbar.widget.EmoticonsFuncView

open class CCEmojiLayout<T> @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, def: Int = 0) : CusEmoticonsLayout<T>(context, attrs, def) {
    private var emoticonPacksAdapter: EmoticonPacksAdapter? = null
    private var onFuncListener: OnKeyboardListener<T>? = null

    private val onEmojiClickListener: OnEmojiClickListener = object : OnEmojiClickListener() {

        override fun getEt(): EditText? {
            return etChat
        }

        override fun onStickerClick(url: String, view: View) {
            onFuncListener?.onStickerClick(url, view, takeExtData())
        }
    }

    private val pageEmoticonSelectedListener: EmoticonsFuncView.EmoticonsFuncListener = object : EmoticonsFuncView.EmoticonsFuncListener {
        override fun onCurrentEmoticonPackChanged(currentPack: EmoticonPack<out Emoticon>?) {
            onFuncListener?.onPageEmoticonSelected(currentPack as EmoticonPack<Emoticon>?)
        }

        override fun onPageSelected(position: Int) {
        }

    }
    private val payClickListener = OnPayClickListener<EmoticonPack<Emoticon>> { onFuncListener?.onPayClick(it) }

    init {
        initEmoticon()
    }

    fun setOnFuncListener(funcListener: OnKeyboardListener<T>?) {
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

    fun updateEmoticon(pack: EmoticonPack<out Emoticon>) {
        emoticonPacksAdapter?.let {
            var selectIndex = 0
            var packList = it.packList.toMutableList()
            packList.forEachIndexed { index, pakcItem ->
                if (pakcItem.id == pack.id) {
                    packList[index] = pack
                    selectIndex = index
                    return@forEachIndexed
                }
            }
            setEmoticon(packList)
            onToolBarItemClick(packList[selectIndex])
        }
    }

    fun setEmoticon(packList: List<EmoticonPack<out Emoticon>>) {
        setOnPageEmoticonSelectedListener(pageEmoticonSelectedListener)
        emoticonPacksAdapter = EmoticonPacksAdapter(packList)
        emoticonPacksAdapter?.setClickListener(onEmojiClickListener)
        emoticonPacksAdapter?.setPayClickListener(payClickListener)
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