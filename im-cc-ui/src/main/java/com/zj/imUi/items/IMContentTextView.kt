package com.zj.imUi.items

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.zj.imUi.R
import com.zj.imUi.UiMsgType
import com.zj.imUi.interfaces.ImMsgIn

class IMContentTextView @JvmOverloads constructor(context: Context,
    attrs: AttributeSet? = null,
    def: Int = 0) : AppCompatTextView(context, attrs, def), ImContentIn {

    override fun onSetData(data: ImMsgIn?) {
        if (data == null) return
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        typeface = Typeface.defaultFromStyle(Typeface.BOLD);
        setTextColor(if (data.getSelfUserId() == data.getSenderId()) {
            Color.WHITE
        } else {
            ContextCompat.getColor(context, R.color.im_msg_message_textColor_replyMe)
        })
        if (data.getType() == UiMsgType.MSG_TYPE_TEXT) text = data.getTextContent()
        else if (data.getType() == UiMsgType.MSG_TYPE_QUESTION) {
            //Q&A界面  回答文本
            if (data.getSelfUserId() == data.getOwnerId()) {
                setTextColor(ContextCompat.getColor(context, R.color.im_msg_text_color_gray))
            } else setTextColor(ContextCompat.getColor(context, R.color.im_msg_text_color_black))
            text = data.getAnswerContentTextContent()
        }
    }

    override fun chatType(chatType:Any) {
    }


    override fun onResume(data: ImMsgIn?) {

    }

    override fun onStop(data: ImMsgIn?) {

    }

    override fun onDestroy(data: ImMsgIn?) {

    }

}