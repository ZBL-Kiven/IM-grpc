package com.zj.imUi.items

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

import com.zj.imUi.R
import com.zj.imUi.interfaces.ImMsgIn

/**
 * author: 李 祥
 * date:   2021/8/12 7:44 下午
 * description:
 */
class IMContentName @JvmOverloads  constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attributeSet, defStyle) {

    private var mNickname :TextView
    private var ivOwner:ImageView

    init {
        LayoutInflater.from(context).inflate(R.layout.im_msg_widget_nickname,this,true)
        mNickname = findViewById(R.id.im_msg_widget_nickname_tv_name)
        ivOwner = findViewById(R.id.im_msg_widget_nickname_img_owner)
    }

    fun setData(messageBean: ImMsgIn){
        //消息发送者为大V
         if (messageBean.getSenderId() == messageBean.getOwnerId())
        {
            ivOwner.visibility=View.VISIBLE
        }
        mNickname.text = messageBean.getSenderName()
    }
}
