package com.zj.imUi.ui

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.zj.imUi.R
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

    override fun getSendStatusLayoutParams(d: ImMsgIn): LayoutParams {
        val size = (context.resources.displayMetrics.density * 16f + 0.5f).toInt()
        return LayoutParams(size, size).apply {
            ivSendStatusNo?.visibility = View.GONE
            if (d.getSenderId() == d.getSelfUserId()) {
                when (d.getSendingState()) {
                    -2, -1 -> ivSendStatusNo?.visibility = View.VISIBLE
                    else -> {
                        ivSendStatusNo?.visibility = View.GONE
                    }
                }
                addRule(ALIGN_PARENT_BOTTOM)
                addRule(START_OF, R.id.im_item_message_bubble)
            }
        }
    }

    override fun getSendingLayoutParams(d: ImMsgIn): LayoutParams {
        val size = (context.resources.displayMetrics.density * 16f + 0.5f).toInt()
        return LayoutParams(size, size).apply {
            if (d.getSenderId() == d.getSelfUserId()) {
                amSending?.visibility = View.VISIBLE
                when (d.getSendingState()) {
                    1, 2 -> {
                        amSending?.visibility = View.VISIBLE
                        amSending?.indicator?.start()
                    }
                    else -> {
                        amSending?.visibility = View.GONE
                        amSending?.indicator?.stop()
                    }
                }
                addRule(ALIGN_PARENT_BOTTOM)
                addRule(START_OF, R.id.im_item_message_bubble)
            }
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

    override fun getBubbleRenderer(data: ImMsgIn): BaseBubbleRenderer {
        return BubbleRenderer
    }

}