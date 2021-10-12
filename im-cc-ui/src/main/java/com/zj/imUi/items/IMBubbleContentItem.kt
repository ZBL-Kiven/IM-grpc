package com.zj.imUi.items

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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
import com.zj.imUi.utils.TimeDiffUtils
import com.zj.imUi.widget.GroupRewardOwnerMeItem
import com.zj.imUi.widget.MsgPop
import com.zj.views.ut.DPUtils


class IMBubbleContentItem @JvmOverloads constructor(context: Context,
    attrs: AttributeSet? = null,
    def: Int = 0) : BaseBubble(context, attrs, def), MessageSendTimeUtils.SendTImeListener {

    private val tvName: AppCompatTextView
    private val iconIsOwner: AppCompatImageView
    private val iconV :AppCompatImageView
    private val llQuestionContent: LinearLayout
    private val llName: LinearLayout
    private val bubbleContent: FrameLayout
    private val bubbleRepliedContent: FrameLayout
    private val tvQuestionName: AppCompatTextView
    private val tvFlag: AppCompatTextView
    private val tvQuestionContent: AppCompatTextView
    private val imgQuestion: AppCompatImageView
    private val imgReply: AppCompatImageView
    private val timeBottom: GroupRewardOwnerMeItem
    private val llContent: LinearLayout
    private var curContentIn: ImContentIn? = null
    private var curContentInReply: ImContentIn? = null
    private val baseContentMargins = DPUtils.dp2px(12f)

    init {
        LayoutInflater.from(context).inflate(R.layout.im_msg_bubble_content, this, true)
        tvName = findViewById(R.id.im_msg_item_normal_text_tv_nickname)
        iconIsOwner = findViewById(R.id.im_msg_bubble_img_owner)
        iconV = findViewById(R.id.im_msg_bubble_img_v)
        bubbleContent = findViewById(R.id.im_msg_bubble_viewstub)
        bubbleRepliedContent = findViewById(R.id.im_msg_bubble_replied_stub)
        tvQuestionContent = findViewById(R.id.im_msg_item_normal_text_replied_content)
        tvFlag = findViewById(R.id.im_msg_item_normal_text_replied_tv_flag)
        tvQuestionName =
            findViewById(R.id.im_msg_item_normal_text_replied_tv_nickname) //question Icon
        imgQuestion = findViewById(R.id.im_msg_item_normal_icon_question)
        imgReply = findViewById(R.id.im_msg_item_normal_icon_reply)
        timeBottom = findViewById(R.id.im_msg_item_normal_text_replied_time)
        llQuestionContent = findViewById(R.id.im_msg_bubble_content_ll_question)
        llName = findViewById(R.id.im_msg_bubble_ll_title)
        llContent = findViewById(R.id.im_msg_bubble_img_ll_content)
    }

    override fun init(data: ImMsgIn,chatType:Any) {
        tvName.text = data.getSenderName()
        imgQuestion.visibility = View.GONE
        imgReply.visibility = View.GONE
        if (data.getSelfUserId() == data.getSenderId()) {
            llName.visibility = View.GONE
        } else {
            llName.visibility = View.VISIBLE
            if (data.getSenderId() == data.getOwnerId()){
                iconIsOwner.visibility = View.VISIBLE
                iconV.visibility = View.VISIBLE
            }
            else{
                iconIsOwner.visibility = View.GONE
                iconV.visibility = View.GONE
            }
        }
        performRegisterTimer()
        setViewStub(data)
        if(chatType == 1) {
            setReplyContent(data)
            setIconVisibility(data)
        }
        setContentColor(data)
        //        setTime(data)
    }

    private fun setTime(data: ImMsgIn) {
        if (data.getReplyMsgType() == UiMsgType.MSG_TYPE_QUESTION) {
            if (data.getSelfUserId() == data.getSenderId()) {
                timeBottom.visibility = View.VISIBLE
                timeBottom.setData(data)
            }
        } else timeBottom.visibility = View.GONE

    }

    private fun setContentColor(data: ImMsgIn) {
        if (data.getReplyMsgType() == UiMsgType.MSG_TYPE_QUESTION) {
            if (data.getSenderId() == data.getSelfUserId()) {
                tvQuestionName.setTextColor(ContextCompat.getColor(context,
                    R.color.im_msg_text_color_white))
                tvFlag.setTextColor(ContextCompat.getColor(context, R.color.im_msg_text_color_white))
                tvQuestionContent.setTextColor(ContextCompat.getColor(context,
                    R.color.im_msg_text_color_white))
            } else {
                if (data.getReplyMsgQuestionIsPublished() == false) {
                    tvQuestionName.setTextColor(ContextCompat.getColor(context, R.color.im_msg_bg_purple))
                    tvFlag.setTextColor(ContextCompat.getColor(context, R.color.im_msg_bg_purple))
                    tvQuestionContent.setTextColor(ContextCompat.getColor(context,
                        R.color.im_msg_bg_purple))
                } else if (data.getReplySenderId() == data.getSelfUserId() && data.getReplyMsgQuestionIsPublished() == true) {
                    tvQuestionName.setTextColor(ContextCompat.getColor(context, R.color.im_msg_bg_origin))
                    tvFlag.setTextColor(ContextCompat.getColor(context, R.color.im_msg_bg_origin))
                    tvQuestionContent.setTextColor(ContextCompat.getColor(context,
                        R.color.im_msg_bg_origin))
                } else {
                    tvQuestionName.setTextColor(ContextCompat.getColor(context,
                        R.color.im_msg_reward_text_color_reply))
                    tvFlag.setTextColor(ContextCompat.getColor(context,
                        R.color.im_msg_reward_text_color_reply))
                    tvQuestionContent.setTextColor(ContextCompat.getColor(context,
                        R.color.im_msg_reward_text_color_reply))
                }
            }
        } else {
            if (data.getReplySenderId() == data.getSelfUserId()) { //被回复人是自己
                tvQuestionName.setTextColor(ContextCompat.getColor(context, R.color.im_msg_bg_origin))
                tvFlag.setTextColor(ContextCompat.getColor(context, R.color.im_msg_bg_origin))
                if (data.getReplyMsgType() == UiMsgType.MSG_TYPE_TEXT) {
                    tvQuestionContent.setTextColor(ContextCompat.getColor(context,
                        R.color.im_msg_bg_origin))
                } else tvQuestionContent.setTextColor(ContextCompat.getColor(context,
                    R.color.im_msg_bg_green))
            } else { //    被回复人是其他人
                if (data.getSenderId() == data.getSelfUserId()) {
                    tvQuestionName.setTextColor(ContextCompat.getColor(context,
                        R.color.im_msg_text_color_self_reply_others))
                    tvFlag.setTextColor(ContextCompat.getColor(context,
                        R.color.im_msg_text_color_self_reply_others))
                    if (data.getReplyMsgType() == UiMsgType.MSG_TYPE_TEXT) {
                        tvQuestionContent.setTextColor(ContextCompat.getColor(context,
                            R.color.im_msg_text_color_self_reply_others))
                    } else tvQuestionContent.setTextColor(ContextCompat.getColor(context,
                        R.color.im_msg_bg_green))
                } else {
                    tvQuestionName.setTextColor(ContextCompat.getColor(context,
                        R.color.im_msg_reward_text_color_reply))
                    tvFlag.setTextColor(ContextCompat.getColor(context,
                        R.color.im_msg_reward_text_color_reply))
                    if (data.getReplyMsgType() == UiMsgType.MSG_TYPE_TEXT) {
                        tvQuestionContent.setTextColor(ContextCompat.getColor(context,
                            R.color.im_msg_reward_text_color_reply))
                    } else tvQuestionContent.setTextColor(ContextCompat.getColor(context,
                        R.color.im_msg_bg_green))
                }
            }
        }
    }

    private fun setIconVisibility(data: ImMsgIn) {
        if (data.getReplyMsgType() == UiMsgType.MSG_TYPE_QUESTION) {
            imgQuestion.visibility = View.VISIBLE
            imgReply.visibility = View.VISIBLE
            if (data.getSenderId() == data.getSelfUserId()) {
                imgReply.setImageResource(R.drawable.im_msg_item_widget_reward_icon_answer_white)
            } else if (data.getReplyMsgQuestionIsPublished() == false) {
                imgReply.setImageResource(R.drawable.im_msg_item_widget_reward_icon_answer_private)
            } else {
                imgReply.setImageResource(R.drawable.im_msg_item_widget_reward_icon_answer_normal)
            }
        } else {
            imgQuestion.visibility = View.GONE
            imgReply.visibility = View.GONE
        }
    }

    private fun setReplyContent(data: ImMsgIn) {
        if (data.getReplyMsgClientMsgId() != null) data.getReplyMsgType()?.let { data.getReplySenderName()?.let {
            llQuestionContent.visibility = View.VISIBLE
            tvQuestionName.text = it
            tvQuestionContent.visibility = View.VISIBLE
            when (data.getReplyMsgType()) {
                    UiMsgType.MSG_TYPE_TEXT -> tvQuestionContent.text = data.getReplyMsgTextContent()
                    UiMsgType.MSG_TYPE_IMG -> {
                        tvQuestionContent.visibility = View.GONE
                        bubbleRepliedContent.visibility = View.VISIBLE
                        setViewStub2(data)
                    }
                    UiMsgType.MSG_TYPE_AUDIO -> tvQuestionContent.text = context.getString(R.string.im_ui_msg_reply_type_audio)
                    UiMsgType.MSG_TYPE_QUESTION -> tvQuestionContent.text = data.getReplyMsgQuestionContent()
                    UiMsgType.MSG_TYPE_CC_VIDEO -> {
                        tvQuestionContent.visibility = View.GONE
                        bubbleRepliedContent.visibility = View.VISIBLE
                        setViewStub2(data)                    }
                }
                return@setReplyContent
            }
        }
        llQuestionContent.visibility = View.GONE
    }

    private fun setViewStub2(data: ImMsgIn) {
        var isSameTypeReply = false
        try {
            val v: View = when (data.getReplyMsgType()) {
                UiMsgType.MSG_TYPE_IMG -> {
                    isSameTypeReply = curContentInReply is IMContentImageView
                    if (isSameTypeReply) null else IMContentImageView(context)
                }
                UiMsgType.MSG_TYPE_CC_VIDEO -> {
                    isSameTypeReply = curContentInReply is IMContentCCVideoWidgetVIew
                    if (isSameTypeReply) null else IMContentCCVideoWidgetVIew(context)
                }
                else -> null
            } ?: return

            if (!isSameTypeReply) {
                bubbleRepliedContent.removeAllViews()
                curContentInReply = v as? ImContentIn
                bubbleRepliedContent.addView(v, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
                if (data.getReplyMsgType() == UiMsgType.MSG_TYPE_IMG) {
                    bubbleRepliedContent.setOnClickListener {
                        Log.d("LiXiang", "bubbleRContent点击")
                        data.onViewLargePic()
                    }
                }
            }
        } finally {
            bubbleRepliedContent.setPadding(12,12,0,0)
            curContentInReply?.onSetData(data)
        }
    }

    private fun setViewStub(data: ImMsgIn) {
        var isSameType = false
        try {
            val v: View = when (data.getType()) {
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

            if (v is IMContentImageView && data.getSelfUserId() == data.getSenderId()) {
                if (data.getReplyMsgClientMsgId() == null||chatType == 2) llContent.setPadding(0, 0, 0, 0)
                else llContent.setPadding(baseContentMargins, baseContentMargins, baseContentMargins, DPUtils.dp2px(8f))
            } else if (v is IMContentAudioView && data.getSelfUserId() == data.getSenderId()) {
                if (data.getReplyMsgClientMsgId() == null) llContent.setPadding(0, 0, 0, 0)
                else llContent.setPadding(baseContentMargins, baseContentMargins, baseContentMargins, DPUtils.dp2px(8f))
            } else if (data.getSenderId() != data.getSelfUserId()) {
                if (data.getReplyMsgClientMsgId() == null) llContent.setPadding(baseContentMargins, DPUtils.dp2px(6f), baseContentMargins, baseContentMargins)
                else llContent.setPadding(baseContentMargins, DPUtils.dp2px(6f), baseContentMargins, DPUtils.dp2px(8f))
            } else {
                if (data.getReplyMsgClientMsgId() != null) { llContent.setPadding(baseContentMargins, baseContentMargins, baseContentMargins, DPUtils.dp2px(8f)) }
                else llContent.setPadding(baseContentMargins, baseContentMargins, baseContentMargins, baseContentMargins)
            }


            if (!isSameType) {
                bubbleContent.removeAllViews()
                curContentIn = v as? ImContentIn
                bubbleContent.addView(v, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
                if (data.getType() == UiMsgType.MSG_TYPE_IMG) {
                    bubbleContent.setOnClickListener {
                        Log.d("LiXiang", "bubbleContent点击")
                        data.onViewLargePic()
                    }
                }else if(data.getType() == UiMsgType.MSG_TYPE_AUDIO){
                    chatType?.let { curContentIn?.chatType(it) }
                }
                bubbleContent.setOnLongClickListener {
                    Log.d("LiXiang", "bubbleContent长按点击响应")
                    val isNotSelf = data.getSelfUserId() != data.getSenderId()
                    if (data.getType() == UiMsgType.MSG_TYPE_TEXT || isNotSelf) {
                        MsgPop(context, data).show(it)
                    }
                    true
                }
            }
        } finally {
            curContentIn?.onSetData(data)
        }
    }

    override fun notifyChange(pl: Any?) {
        super.notifyChange(pl)
        when (pl) {
            BaseImItem.NOTIFY_CHANGE_AUDIO, BaseImItem.NOTIFY_CHANGE_VIDEO -> onResume()
            BaseImItem.NOTIFY_CHANGE_SENDING_STATE -> performRegisterTimer()
        }
    }

    override fun onResume() {
        curContentIn?.onResume(curData?.invoke())
        curContentInReply?.onResume(curData?.invoke())
        performRegisterTimer()
    }

    private fun performRegisterTimer() {
        curData?.invoke()?.let {
            if (it.getSendState() == 0 || it.getSendState() == 3) {
                TimeDiffUtils.timeDifference(it.getSendTime())?.let { it1 ->
                    MessageSendTimeUtils.registerSendTimeObserver(it.getMsgId(), it1, this)
                }
            } else {
                MessageSendTimeUtils.unRegisterSendTImeObserver(it.getMsgId())
            }
        }
    }

    override fun onSendTime(msgId: String, sendTime: Long) {
        if (msgId == this.curData?.invoke()?.getMsgId()) {
            this.curData?.invoke()?.let { timeBottom.setDataWithTime(sendTime) }
        }
    }

    override fun onStop() {
        curContentIn?.onStop(curData?.invoke())
        curContentInReply?.onStop(curData?.invoke())
        curData?.invoke()?.let { MessageSendTimeUtils.unRegisterSendTImeObserver(it.getMsgId()) }
    }

    override fun onDestroy() {
        curContentIn?.onDestroy(curData?.invoke())
        curContentInReply?.onDestroy(curData?.invoke())
        bubbleContent.removeAllViews()
        bubbleRepliedContent.removeAllViews()
        curContentIn = null
        curContentInReply = null
    }
}