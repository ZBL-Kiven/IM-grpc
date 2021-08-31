package com.zj.imUi.items

import android.content.Context
import android.util.AttributeSet
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
import com.zj.imUi.widget.GroupRewardOwnerMeItem


class IMBubbleContentItem @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0) : BaseBubble(context, attrs, def) {

    private val tvName: AppCompatTextView
    private val iconIsOwner: AppCompatImageView
    private val llQuestionContent: LinearLayout
    private val llName: LinearLayout
    private val bubbleContent: FrameLayout
    private val tvQuestionName: AppCompatTextView
    private val tvFlag: AppCompatTextView
    private val tvQuestionContent: AppCompatTextView
    private val imgQuestion: AppCompatImageView
    private val imgReply: AppCompatImageView
    private val timeBottom: GroupRewardOwnerMeItem
    private val llContent: LinearLayout
    private var curContentIn: ImContentIn? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.im_msg_bubble_content, this, true)
        tvName = findViewById(R.id.im_msg_item_normal_text_tv_nickname)
        bubbleContent = findViewById(R.id.im_msg_bubble_viewstub) //被回复的内容
        tvQuestionContent = findViewById(R.id.im_msg_item_normal_text_replied_content)
        tvFlag = findViewById(R.id.im_msg_item_normal_text_replied_tv_flag)
        tvQuestionName = findViewById(R.id.im_msg_item_normal_text_replied_tv_nickname) //question Icon
        imgQuestion = findViewById(R.id.im_msg_item_normal_icon_question)
        imgReply = findViewById(R.id.im_msg_item_normal_icon_reply)
        timeBottom = findViewById(R.id.im_msg_item_normal_text_replied_time)
        llQuestionContent = findViewById(R.id.im_msg_bubble_content_ll_question)
        llName = findViewById(R.id.im_msg_bubble_ll_title)
        iconIsOwner = findViewById(R.id.im_msg_bubble_img_owner)
        llContent = findViewById(R.id.im_msg_bubble_img_ll_content)
    }

    override fun init(data: ImMsgIn) {
        tvName.text = data.getSenderName()
        if (data.getSenderId() == data.getOwnerId()) {
            iconIsOwner.visibility = View.VISIBLE
        }
        if (data.getSelfUserId() == data.getSenderId()) {
            llName.visibility = View.GONE
        } else llName.visibility = View.VISIBLE
        setIconVisibility(data)
        setViewStub(data)
        setReplyContent(data)
        setTime(data)
        setContentColor(data)
    }

    private fun setTime(data: ImMsgIn) {
        timeBottom.visibility = View.GONE
        if (data.getSelfUserId() == data.getOwnerId() && data.getReplyMsgType() == UiMsgType.MSG_TYPE_QUESTION) {
            timeBottom.setData(data)
            timeBottom.visibility = View.VISIBLE
        }
    }

    private fun setContentColor(data: ImMsgIn) {
        if (data.getReplyMsgType() == UiMsgType.MSG_TYPE_QUESTION) {
            if (data.getSenderId() == data.getSelfUserId() && data.getSelfUserId() == data.getOwnerId()) {
                tvQuestionName.setTextColor(ContextCompat.getColor(context, R.color.text_color_white))
                tvFlag.setTextColor(ContextCompat.getColor(context, R.color.text_color_white))
                tvQuestionContent.setTextColor(ContextCompat.getColor(context, R.color.text_color_white))
            } else if (data.getSenderId() == data.getOwnerId()) {
                if (data.getReplyMsgQuestionIsPublished() == false) {
                    tvQuestionName.setTextColor(ContextCompat.getColor(context, R.color.bg_purple))
                    tvFlag.setTextColor(ContextCompat.getColor(context, R.color.bg_purple))
                    tvQuestionContent.setTextColor(ContextCompat.getColor(context, R.color.bg_purple))
                } else if (data.getReplySenderId() == data.getSelfUserId() && data.getReplyMsgQuestionIsPublished() == true) {
                    tvQuestionName.setTextColor(ContextCompat.getColor(context, R.color.bg_origin))
                    tvFlag.setTextColor(ContextCompat.getColor(context, R.color.bg_origin))
                    tvQuestionContent.setTextColor(ContextCompat.getColor(context, R.color.bg_origin))
                } else {
                    tvQuestionName.setTextColor(ContextCompat.getColor(context, R.color.reward_text_color_reply))
                    tvFlag.setTextColor(ContextCompat.getColor(context, R.color.reward_text_color_reply))
                    tvQuestionContent.setTextColor(ContextCompat.getColor(context, R.color.reward_text_color_reply))
                }
            }
        } else {
            if (data.getReplySenderId() == data.getSelfUserId()) { //被回复人是自己
                tvQuestionName.setTextColor(ContextCompat.getColor(context, R.color.bg_origin))
                tvFlag.setTextColor(ContextCompat.getColor(context, R.color.bg_origin))
                if (data.getReplyMsgType() == UiMsgType.MSG_TYPE_TEXT) {
                    tvQuestionContent.setTextColor(ContextCompat.getColor(context, R.color.bg_origin))
                } else tvQuestionContent.setTextColor(ContextCompat.getColor(context, R.color.bg_green))
            } else { //    被回复人是其他人
                if (data.getSenderId() == data.getSelfUserId()) {
                    tvQuestionName.setTextColor(ContextCompat.getColor(context, R.color.text_color_self_reply_others))
                    tvFlag.setTextColor(ContextCompat.getColor(context, R.color.text_color_self_reply_others))
                    if (data.getReplyMsgType() == UiMsgType.MSG_TYPE_TEXT) {
                        tvQuestionContent.setTextColor(ContextCompat.getColor(context, R.color.text_color_self_reply_others))
                    } else tvQuestionContent.setTextColor(ContextCompat.getColor(context, R.color.bg_green))
                } else {
                    tvQuestionName.setTextColor(ContextCompat.getColor(context, R.color.reward_text_color_reply))
                    tvFlag.setTextColor(ContextCompat.getColor(context, R.color.reward_text_color_reply))
                    if (data.getReplyMsgType() == UiMsgType.MSG_TYPE_TEXT) {
                        tvQuestionContent.setTextColor(ContextCompat.getColor(context, R.color.reward_text_color_reply))
                    } else tvQuestionContent.setTextColor(ContextCompat.getColor(context, R.color.bg_green))
                }
            }
        }
    }

    private fun setIconVisibility(data: ImMsgIn) {
        if (data.getReplyMsgType() == UiMsgType.MSG_TYPE_QUESTION) {
            imgQuestion.visibility = View.VISIBLE
            imgReply.visibility = View.VISIBLE
            if (data.getSenderId() == data.getSelfUserId()) {
                if (data.getOwnerId() == data.getSelfUserId()) imgReply.setImageResource(R.drawable.icon_huida_bai)
            } else if (data.getReplyMsgQuestionIsPublished() == false && data.getSenderId() == data.getOwnerId()) {
                imgReply.setImageResource(R.drawable.icon_simihuida_normal)
            }
        } else {
            imgQuestion.visibility = View.GONE
            imgReply.visibility = View.GONE
        }
    }

    private fun setReplyContent(data: ImMsgIn) {
        data.getReplySenderName()?.let {
            llQuestionContent.visibility = View.VISIBLE
            tvQuestionName.text = it
        }
        when (data.getReplyMsgType()) {
            UiMsgType.MSG_TYPE_TEXT -> {
                tvQuestionContent.text = data.getReplyMsgTextContent()
            }
            UiMsgType.MSG_TYPE_IMG -> tvQuestionContent.text = context.getString(R.string.im_ui_msg_reward_type_image)
            UiMsgType.MSG_TYPE_AUDIO -> tvQuestionContent.text = context.getString(R.string.im_ui_msg_reward_type_audio)
            UiMsgType.MSG_TYPE_QUESTION -> tvQuestionContent.text = data.getReplyMsgQuestionContent()
        }
    }

    private fun setViewStub(data: ImMsgIn) {
        var isSameType = false
        val v: View? = when (data.getType()) {
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
        }
        if (!isSameType) {
            bubbleContent.removeAllViews()
            curContentIn = v as? ImContentIn
            bubbleContent.addView(v, LayoutParams(-1, -1))
            curContentIn?.onSetData(data)
        } else {
            curContentIn?.onSetData(data)
        }
    }

    override fun notifyChange(pl: Any?) {
        super.notifyChange(pl)
        when (pl) {
            BaseImItem.NOTIFY_CHANGE_AUDIO, BaseImItem.NOTIFY_CHANGE_VIDEO -> onResume()
        }
    }

    override fun onResume() {
        curContentIn?.onResume(curData?.invoke())
    }

    override fun onStop() {
        curContentIn?.onStop(curData?.invoke())
    }

    override fun onDestroy() {
        curContentIn?.onDestroy(curData?.invoke())
        bubbleContent.removeAllViews()
        curContentIn = null
    }
}