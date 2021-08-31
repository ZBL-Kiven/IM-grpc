package com.zj.imUi.widget.top

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.zj.imUi.UiMsgType

import com.zj.imUi.R
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imUi.widget.GroupRewardItem

/**
 * author: 李 祥
 * date:   2021/8/12 7:44 下午
 * description:
 */
class GroupMessageItemTitle @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : ConstraintLayout(context, attributeSet, defStyle) {

    private var mNickname: TextView
    private var mGroupRewardItem: GroupRewardItem

    init {
        LayoutInflater.from(context).inflate(R.layout.im_msg_item_title, this, true)
        mGroupRewardItem = findViewById(R.id.im_msg_item_title_group_reward_item)
        mNickname = findViewById(R.id.im_msg_item_title_tv_nickname)
    }

    fun setData(userId: Int?, messageBean: ImMsgIn?) {
        if (messageBean == null ||userId ==null ) return
        mGroupRewardItem.visibility = View.GONE //消息为打赏消息，且接收者为大V，打赏控件可见
        if (messageBean.getType() == UiMsgType.MSG_TYPE_QUESTION && userId == messageBean.getOwnerId()) {
            mGroupRewardItem.visibility = View.VISIBLE
            mGroupRewardItem.setBackGround(messageBean)
        }
        mNickname.text = messageBean.getSenderName()
    }
}
