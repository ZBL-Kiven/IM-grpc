package com.zj.imUi.items

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewStub
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.zj.imUi.Constance
import com.zj.imUi.R
import com.zj.imUi.base.BaseBubble
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imUi.utils.AutomationImageCalculateUtils
import com.zj.imUi.widget.GroupRewardOwnerMeItem


class IMBubbleContentItem @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0) : BaseBubble(context, attrs, def) {

    private val tvName: AppCompatTextView
    private val bubbleContent: ViewStub
    private val tvQuestionName: AppCompatTextView
    private val tvFlag: AppCompatTextView
    private val tvQuestionContent: AppCompatTextView
    private val imgQuestion: AppCompatImageView
    private val imgReply: AppCompatImageView
    private val timeBottom: GroupRewardOwnerMeItem

    init {
        LayoutInflater.from(context).inflate(R.layout.im_msg_bubble_content, this, true)
        tvName = findViewById(R.id.im_msg_item_normal_text_tv_nickname)
        bubbleContent = findViewById(R.id.im_msg_bubble_viewstub) //被回复的内容
        tvQuestionContent = findViewById(R.id.im_msg_item_normal_text_replied_content)
        tvFlag = findViewById(R.id.im_msg_item_normal_text_replied_tv_flag)
        tvQuestionName = findViewById(R.id.im_msg_item_normal_text_replied_tv_nickname) //question Icon
        imgReply = findViewById(R.id.im_msg_item_normal_icon_question)
        imgQuestion = findViewById(R.id.im_msg_item_normal_icon_reply)
        timeBottom = findViewById(R.id.im_msg_item_normal_text_replied_time)
    }

    override fun init(data: ImMsgIn) {
        tvName.text = data.getSenderName() //如果发送者为自己，不展示昵称
        if (data.getSelfUserId() == data.getSenderId()) {
            tvName.visibility = View.GONE
        }
        setIconVisibility(data)
        setViewStub(data)
        setReplyContent(data)
        setTime(data)
        setContentColor(data)
    }

    private fun setTime(data: ImMsgIn) {
        timeBottom.visibility = View.GONE
        if (data.getSelfUserId() == data.getOwnerId() && data.getReplyMsgType() == Constance.MSG_TYPE_QUESTION) {
            timeBottom.setData(data)
            timeBottom.visibility = View.VISIBLE
        }
    }

    private fun setContentColor(data: ImMsgIn) {
        if (data.getReplySenderId() == data.getSelfUserId()) { //被回复人是自己
            tvQuestionName.setTextColor(ContextCompat.getColor(context, R.color.bg_origin))
            tvFlag.setTextColor(ContextCompat.getColor(context, R.color.bg_origin))
            if (data.getReplyMsgType() == Constance.MSG_TYPE_TEXT) {
                tvQuestionContent.setTextColor(ContextCompat.getColor(context, R.color.bg_origin))
            } else tvQuestionContent.setTextColor(ContextCompat.getColor(context, R.color.bg_green))
        }
    }

    private fun setIconVisibility(data: ImMsgIn) {
        if (data.getReplyMsgType() == Constance.MSG_TYPE_QUESTION) {
            if (data.getOwnerId() == data.getSelfUserId()) {
                imgReply.setImageResource(R.drawable.icon_huida_bai)
            }
            if (!data.getPublished()) {
                imgReply.setImageResource(R.drawable.icon_simihuida_normal)
            }
        } else {
            imgQuestion.visibility = View.GONE
            imgReply.visibility = View.GONE
        }
    }

    private fun setReplyContent(data: ImMsgIn) {
        data.getReplySenderName()?.let {
            tvQuestionName.text = it
        }
        when (data.getReplyMsgType()) {
            Constance.MSG_TYPE_TEXT -> {
                tvQuestionContent.text = data.getReplyMsgTextContent()
            }
            Constance.MSG_TYPE_IMG -> tvQuestionContent.text = context.getString(R.string.im_ui_msg_reward_type_image)
            Constance.MSG_TYPE_AUDIO -> tvQuestionContent.text = context.getString(R.string.im_ui_msg_reward_type_audio)
        }
    }

    private fun setViewStub(data: ImMsgIn) {
        when (data.getType()) {
            Constance.MSG_TYPE_TEXT -> {
                bubbleContent.layoutResource = R.layout.im_msg_item_normal_text
                bubbleContent.inflate()
                val textContent: AppCompatTextView = findViewById(R.id.im_msg_item_normal_text_tv_content)
                textContent.text = data.getTextContent()
                if (data.getSelfUserId() == data.getSenderId()) textContent.setTextColor(Color.WHITE)
            }

            Constance.MSG_TYPE_IMG -> {
                bubbleContent.layoutResource = R.layout.im_msg_item_normal_img
                bubbleContent.inflate()
                val imgContent: AppCompatImageView = findViewById(R.id.im_msg_item_normal_img_img_content)
                setImg(imgContent, data)
            }

            Constance.MSG_TYPE_AUDIO -> {
                bubbleContent.layoutResource = R.layout.im_msg_item_normal_audio
                bubbleContent.inflate()
                val imgContent: AppCompatTextView = findViewById(R.id.im_msg_item_normal_audio_content)
            }
        }
    }

    private fun setImg(imgView: AppCompatImageView, data: ImMsgIn) {
        val arrayInt: Array<Int>? = setImgLp(data)
        if (arrayInt != null) {
            data.getImgContentUrl()?.let {
                val corners = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, context.resources.displayMetrics).toInt()
                Glide.with(imgView).load(it).centerInside().override(width, height).apply(RequestOptions.bitmapTransform(RoundedCorners(corners))).into(imgView)
            }
        }
    }

    private fun setImgLp(data: ImMsgIn): Array<Int>? {
        return if (data.getImgContentWidth() != null && data.getImgContentWidth() != null) {
            AutomationImageCalculateUtils.proportionalWH(data.getImgContentWidth()!!, data.getImgContentHeight()!!, 201, 398, 1F)
        } else null
    }

    override fun onResume() {}
    override fun onStop() {}
    override fun onDestroy() {
        removeAllViews()
    }
}