package com.zj.imUi.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.zj.imUi.R
import com.zj.imUi.interfaces.ImMsgIn

import java.lang.StringBuilder

class GroupRewardOwnerMeItem @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attributeSet, defStyle) {

    private var textRewardNumber: AppCompatTextView
    private var textRewardTime: AppCompatTextView

    init {
        LayoutInflater.from(context).inflate(R.layout.im_msg_item_owner_reward_bottom, this, true)
        textRewardNumber = findViewById(R.id.im_msg_item_owner_reward_bottom_tv_reward_number)
        textRewardTime = findViewById(R.id.im_msg_item_owner_reward_bottom_tv_tIme)
    }

    @SuppressLint("SetTextI18n")
    fun setData(imMsgIn: ImMsgIn) {
        textRewardNumber.text = "+ " + imMsgIn.getReplyMsgQuestionSpark()
        textRewardTime.text = setTimeText(imMsgIn.getSendTime())
        textRewardTime.setTextColor(ContextCompat.getColor(context,R.color.text_color_white))
    }

    private fun setTimeText(sendTime: Long): CharSequence? {
        return if (sendTime in 60000..3599999) {
            StringBuilder(timeParse(sendTime)).append(" ").append(context.getString(R.string.im_ui_min_ago))
        } else if (sendTime > 3600000 && sendTime < 3600000 * 48) {
            StringBuilder(timeParseHour(sendTime)).append(" ").append(context.getString(R.string.im_ui_hours_ago))
        } else null
    }

    private fun timeParseHour(duration: Long): String {
        val time: String?
        val hour = duration / 3600000
        time = hour.toString()
        return time
    }

    private fun timeParse(duration: Long): String {
        val minute = duration / 60000
        return minute.toString()
    }
}


