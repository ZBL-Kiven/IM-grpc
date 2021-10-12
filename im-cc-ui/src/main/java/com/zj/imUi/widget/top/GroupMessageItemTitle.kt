package com.zj.imUi.widget.top

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.zj.imUi.UiMsgType

import com.zj.imUi.R
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imUi.utils.MessageSendTimeUtils
import com.zj.imUi.utils.TimeDiffUtils
import com.zj.imUi.widget.GroupRewardItem

/**
 * author: 李 祥
 * date:   2021/8/12 7:44 下午
 * description:
 */
class GroupMessageItemTitle @JvmOverloads constructor(context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0) : ConstraintLayout(context, attributeSet, defStyle) {

    private var mNickname: TextView
    private var mSendTime: AppCompatTextView
    private var mGroupRewardItem: GroupRewardItem


    init {
        LayoutInflater.from(context).inflate(R.layout.im_msg_item_title, this, true)
        mGroupRewardItem = findViewById(R.id.im_msg_item_title_group_reward_item)
        mNickname = findViewById(R.id.im_msg_item_title_tv_nickname)
        mSendTime = findViewById(R.id.im_msg_item_title_tv_send_time)
    }

    fun setData(data: ImMsgIn?, chatType:Any) {
        if (data == null) return
        mGroupRewardItem.visibility = View.VISIBLE
        mGroupRewardItem.setBackGround(data)
        mNickname.text = data.getSenderName()
        mSendTime.text = TimeDiffUtils.timeDifference(data.getSendTime())?.let { it1 ->
            TimeDiffUtils.setTimeText(it1, context)
        }
        if (chatType == 1) {
            if (data.getSelfUserId() == data.getOwnerId()) {
                mNickname.visibility = View.VISIBLE
                mNickname.setPadding(0, 1, 0, 0)
                mSendTime.textSize = 10f
                if (data.getPublished()) {
                    mSendTime.setTextColor(ContextCompat.getColor(context, R.color.im_msg_text_color_gray))
                } else mSendTime.setTextColor(ContextCompat.getColor(context, R.color.im_msg_bg_purple_60))
            } else {
                mNickname.visibility = View.GONE
                mSendTime.setPadding(0, 12, 0, 0)
                mSendTime.textSize = 12f
            }
        } else {
            if (data.getQuestionStatus() == 1) {
                mGroupRewardItem.visibility = View.GONE
            }
            mNickname.setPadding(0, 12, 0, 0)
            mNickname.visibility = View.VISIBLE
            mSendTime.visibility = View.GONE
        }
    }

    fun setDataSendTime(sendTime: Long) {
        mSendTime.text = TimeDiffUtils.setTimeText(sendTime, context)
    }
}
