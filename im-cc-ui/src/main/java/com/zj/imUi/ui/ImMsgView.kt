package com.zj.imUi.ui

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.zj.imUi.R
import com.zj.imUi.UiMsgType
import com.zj.imUi.base.BaseBubbleRenderer
import com.zj.imUi.base.BaseImItem
import com.zj.imUi.bubble.BubbleRenderer
import com.zj.imUi.interfaces.ImMsgIn

class ImMsgView(context: Context) : BaseImItem<ImMsgIn>(context) {

    override fun getBubbleLayoutParams(d: ImMsgIn): LayoutParams {
        return LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT).apply {
            if (d.getSenderId() == d.getSelfUserId()) {
                ivAvatar?.visibility = View.GONE
                addRule(ALIGN_PARENT_END)
            } else {
                val avatar = ivAvatar
                if (avatar != null) {
                    addRule(RIGHT_OF, R.id.im_item_message_avatar)
                    avatar.visibility = View.VISIBLE
                } else {
                    addRule(ALIGN_PARENT_START)
                }
            }
        }
    }


    override fun getSendingLayoutParams(d: ImMsgIn): LayoutParams {
        val size = (context.resources.displayMetrics.density * 16f + 0.5f).toInt()
        return LayoutParams(size, size).apply {
            addRule(ALIGN_PARENT_BOTTOM)
            addRule(START_OF, R.id.im_item_message_bubble)
        }
    }

    override fun getAvatarLayoutParams(d: ImMsgIn): LayoutParams {
        val size = (context.resources.displayMetrics.density * 40f + 0.5f).toInt()
        return LayoutParams(size, size)
    }

    override fun onLoadAvatar(iv: ImageView?, d: ImMsgIn) {
        if (iv == null) return
        val corners = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, context.resources.displayMetrics).toInt()
        Glide.with(iv).load(d.getSenderAvatar()).centerInside().apply(RequestOptions.bitmapTransform(RoundedCorners(corners))).into(iv)
    }

    override fun getBubbleRenderer(data: ImMsgIn): BaseBubbleRenderer? {
        if (data.getSenderId() == data.getSelfUserId() && data.getType() == UiMsgType.MSG_TYPE_IMG && data.getReplyMsgClientMsgId() == null) return null
        if (data.getType() == UiMsgType.MSG_TYPE_AUDIO&&data.getSenderId() == data.getSelfUserId()&&data.getReplyMsgClientMsgId() == null) return null
        return BubbleRenderer
    }

}