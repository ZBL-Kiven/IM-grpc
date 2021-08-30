package com.zj.imUi.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.zj.imUi.R
import com.zj.imUi.interfaces.ImMsgIn

class GroupRewardMeItem @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attributeSet, defStyle) {

    private var isReduceText: AppCompatTextView
    private var textRewardNumber: AppCompatTextView
    private var sparkImg: AppCompatImageView
    private var rewardLinearLayout: LinearLayout

    init {
        LayoutInflater.from(context).inflate(R.layout.im_msg_item_widget_reward_user, this, true)
        isReduceText = findViewById(R.id.tv_reduce)
        textRewardNumber = findViewById(R.id.tv_rewardnumber)
        sparkImg = findViewById(R.id.img_spark)
        rewardLinearLayout = findViewById(R.id.ll)
        val reducePaint: Paint = isReduceText.paint
        val textPaint: Paint = textRewardNumber.paint

        reducePaint.isFakeBoldText = true
        textPaint.isFakeBoldText = true
    }


    /**
     * status 状态
     * number 金额数量
     */
    @SuppressLint("ResourceAsColor")
    fun setBackGround(imMsgIn: ImMsgIn) {
        when (imMsgIn.getQuestionStatus()) { //已回复
            1 -> { //设置是否显示减号
                setIsReduce(true) //设置打赏金额的文字颜色
                textRewardNumber.setTextColor(ContextCompat.getColor(context, R.color.reward_text_color_timeout))
                isReduceText.setTextColor(ContextCompat.getColor(context, R.color.reward_text_color_timeout)) //设置布局背景
                rewardLinearLayout.setBackgroundResource(R.drawable.bg_yilingqu_me)
            } //未回复在等待回复
            0 -> {
                setIsReduce(false)
                textRewardNumber.setTextColor(ContextCompat.getColor(context, R.color.reward_text_color_member_waitReply))
                rewardLinearLayout.setBackgroundResource(R.drawable.bg_moren_me)
            } //超时
            2 -> {
                setIsReduce(false)
                textRewardNumber.setTextColor(ContextCompat.getColor(context, R.color.reward_text_color_timeout))
                rewardLinearLayout.setBackgroundResource(R.drawable.bg_yilingqu_me)
            }
        } //设置打赏金额
        textRewardNumber.text = imMsgIn.getSpark().toString() //设置星星状态
        setSpark(imMsgIn.getQuestionStatus())
    }

    private fun setIsReduce(isReduce: Boolean) {
        if (isReduce) {
            isReduceText.visibility = View.VISIBLE
        } else isReduceText.visibility = View.GONE
    }

    private fun setSpark(status: Int) {
        when (status) {
            0 -> {
                sparkImg.setImageResource(R.drawable.icon_diamond_normal)
            }
            1, 2 -> sparkImg.setImageResource(R.drawable.icon_diamond_ed)
        }
    }

}


