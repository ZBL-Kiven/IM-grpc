package com.zj.imUi.ui

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
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

    override fun getAvatarLayoutParams(d: ImMsgIn): LayoutParams {
        val size = (context.resources.displayMetrics.density * 40f + 0.5f).toInt()
        return LayoutParams(size, size)
    }

    override fun onLoadAvatar(iv: ImageView?, d: ImMsgIn) {
        if (iv == null) return
        Glide.with(iv).load(d.getSenderAvatar()).into(iv)
    }

    override fun getBubbleRenderer(data: ImMsgIn): BaseBubbleRenderer {
        return BubbleRenderer
    }
}