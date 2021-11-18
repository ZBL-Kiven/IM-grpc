package com.zj.imtest.ui.input

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.bean.RoteInfo
import com.zj.database.entity.MessageInfoEntity
import com.zj.emotionbar.adapt2cc.CCEmojiLayout
import com.zj.emotionbar.interfaces.ExtInflater
import com.zj.imtest.IMConfig

class CcInputLayout @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, def: Int = 0) : CCEmojiLayout<MessageInfoEntity>(context, attrs, def), ExtInflater<MessageInfoEntity> {

    init {
        setExtInflater(this)
        IMHelper.addReceiveObserver<RoteInfo<MessageInfoEntity>>("CcInputLayout").filterIn { _, c -> c == IMConfig.ROUTE_CALL_ID_REPLY_MESSAGE }.listen { r, _, payload ->
            if (payload != IMConfig.ROUTE_CALL_ID_REPLY_MESSAGE) return@listen
            setExtData(r?.data)
        }
    }

    override fun onInflate(view: FrameLayout?, inflater: LayoutInflater?, data: MessageInfoEntity?) {
        if (data == null) {
            view?.removeAllViews()
            view?.visibility = View.GONE
        } else {
            view?.visibility = View.VISIBLE
            InputReplyLayoutInflater.inflate(view, inflater, data) {
                setExtData(null)
            }
        }
    }
}