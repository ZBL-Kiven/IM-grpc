package com.zj.rebuild.chat.widget.top

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.zj.database.entity.MessageInfoEntity
import com.zj.imUi.R
import com.zj.rebuild.chat.widget.GroupRewardItem

/**
 * author: 李 祥
 * date:   2021/8/12 7:44 下午
 * description:
 */
class GroupMessageItemTitle @JvmOverloads  constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attributeSet, defStyle) {

    private var mNickname :TextView
    private var mGroupRewardItem:GroupRewardItem
    private var mRenZheng:ImageView

    init {
        LayoutInflater.from(context).inflate(R.layout.group_message_item_title,this,true)
        mGroupRewardItem = findViewById(R.id.group_reward_item)
        mNickname = findViewById(R.id.tv_nickname)
        mRenZheng = findViewById(R.id.img_renzheng)
    }

    fun setData(userId:Int,messageBean: MessageInfoEntity){

        mGroupRewardItem.setVisible(false)
        //消息为打赏消息，且接收者为大V，打赏控件可见
        if (messageBean.msgType == "question"&&userId ==messageBean.ownerId){
            mGroupRewardItem.setVisible(true)
            mGroupRewardItem.setBackGround(messageBean)
        }
        //消息发送者为大V
         if (messageBean.sender.senderId == messageBean.ownerId)
        {
            mRenZheng.setVisible(true)
        }
        mNickname.text = messageBean.sender.senderName
    }
}
