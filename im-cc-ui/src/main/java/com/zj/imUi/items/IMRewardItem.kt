package com.zj.imUi.items

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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
import com.zj.imUi.utils.MessageReplySendTimeUtils
import com.zj.imUi.utils.TimeDiffUtils
import com.zj.imUi.widget.top.GroupMessageItemTitle
import com.zj.views.ut.DPUtils
import java.util.*

/**
 * author: 李 祥
 * date:   2021/8/9  6:44 下午
 * description: 消息列表Item 打赏内容
 */
class IMRewardItem @JvmOverloads constructor(context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0) : BaseBubble(context, attributeSet, defStyle),
    MessageSendTimeUtils.SendTImeListener, MessageReplySendTimeUtils.SendTImeListener {

    private val tvName: GroupMessageItemTitle
    private var textQuestion: AppCompatTextView
    private var textIsPublic: AppCompatTextView
    private var textReplyType: AppCompatTextView
    private var textResponseType: AppCompatTextView
    private var textReplyOwnerNickName: AppCompatTextView
    private var textReplyOwnerSendTime: AppCompatTextView

    private var llQuestion: LinearLayout
    private var llReplyContentUser: LinearLayout
    private var llReplyContentOwner: LinearLayout
    private var replyContent: FrameLayout
    private var replyOwnerContent: FrameLayout
    private var llQuestionType: LinearLayout
    private var questionIcon: AppCompatImageView

    private var tvReliedFLag: AppCompatTextView
    private val baseContentMargins = DPUtils.dp2px(12f)
    private var frameFLag: FrameLayout
    private var curContentIn: ImContentIn? = null


    enum class RewardMsgState(val type: Int){
        WAIT_REPLY(0),
        ALREADY_REPLIED(1),
        REJECTED(2)
    }
    private var contentLayout: View = LayoutInflater.from(context)
        .inflate(R.layout.im_msg_item_owner_reward_question, this, false)

    init {
        with(contentLayout) {
            tvName = findViewById(R.id.im_msg_item_owner_reward_question_title)
            textQuestion = findViewById(R.id.im_msg_item_owner_reward_question_tv_content)
            textResponseType = findViewById(R.id.im_msg_item_owner_reward_question_tv_type)
            textReplyOwnerNickName = findViewById(R.id.im_msg_item_owner_tv_nickname)
            textReplyOwnerSendTime = findViewById(R.id.im_msg_item_owner_tv_reply_time)
            textIsPublic = findViewById(R.id.im_msg_item_owner_reward_question_tv_is_public)
            replyContent = findViewById(R.id.im_msg_item_user_frame_reply_content)
            replyOwnerContent = findViewById(R.id.im_msg_item_owner_frame_reply_content)
            llQuestionType = findViewById(R.id.im_msg_item_owner_reward_ll_type)

            questionIcon = findViewById(R.id.im_msg_item_owner_reward_question_icon)
            textReplyType = findViewById(R.id.im_msg_item_owner_reward_question_tv_reply_type)
            tvReliedFLag = findViewById(R.id.im_msg_item_owner_reward_widget_replied_flag)
            llQuestion = findViewById(R.id.im_msg_item_owner_reward_question_il)
            frameFLag = findViewById(R.id.im_msg_item_owner_reward_frame)

            llReplyContentUser = findViewById(R.id.im_msg_item_reward_ll_reply_content)
            llReplyContentOwner = findViewById(R.id.im_msg_item_owner_ll_reply_content)
        }
    }

    override fun init(data: ImMsgIn, chatType: Any) {
        if (childCount == 0) {
            addView(contentLayout)
        }
        if (chatType == 3 && data.getSelfUserId() == data.getSenderId()) {
            contentLayout.layoutParams =
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            val lp = textReplyType.layoutParams as LinearLayout.LayoutParams
            lp.width = LayoutParams.MATCH_PARENT
            lp.marginStart = DPUtils.dp2px(12f)
            lp.marginEnd = DPUtils.dp2px(12f)
            textReplyType.layoutParams = lp
        }
        llReplyContentOwner.visibility = View.GONE
        llReplyContentUser.visibility = View.GONE
        textReplyType.visibility = View.GONE
        tvReliedFLag.visibility = View.GONE
        llQuestionType.visibility = View.VISIBLE
        questionIcon.visibility = View.VISIBLE
        textReplyOwnerSendTime.visibility = View.GONE


        setTitle(data, chatType)
        if (chatType == UiMsgType.GROUP_CHAT || chatType == UiMsgType.PICK_FANS_USER_CHAT || (data.getSelfUserId() == data.getOwnerId() && data.getQuestionStatus() == 0)) {
            setChatRewardItem(data)
        } else setPrivateChatItem(data)

    }

    private fun setPrivateChatItem(data: ImMsgIn) {
        textQuestion.text = data.getQuestionTextContent()
        if (data.getSelfUserId() == data.getSenderId()) {
            textQuestion.setTextColor(ContextCompat.getColor(context,
                R.color.im_msg_bg_color_white))
        } else textQuestion.setTextColor(ContextCompat.getColor(context,
            R.color.im_msg_text_color_black))
        llQuestionType.visibility = View.GONE
        questionIcon.visibility = View.GONE
        if (data.getSenderId() == data.getSelfUserId()) llQuestion.setPadding(baseContentMargins,
            baseContentMargins,
            baseContentMargins,
            baseContentMargins)
        else llQuestion.setPadding(baseContentMargins,
            DPUtils.dp2px(8f),
            baseContentMargins,
            baseContentMargins) //普通消息变为自适应宽度
        contentLayout.layoutParams =
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        frameFLag.visibility = View.GONE
    }

    private fun setChatRewardItem(data: ImMsgIn) {
        contentLayout.setOnClickListener {
            data.jumpToSenderRewardsPage() //跳转到该用户的所有打赏消息，私聊
        }
        setTextStyle()
        performRegisterTimer() //设置回复方式
        textResponseType.text = data.getAnswerMsgType().toString().let { setReplyTypeText(it) }
        textResponseType.visibility = View.VISIBLE

        if (data.getSenderId() == data.getSelfUserId()) llQuestion.setPadding(baseContentMargins,
            DPUtils.dp2px(4f),
            baseContentMargins,
            0)
        else llQuestion.setPadding(baseContentMargins, DPUtils.dp2px(4f), baseContentMargins, 0)

        //问题内容
        textQuestion.text = data.getQuestionTextContent() //当为群主视角查看未回答问题时,增加可点击textView控件
        val messageNormal = data.getSendState() == 0 || data.getSendState() == 3
        if(data.getMsgIsReject() == true){
            //调整Flag位置
            val lp = FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
            lp.gravity = Gravity.END
            lp.setMargins(0,DPUtils.dp2px(-60f),DPUtils.dp2px(10f),10)
            tvReliedFLag.layoutParams = lp
            tvReliedFLag.setBackgroundResource(R.drawable.im_msg_item_rejected_flag_cornor_bg)
            tvReliedFLag.setTextColor(Color.parseColor("#FFFF3B30"))
            tvReliedFLag.visibility = View.VISIBLE
        }else if (data.getQuestionStatus() == 0 && messageNormal) {
            when {
                data.getSelfUserId() == data.getOwnerId() -> {
                    textReplyType.visibility = View.VISIBLE
                    textReplyType.text = setReplyTypeTextUP(data.getAnswerMsgType()
                        .toString())?.uppercase(Locale.getDefault())
                    textReplyType.setOnClickListener {
                        data.onReplyQuestion()
                    }
                }
                data.getSenderId() == data.getSelfUserId() -> {
                    textReplyType.visibility = View.VISIBLE
                    textReplyType.text = context.getString(R.string.im_ui_msg_button_recall)
                        .uppercase(Locale.getDefault())
                    textReplyType.setOnClickListener {
                        data.userRetractRewardMsg()
                    }
                }
                else -> {
                    textReplyType.visibility = View.GONE
                }
            }
            llReplyContentUser.visibility = View.GONE
            llReplyContentOwner.visibility = View.GONE
        } else if (data.getQuestionStatus() == 1) { //已回复状态
            textReplyType.visibility = View.GONE
            if (data.getSelfUserId() == data.getOwnerId()) {
                setReplyContent(data, replyOwnerContent)
                llReplyContentOwner.visibility = View.VISIBLE
            } else {
                llReplyContentUser.visibility = View.VISIBLE
                textReplyOwnerNickName.text = data.getAnswerContentSenderName()
                setReplyContent(data, replyContent)
            }
        }

        if (data.getSelfUserId() == data.getOwnerId()) {
            textResponseType.visibility = View.GONE
        }

        //textReplyType属性设置
        if (data.getPublished()) {
            textIsPublic.text = context.getString(R.string.im_ui_public)
            if (data.getSelfUserId() == data.getOwnerId()) {
                textReplyType.setBackgroundResource(R.drawable.im_msg_item_textview_frame_origin_roundcornor_4dp)
                textReplyType.setTextColor(ContextCompat.getColor(context,
                    R.color.im_msg_text_color_white))
            } else {
                textReplyType.setBackgroundResource(R.drawable.im_msg_item_textview_frame_white_roundcornor_4dp)
                textReplyType.setTextColor(ContextCompat.getColor(context,
                    R.color.im_msg_bg_origin))
            }
        } else {
            textIsPublic.text = context.getString(R.string.im_ui_private)
            textReplyType.setBackgroundResource(R.drawable.im_msg_item_textview_frame_purple_round_corner_4dp)
            textReplyType.setTextColor(ContextCompat.getColor(context,
                R.color.im_msg_text_color_white))
        }


        when {
            data.getMsgIsReject() == true ->{
                setAlreadyReplyBg(RewardMsgState.REJECTED.type)
            }
            data.getQuestionStatus() == 0 -> {
                setWaitReply(data)
            }
            data.getQuestionStatus() == 1 -> { //已回复
                setAlreadyReplyBg(RewardMsgState.ALREADY_REPLIED.type)
            }
        }
        if (data.getSelfUserId() == data.getOwnerId()) {
            val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT)
            lp.setMargins(0, DPUtils.dp2px(-60f), DPUtils.dp2px(10f), DPUtils.dp2px(10f))
            lp.gravity = Gravity.END
            tvReliedFLag.layoutParams = lp
        }
    }

    private fun setReplyContent(data: ImMsgIn, frameLayout: FrameLayout) {
        var isSameType = false
        try {
            val v: View = when (data.getAnswerMsgType()) {
                UiMsgType.MSG_TYPE_TEXT -> {
                    isSameType = curContentIn is IMContentTextView
                    if (isSameType) null else IMContentTextView(context)
                }

                UiMsgType.MSG_TYPE_IMG -> {
                    isSameType = curContentIn is IMContentImageView
                    if (isSameType) null else IMContentImageView(context)
                }

                UiMsgType.MSG_TYPE_AUDIO -> {
                    isSameType = curContentIn is IMContentAudioView
                    if (isSameType) null else IMContentAudioView(context)
                }
                else -> null
            } ?: return

            if (!isSameType) {
                frameLayout.removeAllViews()
                curContentIn = v as? ImContentIn
                frameLayout.addView(v,
                    LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
                if (data.getAnswerMsgType() == UiMsgType.MSG_TYPE_IMG) {
                    frameLayout.setOnClickListener {
                        data.onViewLargePic()
                    }
                }
            }
        } finally {
            curContentIn?.onSetData(data)
        }
    }

    private fun setTextStyle() {
        tvReliedFLag.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        tvReliedFLag.paint.isFakeBoldText = true
        textQuestion.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        textIsPublic.typeface =
            Typeface.defaultFromStyle(Typeface.BOLD) //        tvCountdown.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        textReplyType.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        textResponseType.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        textReplyOwnerNickName.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
    }

    private fun setWaitReply(data: ImMsgIn) { //未回复
        questionIcon.setImageResource(R.drawable.im_msg_item_widget_reward_icon_question_normal)
        if (data.getSenderId() == data.getSelfUserId()) {      //消息发送者是自己
            if (data.getPublished()) {
                textQuestion.setTextColor(ContextCompat.getColor(context,
                    R.color.im_msg_text_color_white)) //回答方式背景
                textResponseType.setTextColor(ContextCompat.getColor(context,
                    R.color.im_msg_text_color_white))
                textResponseType.setBackgroundResource(R.drawable.im_msg_item_reward_white_frame_bg)
                textIsPublic.setBackgroundResource(R.drawable.im_msg_item_reward_white_frame_bg)
                textIsPublic.setTextColor(ContextCompat.getColor(context,
                    R.color.im_msg_bg_color_white))

            } else {
                textQuestion.setTextColor(ContextCompat.getColor(context,
                    R.color.im_msg_text_color_black))
                textResponseType.setTextColor(ContextCompat.getColor(context,
                    R.color.im_msg_text_color_purple_private))
                textResponseType.setBackgroundResource(R.drawable.im_msg_item_reward_gray_frame_bg)
                textIsPublic.setBackgroundResource(R.drawable.im_msg_item_reward_gray_frame_bg)
                textIsPublic.setTextColor(ContextCompat.getColor(context,
                    R.color.im_msg_text_color_purple_private)) //                imgCountdown.setImageResource(R.drawable.im_msg_item_widget_reward_countdown_origin)
            }
        } else { //消息发送者为其他群员，且自己为大V才能看到打赏消息
            if (data.getPublished()) {
                textQuestion.setTextColor(ContextCompat.getColor(context,
                    R.color.im_msg_text_color_black))
                textResponseType.setTextColor(ContextCompat.getColor(context,
                    R.color.im_msg_text_color_member_type))
                textResponseType.setBackgroundResource(R.drawable.im_msg_item_reward_pink_frame_bg)
                textIsPublic.setTextColor(ContextCompat.getColor(context,
                    R.color.im_msg_text_color_origin_private))
                textIsPublic.setBackgroundResource(R.drawable.im_msg_item_reward_pink_frame_bg)
            } else {
                textQuestion.setTextColor(ContextCompat.getColor(context,
                    R.color.im_msg_text_color_black))
                textResponseType.setTextColor(ContextCompat.getColor(context,
                    R.color.im_msg_text_color_purple_private))
                textResponseType.setBackgroundResource(R.drawable.im_msg_item_reward_gray_frame_bg)
                textIsPublic.setBackgroundResource(R.drawable.im_msg_item_reward_gray_frame_bg)
                textIsPublic.setTextColor(ContextCompat.getColor(context,
                    R.color.im_msg_text_color_purple_private))
            }
        }
    }

    private fun setTitle(data: ImMsgIn, chatType: Any) {
        if (data.getSelfUserId() != data.getOwnerId() && chatType == 2) {
            tvName.visibility = View.GONE
        } else {
            tvName.visibility = View.VISIBLE
            tvName.setData(data, chatType)
        }
    }


    private fun setAlreadyReplyBg(type:Int) { //回答方式背景
        textQuestion.setTextColor(ContextCompat.getColor(context, R.color.im_msg_text_color_black))
        questionIcon.visibility = View.VISIBLE
        questionIcon.setImageResource(R.drawable.im_msg_item_widget_reward_icon_question_normal)
        textResponseType.setTextColor(ContextCompat.getColor(context,
            R.color.im_msg_frame_textview_private))
        textResponseType.setBackgroundResource(R.drawable.im_msg_item_reward_gray2_frame_bg)
        textIsPublic.setBackgroundResource(R.drawable.im_msg_item_reward_gray2_frame_bg)
        textIsPublic.setTextColor(ContextCompat.getColor(context,
            R.color.im_msg_frame_textview_private))
        if(type == RewardMsgState.ALREADY_REPLIED.type){
            tvReliedFLag.text = context.getString(R.string.im_ui_replied)
        }else if (type == RewardMsgState.REJECTED.type){
            tvReliedFLag.text = context.getString(R.string.im_ui_rejected)
        }
        tvReliedFLag.visibility = View.VISIBLE
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
                TimeDiffUtils.timeDifference(it.getSendTime())?.let { it1 ->
                    MessageSendTimeUtils.registerSendTimeObserver(it.getMsgId(), it1, this)
                }
                it.getAnswerContentSendTime()?.let { it1 ->
                    TimeDiffUtils.timeDifference(it1)?.let { it1 ->
                        MessageReplySendTimeUtils.registerSendTimeObserver(it.getMsgId(), it1, this)
                    }
                }
            } else {
                MessageSendTimeUtils.unRegisterSendTImeObserver(it.getMsgId())
                MessageReplySendTimeUtils.unRegisterSendTImeObserver(it.getMsgId())
            }
        }
    }

    override fun onResume() {
        curContentIn?.onResume(curData?.invoke())
        performRegisterTimer()
    }

    override fun onStop() {
        curContentIn?.onStop(curData?.invoke())
        curData?.invoke()?.let {
            MessageSendTimeUtils.unRegisterSendTImeObserver(it.getMsgId())
            MessageReplySendTimeUtils.unRegisterSendTImeObserver(it.getMsgId())
        }
    }

    override fun onDestroy() {
        curContentIn?.onDestroy(curData?.invoke())
        curContentIn = null
        removeAllViews()
    }

    override fun notifyChange(pl: Any?) {
        super.notifyChange(pl)
        when (pl) {
            BaseImItem.NOTIFY_CHANGE_REWARD_STATE -> {

            }
            BaseImItem.NOTIFY_CHANGE_SENDING_STATE -> {
                performRegisterTimer()
            }
        }
    }


    override fun onSendTime(msgId: String, sendTime: Long) {
        if (msgId == this.curData?.invoke()?.getMsgId()) {
            tvName.setDataSendTime(sendTime)
        }
    }

    override fun onReplySendTime(msgId: String, sendTime: Long) {
        if (msgId == this.curData?.invoke()?.getMsgId()) {
            textReplyOwnerSendTime.text = TimeDiffUtils.setTimeText(sendTime, context)
        }
    }
}

