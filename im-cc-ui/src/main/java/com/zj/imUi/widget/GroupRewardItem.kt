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
        textRewardNumber = findViewById(R.id.tv_reward_number)
        sparkImg = findViewById(R.id.img_spark)
        rewardLinearLayout = findViewById(R.id.ll)
        val textPaint: Paint = textRewardNumber.paint
        textPaint.isFakeBoldText = true
    }

    private fun setSpark(status: Int) {
        when (status) {
            2 -> {
                sparkImg.setImageResource(R.drawable.icon_currency_guoqi)
            }
            0, 1 -> sparkImg.setImageResource(R.drawable.icon_currency_normal)
        }
    }

    /**
     * statu 状态
     * number 金额数量
     */
    @SuppressLint("ResourceAsColor")
    fun setBackGround(messageBean: ImMsgIn) {
        setSpark(messageBean.getQuestionStatus())
        var plus = ""
        when (messageBean.getQuestionStatus()) {
            1 -> { //设置是否显示加号
                textRewardNumber.setTextColor(ContextCompat.getColor(context, R.color.reward_text_color_reply)) //设置布局背景
                rewardLinearLayout.setBackgroundResource(R.drawable.bg_yilingqu)
                plus = "+ "
            } //未回复
            0 -> {
                textRewardNumber.setTextColor(ContextCompat.getColor(context, R.color.im_msg_item_reward_number_color))
                rewardLinearLayout.setBackgroundResource(R.drawable.im_ui_reward_bg_default)
            } //超时
            2 -> {
                textRewardNumber.setTextColor(ContextCompat.getColor(context, R.color.reward_text_color_timeout))
                rewardLinearLayout.setBackgroundResource(R.drawable.bg_weilingqu)
            }
        }
        messageBean.getSpark().let {
            textRewardNumber.text = StringBuilder(plus).append(it)
        }
    }
}


