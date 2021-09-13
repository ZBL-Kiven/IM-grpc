package com.zj.imUi.widget.bottom

import android.content.Context
import android.util.AttributeSet
import android.util.TimeUtils
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.zj.imUi.R
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imUi.utils.TimeDiffUtils
import com.zj.imUi.widget.GroupRewardMeItem
import java.lang.StringBuilder

/**
 * author: 李 祥
 * date:   2021/8/12 7:44 下午
 * description:
 */
class GroupMessageItemTime @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : ConstraintLayout(context, attributeSet, defStyle) {

    private var mTimeTextView: TextView? = null
    private var mGroupRewardMeItem: GroupRewardMeItem

    init {
        LayoutInflater.from(context).inflate(R.layout.im_msg_item_bottom, this, true)
        mGroupRewardMeItem = findViewById(R.id.im_msg_item_bottom_group_reward_item)
        mTimeTextView = findViewById(R.id.im_msg_item_bottom_tv_time)
    }

    fun setData(imMsgIn: ImMsgIn) {
        mGroupRewardMeItem.setBackGround(imMsgIn)

        mTimeTextView?.text = (TimeDiffUtils.timeDifference(imMsgIn.getSendTime()))?.let { setTimeText(it) }

        if(imMsgIn.getQuestionStatus() == 0&&imMsgIn.getSenderId() == imMsgIn.getSelfUserId()){
            mTimeTextView?.setTextColor(ContextCompat.getColor(context,R.color.text_color_white))
        }
        if (imMsgIn.getQuestionStatus() == 2){
            mTimeTextView?.setTextColor(ContextCompat.getColor(context,R.color.text_color_gray))
        }
    }

    private fun setTimeText(sendTime: Long): CharSequence? {
        return if (sendTime in 60000..3599999) {
            StringBuilder(timeParse(sendTime)).append(context.getString(R.string.im_ui_min_ago))
        } else if (sendTime > 3600000 && sendTime < 3600000 * 48) {
            StringBuilder(timeParseHour(sendTime)).append(context.getString(R.string.im_ui_hours_ago))
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
