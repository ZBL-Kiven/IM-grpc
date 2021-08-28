package com.zj.rebuild.chat.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.sanhe.baselibrary.ext.setVisible
import com.zj.rebuild.R
import com.zj.rebuild.chat.bean.MessageBean
import java.lang.StringBuilder

/**
 * author: 李 祥
 * date:   2021/8/9  6:44 下午
 * description: 消息列表Item 打赏内容
 */
class GroupRewardQuestionContent @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attributeSet, defStyle) {


    private var textQuestion: AppCompatTextView
    private var textReplyType: AppCompatTextView
    private var textResponsetype: AppCompatTextView
    private var imgCountdown: AppCompatImageView
    private var tvCountdown: AppCompatTextView
    private var llCountDown: LinearLayout
    private var questionIcon: AppCompatImageView

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.group_message_type_reward_me_question, this, true)
        textQuestion = findViewById(R.id.tv_question_message)
        textResponsetype = findViewById(R.id.tv_reward_response_type)
        imgCountdown = findViewById(R.id.img_countdown)
        tvCountdown = findViewById(R.id.tv_countdown)
        llCountDown = findViewById(R.id.ll_countdown)
        questionIcon = findViewById(R.id.img_question_icon)
        textReplyType = findViewById(R.id.tv_reply_type)
    }

    private fun timeParseHour(duration: Long): String? {
        var time: String? = ""
        val hour = duration / 360000
        val minutes = duration % 360000
        val minute = Math.round(minutes.toFloat() / 6000)
        if (hour < 1) {
            time += 0
        }
        time += "$hour:"
        if (minute < 10) {
            time += "0"
        }
        time += minute
        return time
    }

    @SuppressLint("ResourceAsColor")
    fun setData(userId: Int, messageBean: MessageBean) {

        //最开始此控件均不可见
        textReplyType.setVisible(false)
        //问题内容
        textQuestion.text = messageBean.questionContent?.textContent?.text
        textQuestion.setTextColor(resources.getColor(R.color.text_color_black))

        //当为群主视角查看未回答问题时,增加可点击textView控件
        if (messageBean.questionContent?.questionStatus == 0 && userId == messageBean.ownerId) {
            textReplyType.setVisible(true)
            val stringBuilder: StringBuilder =
                StringBuilder("REPLY BY ").append(messageBean.questionContent?.contentType?.toUpperCase())
            textReplyType.text = stringBuilder
            if (messageBean.questionContent?.isPublic == true) {
                textResponsetype.text = "Public"
                textResponsetype.setTextColor(context.resources.getColor(R.color.text_color_member_type))
                textReplyType.setBackgroundResource(R.drawable.textview_frame_origin_roundcornor_4dp)
            } else {
                textResponsetype.text = "Private"
                textResponsetype.setTextColor(context.resources.getColor(R.color.text_color_purple_private))
                textReplyType.setBackgroundResource(R.drawable.textview_frame_purple_roundcornor_4dp)
            }
        } else
            textResponsetype.text =
                messageBean.questionContent?.contentType?.let { setReplyTypeText(it) }



        //倒计时赋值
        tvCountdown.text = messageBean.questionContent?.expireTime?.let { timeParseHour(it) }.toString()

        setTimer(messageBean,tvCountdown)

        if (messageBean.questionContent?.questionStatus == 0) {
            if (messageBean.sender.senderId == userId) {
                //消息发送者是自己
                if (messageBean.questionContent?.isPublic == true) {
                    //回答方式背景
                    textResponsetype.setTextColor(context.resources.getColor(R.color.text_color_white))
                    textResponsetype.setBackgroundResource(R.drawable.textview_frame_white_roundcornor)
                    //有效期 图标
                    tvCountdown.setTextColor(context.resources.getColor(R.color.text_color_white))
                    imgCountdown.setImageResource(R.drawable.icon_countdown_send)
                    llCountDown.setBackgroundResource(R.drawable.textview_frame_white_roundcornor)

                } else {
                    textResponsetype.setTextColor(context.resources.getColor(R.color.text_color_purple_private))
                    textResponsetype.setBackgroundResource(R.drawable.textview_frame_gray_roundcornor)

                    tvCountdown.setTextColor(context.resources.getColor(R.color.text_color_origin_private))
                    imgCountdown.setImageResource(R.drawable.icon_countdown_normal)
                    llCountDown.setBackgroundResource(R.drawable.textview_frame_brown_roundcornor)
                }
            } else {
                //消息发送者为其他群员
                //回答方式背景
                textResponsetype.setTextColor(context.resources.getColor(R.color.text_color_member_type))
                textResponsetype.setBackgroundResource(R.drawable.textview_frame_member_roundcornor)
                //有效期 图标
                tvCountdown.setTextColor(context.resources.getColor(R.color.text_color_origin_private))
                imgCountdown.setImageResource(R.drawable.icon_countdown_normal)
                llCountDown.setBackgroundResource(R.drawable.textview_frame_brown_roundcornor)
            }
        } else if (messageBean.questionContent?.questionStatus == 1) {
            //已回复
            //回答方式背景
            textResponsetype.setTextColor(context.resources.getColor(R.color.frame_textview_private))
            textResponsetype.setBackgroundResource(R.drawable.textview_frame_gray2_roundcornor)
            //有效期 图标
            tvCountdown.setTextColor(context.resources.getColor(R.color.frame_textview_private))
            imgCountdown.setImageResource(R.drawable.icon_countdown_ex)
            llCountDown.setBackgroundResource(R.drawable.textview_frame_gray2_roundcornor)

        } else if (messageBean.questionContent?.questionStatus == 2) {
            //超时 的提问字体颜色不同
            textQuestion.setTextColor(resources.getColor(R.color.reward_ll_color_timeout))
            questionIcon.setImageResource(R.drawable.icon_wenti_guoqi)

            //回答方式背景
            textResponsetype.setTextColor(context.resources.getColor(R.color.frame_textview_private))
            textResponsetype.setBackgroundResource(R.drawable.textview_frame_gray2_roundcornor)
            //有效期 图标
            tvCountdown.setTextColor(context.resources.getColor(R.color.frame_textview_private))
            imgCountdown.setImageResource(R.drawable.icon_countdown_ex)
            llCountDown.setBackgroundResource(R.drawable.textview_frame_gray2_roundcornor)
            tvCountdown.text = "--"
        }
    }

    private fun setTimer(messageBean: MessageBean, view: View) {
        val countDownTimer = CountDownTimer(view,messageBean)
        // TODO: 2021/8/26 时间 
        countDownTimer.startTimer(context,-1)
    }

    private fun setReplyTypeText(type: String): String? {
        var text: String? = null
        if (type == "text")
            text = "Text"
        else if (type == "img")
            text = "Image"
        else if (type == "audio")
            text = "Voice"
        return text
    }

}