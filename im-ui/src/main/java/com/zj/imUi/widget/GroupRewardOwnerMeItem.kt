package com.zj.rebuild.chat.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.sanhe.baselibrary.ext.setVisible
import com.zj.rebuild.R
import com.zj.rebuild.chat.bean.MessageBean
import java.lang.StringBuilder
import java.util.jar.Attributes

class GroupRewardOwnerMeItem @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attributeSet, defStyle) {

    private var textRewardNumber:AppCompatTextView
    private var textRewardTime:AppCompatTextView


    init {
        LayoutInflater.from(context).inflate(
            R.layout.group_reward_item_owner_me,this,true
        )
        textRewardNumber = findViewById(R.id.tv_rewardnumber)
        textRewardTime = findViewById(R.id.tv_tIme)

    }


    fun setData(messageBean: MessageBean){
        val string:StringBuilder = StringBuilder("+ ").append(messageBean.replyMsg?.questionContent?.spark)
        textRewardNumber.text = string
        textRewardTime.text = messageBean.sendTime.toString()
    }


}


