package com.zj.imUi.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.zj.imUi.R
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imUi.utils.TimeDiffUtils

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
        textRewardTime.text = (TimeDiffUtils.timeDifference(imMsgIn.getSendTime()))?.let { TimeDiffUtils.setTimeText(it,context) }
        textRewardTime.setTextColor(ContextCompat.getColor(context,R.color.text_color_white))
        textRewardNumber.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
    }

    fun setDataWithTime(sendTime: Long) {
        textRewardTime.text = TimeDiffUtils.setTimeText(sendTime,context)
    }
}


