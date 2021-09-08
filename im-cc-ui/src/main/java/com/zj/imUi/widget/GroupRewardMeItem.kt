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

class GroupRewardMeItem @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attributeSet, defStyle) {

    private var textRewardNumber: AppCompatTextView
    private var sparkImg: AppCompatImageView
    private var rewardLinearLayout: LinearLayout

    init {
        LayoutInflater.from(context).inflate(R.layout.im_msg_item_widget_reward_user, this, true)
        textRewardNumber = findViewById(R.id.im_msg_item_widget_reward_user_tv_reward_number)
        sparkImg = findViewById(R.id.im_msg_item_widget_reward_user_img_spark)
        rewardLinearLayout = findViewById(R.id.im_msg_item_widget_reward_user_ll)
        val textPaint: Paint = textRewardNumber.paint
        textPaint.isFakeBoldText = true
    }

    /**
     * status 状态
     * number 金额数量
     */
    @SuppressLint("ResourceAsColor")
    fun setBackGround(imMsgIn: ImMsgIn) {
        var reduce = ""
        when (imMsgIn.getQuestionStatus()) {
            1 -> {
                textRewardNumber.setTextColor(ContextCompat.getColor(context, R.color.reward_text_color_timeout))
                rewardLinearLayout.setBackgroundResource(R.drawable.im_msg_item_widget_reward_bg_obtained_me)
                sparkImg.setImageResource(R.drawable.im_msg_item_widget_reward_diamonds_gray)
                reduce = "- "
            }
            0 -> {
                textRewardNumber.setTextColor(ContextCompat.getColor(context, R.color.reward_text_color_member_waitReply))
                rewardLinearLayout.setBackgroundResource(R.drawable.im_msg_item_widget_reward_bg_can_obtain_me)
                sparkImg.setImageResource(R.drawable.im_msg_item_widget_reward_diamonds_normal)
            }
            2 -> {
                textRewardNumber.setTextColor(ContextCompat.getColor(context, R.color.reward_text_color_timeout))
                rewardLinearLayout.setBackgroundResource(R.drawable.im_msg_item_widget_reward_bg_obtained_me)
                sparkImg.setImageResource(R.drawable.im_msg_item_widget_reward_diamonds_gray)
            }
        }
        textRewardNumber.text = StringBuilder(reduce).append(imMsgIn.getDiamonds().toString())
    }
}


