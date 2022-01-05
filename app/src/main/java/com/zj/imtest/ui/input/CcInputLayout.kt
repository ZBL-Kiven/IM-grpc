package com.zj.imtest.ui.input

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.zj.ccIm.core.IMHelper
import com.zj.database.entity.MessageInfoEntity
import com.zj.emotionbar.adapt2cc.CCEmojiLayout
import com.zj.emotionbar.data.Emoticon
import com.zj.emotionbar.interfaces.ExtInflater
import com.zj.imtest.IMConfig

class CcInputLayout @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, def: Int = 0) : CCEmojiLayout<MessageInfoEntity, Emoticon>(context, attrs, def), ExtInflater<MessageInfoEntity> {

    init {
        setExtInflater(this)
        IMHelper.addRouteInfoObserver<MessageInfoEntity>("CcInputLayout").listen { r, _, payload ->
            Log.e("------ ", "$r")
            if (payload != IMConfig.ROUTE_CALL_ID_REPLY_MESSAGE) return@listen
            setExtData(r?.data)
        }
        IMHelper.addRouteInfoObserver<Int>("CcInputLayout.test").listen { r, _, _ ->
            Log.e("------ ", "===>  $r")
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