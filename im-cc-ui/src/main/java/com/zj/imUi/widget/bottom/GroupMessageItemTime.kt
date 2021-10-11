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

        mTimeTextView?.text = (TimeDiffUtils.timeDifference(imMsgIn.getSendTime()))?.let { TimeDiffUtils.setTimeText(it,context) }

        if(imMsgIn.getQuestionStatus() == 0&&imMsgIn.getSenderId() == imMsgIn.getSelfUserId() && imMsgIn.getPublished()){
            mTimeTextView?.setTextColor(ContextCompat.getColor(context,R.color.im_msg_text_color_white))
        }else
            mTimeTextView?.setTextColor(ContextCompat.getColor(context,R.color.im_msg_text_color_gray))
    }

    fun setDataWithTime(sendTime: Long,imMsgIn: ImMsgIn) {
        mTimeTextView?.text = TimeDiffUtils.setTimeText(sendTime,context)

        if(imMsgIn.getQuestionStatus() == 0&&imMsgIn.getSenderId() == imMsgIn.getSelfUserId() && imMsgIn.getPublished()){
            mTimeTextView?.setTextColor(ContextCompat.getColor(context,R.color.im_msg_text_color_white))
        }else
            mTimeTextView?.setTextColor(ContextCompat.getColor(context,R.color.im_msg_text_color_gray))
    }


}
