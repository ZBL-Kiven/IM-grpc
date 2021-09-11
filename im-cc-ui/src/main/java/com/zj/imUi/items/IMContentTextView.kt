package com.zj.imUi.items

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.zj.imUi.R
import com.zj.imUi.interfaces.ImMsgIn

class IMContentTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0) : AppCompatTextView(context, attrs, def), ImContentIn {

    override fun onSetData(data: ImMsgIn?) {
        if (data == null) return
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        typeface = Typeface.defaultFromStyle(Typeface.BOLD);
        setTextColor(if (data.getSelfUserId() == data.getSenderId()) {
            Color.WHITE
        } else {
            ContextCompat.getColor(context, R.color.message_textColor_replyMe)
        })
        text = data.getTextContent()
    }

    override fun onResume(data: ImMsgIn?) {

    }

    override fun onStop(data: ImMsgIn?) {

    }

    override fun onDestroy(data: ImMsgIn?) {

    }

}