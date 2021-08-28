package com.zj.rebuild.chat.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.sanhe.baselibrary.utils.ToastUtils
import com.zj.rebuild.BuildConfig
import com.zj.rebuild.R
import com.zj.rebuild.chat.bean.MessageBean

class GroupMessageRecordItem @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attributeSet, defStyle) {

    private var tvTime:AppCompatTextView
    private var tvText:AppCompatTextView



    init {
        LayoutInflater.from(context).inflate(R.layout.group_message_record_item,this,true)
        tvTime=findViewById(R.id.tv_record_time)
        tvText= findViewById(R.id.tv_record_justtext)
    }

    fun setData(userId:Int,messageBean: MessageBean){
        tvTime.text = StringBuilder(messageBean.audioContent?.duration.toString()).append("''")
        if(messageBean.sender?.senderId==messageBean.ownerId&&messageBean.replyMsgId == 0){
            //发送者为大V 且不是回复消息
            tvTime.setTextColor(resources.getColor(R.color.text_color_white))
        }else if(messageBean.replyMsg?.msgType == "question"&& messageBean.replyMsg?.questionContent?.isPublic == true){
            tvTime.setTextColor(resources.getColor(R.color.text_color_origin_private))
        }else if(messageBean.replyMsg?.msgType == "question"&& messageBean.replyMsg?.questionContent?.isPublic == true){
            tvTime.setTextColor(resources.getColor(R.color.bg_purple))
        }else{
            if (BuildConfig.DEBUG){
                ToastUtils.showToast(context,messageBean.msgType,0)
            }
        }
    }

}