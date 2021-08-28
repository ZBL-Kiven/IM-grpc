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
import java.util.jar.Attributes

class GroupRewardItem @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attributeSet, defStyle) {

    private var rewardCount: Int = 0
    private var llbackGroud: Int = 0
    private var tvbackGroud: Int = 0
    private var starColor: Int = 0

    private var isAddimg :AppCompatTextView
    private var textRewardNumber:AppCompatTextView
    private var sparkImg : AppCompatImageView
    private var rewardILnearLayout :LinearLayout;


    init {
        LayoutInflater.from(context).inflate(
            R.layout.group_reward_item,this,true
        )
        isAddimg = findViewById(R.id.tv_add)
        textRewardNumber = findViewById(R.id.tv_rewardnumber)
        sparkImg = findViewById(R.id.img_spark)
        rewardILnearLayout= findViewById(R.id.ll)



        val reducePaint: Paint =isAddimg.paint
        val textPaint: Paint = textRewardNumber.paint

        reducePaint.isFakeBoldText = true
        textPaint.isFakeBoldText = true
    }

    private fun setIsAdd(isadd :Boolean ){
        if(isadd){
            isAddimg.setVisible(true)
        }else
            isAddimg.setVisible(false)
    }

    private  fun setSpark(statu: Int){
        when(statu){
            2->{
                sparkImg.setImageResource(R.drawable.icon_currency_guoqi)
            }
            0,1->sparkImg.setImageResource(R.drawable.icon_currency_normal)
        }
    }


    /**
     * statu 状态
     * number 金额数量
     */
    @SuppressLint("ResourceAsColor")
     fun setBackGround(messageBean: MessageBean){
        //设置打赏金额
        messageBean.questionContent?.spark?.let { textRewardNumber.text = it.toString() }
        //设置星星状态
        messageBean.questionContent?.questionStatus?.let { setSpark(it) }
        when(messageBean.questionContent?.questionStatus){
            //已回复
            1->{
                //设置是否显示加号
                setIsAdd(false)
                //设置打赏金额的文字颜色
                textRewardNumber.setTextColor(resources.getColor(R.color.reward_text_color_reply))
               //设置布局背景
                rewardILnearLayout.setBackgroundResource(R.drawable.bg_yilingqu)
            }
            //未回复
            0->{
                setIsAdd(false)
                textRewardNumber.setTextColor(resources.getColor(R.color.reward_text_color_not_reply))
                rewardILnearLayout.setBackgroundResource(R.drawable.bg_moren)
            }
            //超时
            2->{
                setIsAdd(false)
                textRewardNumber.setTextColor(resources.getColor(R.color.reward_text_color_timeout))
                rewardILnearLayout.setBackgroundResource(R.drawable.bg_weilingqu)

            }
        }
    }

}


