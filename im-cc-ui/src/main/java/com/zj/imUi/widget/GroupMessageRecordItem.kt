package com.zj.imUi.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.zj.imUi.UiMsgType
import com.zj.imUi.R
import com.zj.imUi.interfaces.ImMsgIn


class GroupMessageRecordItem @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attributeSet, defStyle) {

    private var tvTime: AppCompatTextView

    init {
        LayoutInflater.from(context).inflate(R.layout.im_msg_item_widget_record, this, true)
        tvTime = findViewById(R.id.tv_record_time)
    }

    fun setData(userId: Int, messageBean: ImMsgIn?) {
        if (messageBean == null) return
        tvTime.text = StringBuilder(messageBean.getAudioContentDuration().toString()).append("''")
        if (!(messageBean.getSenderId() != messageBean.getOwnerId() || messageBean.getReplyMsgId() != 0L)) { //发送者为大V 且不是回复消息
            tvTime.setTextColor(ContextCompat.getColor(context, R.color.text_color_white))

        } else if (messageBean.getReplyMsgType() == UiMsgType.MSG_TYPE_QUESTION && messageBean.getPublished()) {

            tvTime.setTextColor(ContextCompat.getColor(context, R.color.text_color_origin_private))

        } else if (messageBean.getReplyMsgType() == UiMsgType.MSG_TYPE_QUESTION && !messageBean.getPublished()) {

            tvTime.setTextColor(ContextCompat.getColor(context, R.color.bg_purple))
        }
    }
}