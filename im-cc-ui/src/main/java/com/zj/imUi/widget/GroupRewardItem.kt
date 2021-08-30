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


class GroupRewardItem @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attributeSet, defStyle) {


    private var isAddImg: AppCompatTextView
    private var textRewardNumber: AppCompatTextView
    private var sparkImg: AppCompatImageView
    private var rewardLinearLayout: LinearLayout


    init {
        LayoutInflater.from(context).inflate(R.layout.im_msg_item_widget_reward, this, true)
        isAddImg = findViewById(R.id.tv_add)
        textRewardNumber = findViewById(R.id.tv_rewardnumber)
        sparkImg = findViewById(R.id.img_spark)
        rewardLinearLayout = findViewById(R.id.ll)
        val reducePaint: Paint = isAddImg.paint
        val textPaint: Paint = textRewardNumber.paint

        reducePaint.isFakeBoldText = true
        textPaint.isFakeBoldText = true
    }

    private fun setIsAdd(isAdd: Boolean) {
        if (isAdd) {
            isAddImg.visibility = View.VISIBLE
        } else isAddImg.visibility = View.GONE
    }

    private fun setSpark(statu: Int) {
        when (statu) {
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
        messageBean.getSpark().let { textRewardNumber.text = it.toString() }
        setSpark(messageBean.getQuestionStatus())
        when (messageBean.getQuestionStatus()) {
            1 -> { //设置是否显示加号
                setIsAdd(true) //设置打赏金额的文字颜色
                textRewardNumber.setTextColor(ContextCompat.getColor(context, R.color.reward_text_color_reply)) //设置布局背景
                rewardLinearLayout.setBackgroundResource(R.drawable.bg_yilingqu)
            } //未回复
            0 -> {
                setIsAdd(false)
                textRewardNumber.setTextColor(ContextCompat.getColor(context, R.color.reward_text_color_not_reply))
                rewardLinearLayout.setBackgroundResource(R.drawable.bg_moren)
            } //超时
            2 -> {
                setIsAdd(false)
                textRewardNumber.setTextColor(ContextCompat.getColor(context, R.color.reward_text_color_timeout))
                rewardLinearLayout.setBackgroundResource(R.drawable.bg_weilingqu)
            }
        }
    }

}


