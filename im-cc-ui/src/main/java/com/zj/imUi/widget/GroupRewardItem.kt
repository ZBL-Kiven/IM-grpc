package com.zj.imUi.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.zj.imUi.R
import com.zj.imUi.interfaces.ImMsgIn
import java.lang.StringBuilder


class GroupRewardItem @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attributeSet, defStyle) {

    private var textRewardNumber: AppCompatTextView
    private var sparkImg: AppCompatImageView
    private var rewardLinearLayout: LinearLayout


    init {
        LayoutInflater.from(context).inflate(R.layout.im_msg_item_widget_reward, this, true)
        textRewardNumber = findViewById(R.id.im_msg_item_widget_reward_tv_reward_number)
        sparkImg = findViewById(R.id.im_msg_item_widget_reward_img_spark)
        rewardLinearLayout = findViewById(R.id.im_msg_item_widget_reward_ll)
        val textPaint: Paint = textRewardNumber.paint
        textPaint.isFakeBoldText = true
    }

    /**
     * number 金额数量
     */
    @SuppressLint("ResourceAsColor")
    fun setBackGround(data: ImMsgIn) {
        var plus = ""

        if(data.getSelfUserId() == data.getOwnerId()){
            sparkImg.setImageResource(R.drawable.im_msg_item_widget_reward_spark_normal)
            if (data.getQuestionStatus()== 0){
                textRewardNumber.setTextColor(ContextCompat.getColor(context, R.color.im_msg_im_msg_item_reward_number_color))
                rewardLinearLayout.setBackgroundResource(R.drawable.im_msg_item_widget_reward_bg_can_obtain)
            }else{
                textRewardNumber.setTextColor(ContextCompat.getColor(context, R.color.im_msg_reward_text_color_reply)) //设置布局背景
                rewardLinearLayout.setBackgroundResource(R.drawable.im_msg_item_widget_reward_bg_obtained)
                plus = ""
            }
            data.getSpark().let {
                textRewardNumber.text = StringBuilder(plus).append(it)
            }
        }else{
            if (data.getQuestionStatus()== 0){
                textRewardNumber.setTextColor(ContextCompat.getColor(context, R.color.im_msg_reward_text_color_member_waitReply))
                rewardLinearLayout.setBackgroundResource(R.drawable.im_msg_item_widget_reward_bg_obtained)
                sparkImg.setImageResource(R.drawable.im_msg_item_widget_reward_diamonds_normal)
            }else{
                textRewardNumber.setTextColor(ContextCompat.getColor(context, R.color.im_msg_reward_text_color_timeout))
                rewardLinearLayout.setBackgroundResource(R.drawable.im_msg_item_widget_reward_bg_obtained_me)
                sparkImg.setImageResource(R.drawable.im_msg_item_widget_reward_diamonds_gray)
                plus = "- "
            }
            data.getDiamonds().let {
                textRewardNumber.text = StringBuilder(plus).append(it)
            }
        }

    }
}


