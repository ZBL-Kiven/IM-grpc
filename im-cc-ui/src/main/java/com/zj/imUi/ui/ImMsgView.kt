package com.zj.imUi.ui

import android.content.Context

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.zj.imUi.R
import com.zj.imUi.UiMsgType
import com.zj.imUi.base.BaseBubbleRenderer
import com.zj.imUi.base.BaseImItem
import com.zj.imUi.bubble.BubbleRenderer
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.views.ut.DPUtils

class ImMsgView(context: Context) : BaseImItem<ImMsgIn>(context) {

    override fun getBubbleLayoutParams(d: ImMsgIn): LayoutParams {
        return LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
            if (d.getMsgIsRecalled() || d.getMsgIsSensitive() ||
                d.getUiTypeWithMessageType() == UiMsgType.MSG_TYPE_SYS_REFUSE||
                d.getUiTypeWithMessageType()==UiMsgType.MSG_TYPE_CC_GIFT) {
                ivAvatar?.visibility = View.GONE
                addRule(CENTER_IN_PARENT)
            } else if (d.getSenderId() == d.getSelfUserId()) {
                if (type == 3 && d.getUiTypeWithMessageType() == UiMsgType.MSG_TYPE_QUESTION) {
                    ivAvatar?.visibility = View.GONE
                    addRule(CENTER_IN_PARENT)
                } else {
                    ivAvatar?.visibility = View.GONE
                    addRule(ALIGN_PARENT_END)
                }
            } else {
                ivAvatar?.visibility = View.VISIBLE
                val avatar: ImageView? = ivAvatar
                if (avatar != null) {
                    ivAvatar?.id?.let { addRule(RIGHT_OF, it) }
                } else {
                    initAvatar(d)
                    ivAvatar?.id?.let { addRule(RIGHT_OF, it) }
                }
            }
        }
    }

    override fun getSendingLayoutParams(d: ImMsgIn): LayoutParams {
        val size = (context.resources.displayMetrics.density * 16f + 0.5f).toInt()
        return LayoutParams(size, size).apply {
            addRule(ALIGN_PARENT_BOTTOM)
            setMargins(0, 0, DPUtils.dp2px(8f), 0)
            bubbleView?.id?.let { addRule(START_OF, it) }
        }
    }

    override fun getAvatarLayoutParams(d: ImMsgIn): LayoutParams {
        val size = (context.resources.displayMetrics.density * 40f + 0.5f).toInt()
        val lpS = (context.resources.displayMetrics.density * 8f + 0.5f).toInt()
        val lp = LayoutParams(size, size)
        lp.marginEnd = lpS
        return lp.apply {
            addRule(ALIGN_PARENT_START)
            ivAvatar?.id?.let { addRule(RIGHT_OF, it) }
        }
    }

    override fun onLoadAvatar(iv: ImageView?, d: ImMsgIn) {
        if (iv == null) return
        Glide.with(iv).load(d.getSenderAvatar()).circleCrop().placeholder(R.drawable.im_msg_item_default_avatar).error((R.drawable.im_msg_item_default_avatar)).into(iv)
    }

    override fun getBubbleRenderer(data: ImMsgIn): BaseBubbleRenderer? {
        val dataType = data.getUiTypeWithMessageType()
        if (dataType == UiMsgType.MSG_TYPE_CC_EMOTION) return null
        if (dataType == UiMsgType.MSG_TYPE_CC_GIFT) return null
        if (data.getMsgIsRecalled() || data.getMsgIsSensitive() || data.getUiTypeWithMessageType() == UiMsgType.MSG_TYPE_SYS_REFUSE) return null
        if (data.getSenderId() == data.getSelfUserId() && dataType == UiMsgType.MSG_TYPE_IMG && data.getReplyMsgClientMsgId() == null) return null
        if (dataType == UiMsgType.MSG_TYPE_AUDIO && data.getSenderId() == data.getSelfUserId() && data.getReplyMsgClientMsgId() == null) return null
        if (dataType == UiMsgType.MSG_TYPE_CC_LIVE && data.getSenderId() == data.getSelfUserId()) return null
        type?.let {
            if (it == 2) {
                if (data.getSelfUserId() == data.getSenderId() && (dataType == UiMsgType.MSG_TYPE_IMG || dataType == UiMsgType.MSG_TYPE_AUDIO)) return null
            }
        }
        return BubbleRenderer
    }

    override fun getTvNickNameLayoutParams(d: ImMsgIn): LayoutParams {
        return LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            tvNickname?.id?.let { addRule(BELOW, it) }
            ivAvatar?.id?.let { addRule(RIGHT_OF, it) }
        }
    }
}