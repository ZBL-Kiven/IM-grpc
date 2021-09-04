package com.zj.imUi.items

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.zj.imUi.UiMsgType
import com.zj.imUi.R
import com.zj.imUi.base.BaseBubble
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imUi.utils.RewardTimeCountdownUtils
import com.zj.imUi.widget.bottom.GroupMessageItemTime
import com.zj.imUi.widget.top.GroupMessageItemTitle
import java.lang.StringBuilder
import kotlin.math.roundToInt

/**
 * author: 李 祥
 * date:   2021/8/9  6:44 下午
 * description: 消息列表Item 打赏内容
 */
class IMRewardItem @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : BaseBubble(context, attributeSet, defStyle), RewardTimeCountdownUtils.CountdownListener {

    private val tvName: GroupMessageItemTitle
    private var textQuestion: AppCompatTextView
    private var textReplyType: AppCompatTextView
    private var textResponseType: AppCompatTextView
    private var imgCountdown: AppCompatImageView
    private var tvCountdown: AppCompatTextView
    private var llCountDown: LinearLayout
    private var questionIcon: AppCompatImageView
    private var timeBottom: GroupMessageItemTime
    private var tvReliedFLag:AppCompatTextView

    private var contentLayout: View

    init {
        contentLayout = LayoutInflater.from(context).inflate(R.layout.im_msg_item_owner_reward_question, this, false)
        with(contentLayout) {
            tvName = findViewById(R.id.im_msg_item_owner_reward_question_title)
            textQuestion = findViewById(R.id.im_msg_item_owner_reward_question_tv_content)
            textResponseType = findViewById(R.id.im_msg_item_owner_reward_question_tv_type)
            imgCountdown = findViewById(R.id.im_msg_item_owner_reward_question_img_countdown)
            tvCountdown = findViewById(R.id.im_msg_item_owner_reward_question_tv_countdown)
            llCountDown = findViewById(R.id.im_msg_item_owner_reward_question_ll_countdown)
            questionIcon = findViewById(R.id.im_msg_item_owner_reward_question_icon)
            textReplyType = findViewById(R.id.im_msg_item_owner_reward_question_tv_reply_type)
            timeBottom = findViewById(R.id.im_msg_item_owner_reward_question_bottom_time)
            tvReliedFLag = findViewById(R.id.im_msg_item_owner_reward_widget_replied_flag)
        }
    }

    override fun init(data: ImMsgIn) {

        if (childCount == 0) {
            addView(contentLayout)
        }

        //最开始此控件均不可见
        textReplyType.visibility = View.GONE
        tvReliedFLag.visibility = View.GONE
        setTitle(data)

        //问题内容
        textQuestion.text = data.getQuestionTextContent()
        //当为群主视角查看未回答问题时,增加可点击textView控件
        if (data.getQuestionStatus() == 0 && data.getSelfUserId() == data.getOwnerId()) {
            textReplyType.visibility = View.VISIBLE
            val stringBuilder: StringBuilder = StringBuilder(context.getString(R.string.im_ui_reply_by)).append(" ").append(data.getAnswerMsgType().toString().uppercase())
            textReplyType.text = stringBuilder
            if (data.getPublished()) {
                textResponseType.text = context.getString(R.string.im_ui_public)
                textResponseType.setTextColor(ContextCompat.getColor(context, R.color.text_color_member_type))
                textResponseType.setBackgroundResource(R.drawable.im_msg_item_reward_pink_frame_bg)
                textReplyType.setBackgroundResource(R.drawable.textview_frame_origin_roundcornor_4dp)
            } else {
                textResponseType.text = context.getString(R.string.im_ui_private)
                textResponseType.setTextColor(ContextCompat.getColor(context, R.color.text_color_purple_private))
                textResponseType.setBackgroundResource(R.drawable.im_msg_item_reward_gray_frame_bg)
                textReplyType.setBackgroundResource(R.drawable.textview_frame_purple_round_corner_4dp)
            }
        } else textResponseType.text = data.getAnswerMsgType().toString().let { setReplyTypeText(it) }
        textReplyType.setOnClickListener {
            data.onReplyQuestion()
        }


        if (data.getQuestionStatus() == 0) {
            if (data.getSenderId() == data.getSelfUserId()) {      //消息发送者是自己
                if (data.getPublished()) {
                    textQuestion.setTextColor(ContextCompat.getColor(context, R.color.text_color_white))
                    //回答方式背景
                    textResponseType.setTextColor(ContextCompat.getColor(context, R.color.text_color_white))
                    textResponseType.setBackgroundResource(R.drawable.im_msg_item_reward_white_frame_bg) //有效期 图标
                    tvCountdown.setTextColor(ContextCompat.getColor(context, R.color.text_color_white))
                    imgCountdown.setImageResource(R.drawable.icon_countdown_send)
                    llCountDown.setBackgroundResource(R.drawable.im_msg_item_reward_white_frame_bg)

                } else {
                    textQuestion.setTextColor(ContextCompat.getColor(context, R.color.text_color_black))
                    textResponseType.setTextColor(ContextCompat.getColor(context, R.color.text_color_purple_private))
                    textResponseType.setBackgroundResource(R.drawable.im_msg_item_reward_gray_frame_bg)

                    tvCountdown.setTextColor(ContextCompat.getColor(context, R.color.text_color_origin_private))
                    imgCountdown.setImageResource(R.drawable.icon_countdown_normal)
                    llCountDown.setBackgroundResource(R.drawable.textview_frame_brown_roundcornor)
                }

            } else { //消息发送者为其他群员
                textQuestion.setTextColor(ContextCompat.getColor(context, R.color.text_color_black))
                if (data.getSelfUserId() != data.getOwnerId()) {
                    textResponseType.setTextColor(ContextCompat.getColor(context, R.color.text_color_member_type))
                    textResponseType.setBackgroundResource(R.drawable.im_msg_item_reward_pink_frame_bg) //有效期 图标
                }
                tvCountdown.setTextColor(ContextCompat.getColor(context, R.color.text_color_origin_private))
                imgCountdown.setImageResource(R.drawable.icon_countdown_normal)
                llCountDown.setBackgroundResource(R.drawable.textview_frame_brown_roundcornor)
            }
        } else if (data.getQuestionStatus() == 1) { //已回复
            //回答方式背景
            textQuestion.setTextColor(ContextCompat.getColor(context, R.color.text_color_black))
            textResponseType.setTextColor(ContextCompat.getColor(context, R.color.frame_textview_private))
            textResponseType.setBackgroundResource(R.drawable.im_msg_item_reward_gray2_frame_bg) //有效期 图标
            tvCountdown.setTextColor(ContextCompat.getColor(context, R.color.frame_textview_private))
            imgCountdown.setImageResource(R.drawable.icon_countdown_ex)
            llCountDown.setBackgroundResource(R.drawable.im_msg_item_reward_gray2_frame_bg)
            tvReliedFLag.visibility = View.VISIBLE

        } else if (data.getQuestionStatus() == 2) {
            setOutTimeBg()
        }

        tvCountdown.text = timeParseHour(data.getExpireTime()).toString()
        if (data.getSelfUserId() == data.getSenderId()) {
            timeBottom.visibility = View.VISIBLE
            timeBottom.setData(data)
        }else timeBottom.visibility = View.GONE

    }

    private fun setTitle(data: ImMsgIn) {
        tvName.visibility = View.VISIBLE
        if (data.getSelfUserId() == data.getSenderId()) tvName.visibility = View.GONE
        else {
            tvName.setData(data)
            tvName.visibility = View.VISIBLE
        }
    }


    private fun setOutTimeBg() { //超时 的提问字体颜色不同
        textQuestion.setTextColor(ContextCompat.getColor(context, R.color.reward_ll_color_timeout))
        questionIcon.setImageResource(R.drawable.icon_wenti_guoqi)

        //回答方式背景
        textResponseType.setTextColor(ContextCompat.getColor(context, R.color.frame_textview_private))
        textResponseType.setBackgroundResource(R.drawable.im_msg_item_reward_gray2_frame_bg) //有效期 图标
        tvCountdown.setTextColor(ContextCompat.getColor(context, R.color.frame_textview_private))
        imgCountdown.setImageResource(R.drawable.icon_countdown_ex)
        llCountDown.setBackgroundResource(R.drawable.im_msg_item_reward_gray2_frame_bg)
        tvCountdown.text = "- -"
    }

    private fun setReplyTypeText(type: String): String? {
        var text: String? = null
        when (type) {
            UiMsgType.MSG_TYPE_TEXT -> text = context.getString(R.string.im_ui_msg_type_text)
            UiMsgType.MSG_TYPE_IMG -> text = context.getString(R.string.im_ui_msg_type_image)
            UiMsgType.MSG_TYPE_AUDIO -> text = context.getString(R.string.im_ui_msg_type_audio)
        }
        return text
    }

    override fun onResume() {
        curData?.invoke()?.let {
            RewardTimeCountdownUtils.registerCountdownObserver(it.getMsgId(), it.getExpireTime(), this)
        }
    }

    override fun onStop() {
        curData?.invoke()?.let { RewardTimeCountdownUtils.unRegisterCountdownObserver(it.getMsgId()) }
    }

    override fun onDestroy() {
        removeAllViews()
    }

    override fun onCountdown(msgId: String, remainingTime: Long) {
        if (msgId == this.curData?.invoke()?.getMsgId()) tvCountdown.text = timeParseHour(remainingTime)
        if (remainingTime<1) curData?.invoke()?.getMsgId()?.let { RewardTimeCountdownUtils.unRegisterCountdownObserver(it) }
    }

    private fun timeParseHour(duration: Long): String? {
        if(duration<1){
            return "- -"
        }
        var time: String? = ""
        val hour = duration / 3600000
        val minutes = duration % 3600000
        val minute = (minutes.toFloat() / 60000).roundToInt()
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
}