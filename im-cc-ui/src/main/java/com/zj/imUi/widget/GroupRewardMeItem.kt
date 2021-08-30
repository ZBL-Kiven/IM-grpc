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
        textRewardNumber = findViewById(R.id.tv_reward_number)
        sparkImg = findViewById(R.id.img_spark)
        rewardLinearLayout = findViewById(R.id.ll)
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
                rewardLinearLayout.setBackgroundResource(R.drawable.bg_yilingqu_me)
                sparkImg.setImageResource(R.drawable.icon_diamond_ed)
                reduce = "- "
            }
            0 -> {
                textRewardNumber.setTextColor(ContextCompat.getColor(context, R.color.reward_text_color_member_waitReply))
                rewardLinearLayout.setBackgroundResource(R.drawable.im_ui_reward_bg_default_me)
                sparkImg.setImageResource(R.drawable.icon_diamond_normal)
            }
            2 -> {
                textRewardNumber.setTextColor(ContextCompat.getColor(context, R.color.reward_text_color_timeout))
                rewardLinearLayout.setBackgroundResource(R.drawable.bg_yilingqu_me)
                sparkImg.setImageResource(R.drawable.icon_diamond_ed)
            }
        }
        textRewardNumber.text = StringBuilder(reduce).append(imMsgIn.getSpark().toString())
    }
}


