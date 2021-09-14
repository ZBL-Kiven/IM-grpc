package com.zj.imUi.items

import android.content.Context
import android.graphics.Typeface
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
import com.zj.imUi.base.BaseImItem
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imUi.utils.MessageSendTimeUtils
import com.zj.imUi.utils.RewardTimeCountdownUtils
import com.zj.imUi.utils.TimeDiffUtils
import com.zj.imUi.widget.bottom.GroupMessageItemTime
import com.zj.imUi.widget.top.GroupMessageItemTitle
import java.lang.StringBuilder
import java.util.*
import kotlin.math.roundToInt

/**
 * author: 李 祥
 * date:   2021/8/9  6:44 下午
 * description: 消息列表Item 打赏内容
 */
class IMRewardItem @JvmOverloads constructor(context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0) : BaseBubble(context, attributeSet, defStyle),
    MessageSendTimeUtils.SendTImeListener, RewardTimeCountdownUtils.CountdownListener {

    private val tvName: GroupMessageItemTitle
    private var textQuestion: AppCompatTextView
    private var textReplyType: AppCompatTextView
    private var textResponseType: AppCompatTextView
    private var imgCountdown: AppCompatImageView
    private var tvCountdown: AppCompatTextView
    private var llCountDown: LinearLayout
    private var questionIcon: AppCompatImageView
    private var timeBottom: GroupMessageItemTime
    private var tvReliedFLag: AppCompatTextView

    private var contentLayout: View = LayoutInflater.from(context)
        .inflate(R.layout.im_msg_item_owner_reward_question, this, false)

    init {
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
        contentLayout.setOnClickListener {
            data.jumpToSenderRewardsPage() //跳转到该用户的所有打赏消息
        }


        //最开始此控件均不可见
        textReplyType.visibility = View.GONE
        tvReliedFLag.visibility = View.GONE
        setTextStyle()
        setTitle(data)
        performRegisterTimer()

        //问题内容
        textQuestion.text = data.getQuestionTextContent() //当为群主视角查看未回答问题时,增加可点击textView控件
        if (data.getQuestionStatus() == 0 && data.getSelfUserId() == data.getOwnerId()) {
            textReplyType.visibility = View.VISIBLE
            val stringBuilder: StringBuilder =
                StringBuilder(context.getString(R.string.im_ui_reply_by)).append(" ")
                    .append(setReplyTypeTextUP(data.getAnswerMsgType().toString())?.toUpperCase(
                        Locale.ENGLISH))
            textReplyType.text = stringBuilder
            if (data.getPublished()) {
                textResponseType.text = context.getString(R.string.im_ui_public)
                textResponseType.setTextColor(ContextCompat.getColor(context, R.color.text_color_member_type))
                textResponseType.setBackgroundResource(R.drawable.im_msg_item_reward_pink_frame_bg)
                textReplyType.setBackgroundResource(R.drawable.im_msg_item_textview_frame_origin_roundcornor_4dp)
            } else {
                textResponseType.text = context.getString(R.string.im_ui_private)
                textResponseType.setTextColor(ContextCompat.getColor(context,
                    R.color.text_color_purple_private))
                textResponseType.setBackgroundResource(R.drawable.im_msg_item_reward_gray_frame_bg)
                textReplyType.setBackgroundResource(R.drawable.im_msg_item_textview_frame_purple_round_corner_4dp)
            }
        } else textResponseType.text =
            data.getAnswerMsgType().toString().let { setReplyTypeText(it) }

        textReplyType.setOnClickListener {
            Log.d("LiXiang", "回复TextView点击")
            data.onReplyQuestion()
        }

        tvCountdown.text = timeParseHour(data.getExpireTime()).toString()

        when {
            data.getQuestionStatus() == 0 -> {
                setWaitReply(data)
            }
            data.getQuestionStatus() == 1 -> { //已回复
                setAlreadyReplyBg()
            }
            data.getQuestionStatus() == 2 -> {
                setOutTimeBg()
            }
        } //底部时间及奖励展示
        if (data.getSelfUserId() == data.getSenderId()) {
            timeBottom.visibility = View.VISIBLE
            timeBottom.setData(data)
        } else timeBottom.visibility = View.GONE

    }

    private fun setTextStyle() {
        tvReliedFLag.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        textQuestion.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        tvCountdown.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        textReplyType.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        textResponseType.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
    }

    private fun setWaitReply(data: ImMsgIn) {
        questionIcon.setImageResource(R.drawable.im_msg_item_widget_reward_icon_question_normal)
        if (data.getSenderId() == data.getSelfUserId()) {      //消息发送者是自己
            if (data.getPublished()) {
                textQuestion.setTextColor(ContextCompat.getColor(context,
                    R.color.text_color_white)) //回答方式背景
                textResponseType.setTextColor(ContextCompat.getColor(context,
                    R.color.text_color_white))
                textResponseType.setBackgroundResource(R.drawable.im_msg_item_reward_white_frame_bg) //有效期 图标
                tvCountdown.setTextColor(ContextCompat.getColor(context, R.color.text_color_white))
                imgCountdown.setImageResource(R.drawable.im_msg_item_widget_reward_countdown_white)
                llCountDown.setBackgroundResource(R.drawable.im_msg_item_reward_white_frame_bg)

            } else {
                textQuestion.setTextColor(ContextCompat.getColor(context, R.color.text_color_black))
                textResponseType.setTextColor(ContextCompat.getColor(context,
                    R.color.text_color_purple_private))
                textResponseType.setBackgroundResource(R.drawable.im_msg_item_reward_gray_frame_bg)

                tvCountdown.setTextColor(ContextCompat.getColor(context,
                    R.color.text_color_origin_private))
                imgCountdown.setImageResource(R.drawable.im_msg_item_widget_reward_countdown_origin)
                llCountDown.setBackgroundResource(R.drawable.im_msg_item_textview_frame_brown_roundcornor)
            }

        } else { //消息发送者为其他群员
            textQuestion.setTextColor(ContextCompat.getColor(context, R.color.text_color_black))
            if (data.getSelfUserId() != data.getOwnerId()) {
                textResponseType.setTextColor(ContextCompat.getColor(context,
                    R.color.text_color_member_type))
                textResponseType.setBackgroundResource(R.drawable.im_msg_item_reward_pink_frame_bg) //有效期 图标
            }
            tvCountdown.setTextColor(ContextCompat.getColor(context,
                R.color.text_color_origin_private))
            imgCountdown.setImageResource(R.drawable.im_msg_item_widget_reward_countdown_origin)
            llCountDown.setBackgroundResource(R.drawable.im_msg_item_textview_frame_brown_roundcornor)
        }

        if (data.getExpireTime() in 1..3599999) {
            llCountDown.setBackgroundResource(R.drawable.im_msg_item_reward_red_frame_bg)
            llCountDown.visibility = View.VISIBLE
        }

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
        questionIcon.setImageResource(R.drawable.im_msg_item_widget_reward_icon_question_gray) //回答方式背景
        textResponseType.setTextColor(ContextCompat.getColor(context,
            R.color.frame_textview_private))
        textResponseType.setBackgroundResource(R.drawable.im_msg_item_reward_gray2_frame_bg) //有效期 图标
        tvCountdown.setTextColor(ContextCompat.getColor(context, R.color.frame_textview_private))
        imgCountdown.setImageResource(R.drawable.im_msg_item_widget_reward_countdown_gray)
        llCountDown.setBackgroundResource(R.drawable.im_msg_item_reward_gray2_frame_bg)
        llCountDown.visibility = View.VISIBLE
        tvCountdown.text = context.getString(R.string.im_ui_question_no_time)
    }

    private fun setAlreadyReplyBg() { //回答方式背景
        textQuestion.setTextColor(ContextCompat.getColor(context, R.color.text_color_black))
        questionIcon.setImageResource(R.drawable.im_msg_item_widget_reward_icon_question_normal)
        textResponseType.setTextColor(ContextCompat.getColor(context,
            R.color.frame_textview_private))
        textResponseType.setBackgroundResource(R.drawable.im_msg_item_reward_gray2_frame_bg) //有效期 图标
        tvCountdown.setTextColor(ContextCompat.getColor(context, R.color.frame_textview_private))
        imgCountdown.setImageResource(R.drawable.im_msg_item_widget_reward_countdown_gray)
        llCountDown.setBackgroundResource(R.drawable.im_msg_item_reward_gray2_frame_bg)
        llCountDown.visibility = View.GONE
        tvReliedFLag.visibility = View.VISIBLE
        tvCountdown.text = context.getString(R.string.im_ui_question_no_time)
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

    private fun setReplyTypeTextUP(type: String): String? {
        var text: String? = null
        when (type) {
            UiMsgType.MSG_TYPE_TEXT -> text = context.getString(R.string.im_ui_msg_reward_type_text)
            UiMsgType.MSG_TYPE_IMG -> text = context.getString(R.string.im_ui_msg_reward_type_image)
            UiMsgType.MSG_TYPE_AUDIO -> text =
                context.getString(R.string.im_ui_msg_reward_type_audio)
        }
        return text
    }

    private fun performRegisterTimer() {
        curData?.invoke()?.let {
            if (it.getSendState() == 0 || it.getSendState() == 3) {
                RewardTimeCountdownUtils.registerCountdownObserver(it.getMsgId(),
                    it.getExpireTime(),
                    this)
                TimeDiffUtils.timeDifference(it.getSendTime())?.let { it1 ->
                    MessageSendTimeUtils.registerSendTimeObserver(it.getMsgId(), it1, this)
                }
            } else {
                RewardTimeCountdownUtils.unRegisterCountdownObserver(it.getMsgId())
                MessageSendTimeUtils.unRegisterSendTImeObserver(it.getMsgId())
            }
        }
    }

    override fun onResume() {
        performRegisterTimer()
    }

    override fun onStop() {
        curData?.invoke()?.let {
            RewardTimeCountdownUtils.unRegisterCountdownObserver(it.getMsgId())
            MessageSendTimeUtils.unRegisterSendTImeObserver(it.getMsgId())
        }
    }

    override fun onDestroy() {
        removeAllViews()
    }

    override fun notifyChange(pl: Any?) {
        super.notifyChange(pl)
        when (pl) {
            BaseImItem.NOTIFY_CHANGE_REWARD_STATE -> {
                Log.e("li_xiang_reward", "IMR_reward_change") //过期改变打赏状态
                curData?.invoke()?.questionStatusOverdueChange()
                curData?.invoke()?.let {
                    setTitle(it)
                    timeBottom.setData(it)
                } //重绘
                invalidate()
            }
            BaseImItem.NOTIFY_CHANGE_SENDING_STATE -> {
                performRegisterTimer()
            }
        }
    }

    override fun onCountdown(msgId: String, remainingTime: Long) {
        if (this.curData?.invoke()?.getQuestionStatus() == 0) {
            if (msgId == this.curData?.invoke()?.getMsgId()) tvCountdown.text =
                timeParseHour(remainingTime)
            when {
                remainingTime in 1..3599999 -> {
                    llCountDown.setBackgroundResource(R.drawable.im_msg_item_reward_red_frame_bg)
                }
                remainingTime < 1 -> {
                    setOutTimeBg()
                    textReplyType.visibility = View.GONE
                    notifyChange(BaseImItem.NOTIFY_CHANGE_REWARD_STATE)
                }
                else -> {
                    this.curData?.invoke()?.let { setWaitReply(it) }
                }
            }
        }
    }

    override fun onSendTime(msgId: String, sendTime: Long) {
        if (msgId == this.curData?.invoke()?.getMsgId()) {
            this.curData?.invoke()?.let { timeBottom.setDataWithTime(sendTime, it) }
        }
    }

    private fun timeParseHour(duration: Long): String? {
        if (duration < 1) return context.getString(R.string.im_ui_question_no_time)
        var time: String? = ""
        val hour = duration / 3600000
        val minutes = duration % 3600000
        var minute = (minutes.toFloat() / 60000).roundToInt()
        if (hour < 1) {
            time += 0
        }
        time += "$hour:"
        if (minute < 10) {
            time += "0"
        } else if (minute == 60) {
            minute = 59
        }
        time += minute
        return time
    }
}