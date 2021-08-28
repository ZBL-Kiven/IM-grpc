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

class GroupRewardMeItem @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attributeSet, defStyle) {

    private var rewardCount: Int = 0
    private var llbackGroud: Int = 0
    private var tvbackGroud: Int = 0
    private var starColor: Int = 0

    private var isReducetext :AppCompatTextView
    private var textRewardNumber:AppCompatTextView
    private var sparkImg : AppCompatImageView
    private var rewardILnearLayout :LinearLayout;


    init {
        LayoutInflater.from(context).inflate(
            R.layout.group_reward_item_me,this,true
        )
        isReducetext = findViewById(R.id.tv_reduce)
        textRewardNumber = findViewById(R.id.tv_rewardnumber)
        sparkImg = findViewById(R.id.img_spark)
        rewardILnearLayout= findViewById(R.id.ll)
        val reducePaint: Paint =isReducetext.paint
        val textPaint:Paint = textRewardNumber.paint

        reducePaint.isFakeBoldText = true
        textPaint.isFakeBoldText = true
    }


    /**
     * status 状态
     * number 金额数量
     */
    @SuppressLint("ResourceAsColor")
     fun setBackGround(messageBean: MessageBean){
        when(messageBean.questionContent?.questionStatus){
            //已回复
            1->{
                //设置是否显示减号
                setIsReduce(true)
                //设置打赏金额的文字颜色
                textRewardNumber.setTextColor(resources.getColor(R.color.reward_text_color_timeout))
                isReducetext.setTextColor(resources.getColor(R.color.reward_text_color_timeout))
               //设置布局背景
                rewardILnearLayout.setBackgroundResource(R.drawable.bg_yilingqu_me)
            }
            //未回复在等待回复
            0->{
                setIsReduce(false)
                textRewardNumber.setTextColor(resources.getColor(R.color.reward_text_color_member_waitReply))
                rewardILnearLayout.setBackgroundResource(R.drawable.bg_moren_me)
            }
            //超时
            2->{
                setIsReduce(false)
                textRewardNumber.setTextColor(resources.getColor(R.color.reward_text_color_timeout))
                rewardILnearLayout.setBackgroundResource(R.drawable.bg_yilingqu_me)

            }
        }
        //设置打赏金额
        textRewardNumber.text = messageBean.questionContent?.spark.toString()
        //设置星星状态
        messageBean.questionContent?.questionStatus?.let { setSpark(it) }
    }
    private fun setIsReduce(isReduce :Boolean ){
        if(isReduce){
            isReducetext.setVisible(true)
        }else
            isReducetext.setVisible(false)
    }

    private  fun setSpark(statu: Int){
        when(statu){
            0->{
                sparkImg.setImageResource(R.drawable.icon_diamond_normal)
            }
            1,2->sparkImg.setImageResource(R.drawable.icon_diamond_ed)
        }
    }

}


