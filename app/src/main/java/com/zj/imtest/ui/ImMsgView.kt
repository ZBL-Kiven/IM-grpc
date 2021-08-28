package com.zj.imtest.ui

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.zj.imUi.base.BaseBubbleRenderer
import com.zj.imUi.base.BaseImItem
import com.zj.imUi.bubble.BubbleRenderer1
import com.zj.views.ut.DPUtils
import kotlin.random.Random

class ImMsgView(context: Context) : BaseImItem<ImEntityConverter>(context) {

    override fun getBubbleLayoutParams(d: ImEntityConverter): LayoutParams {
        return LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
            ivAvatar?.let {
                addRule(RIGHT_OF, it.id)
            }
        }
    }

    override fun getAvatarLayoutParams(d: ImEntityConverter): LayoutParams {
        val size = DPUtils.dp2px(40f)
        return LayoutParams(size, size)
    }

    override fun onLoadAvatar(iv: ImageView?, d: ImEntityConverter) {
        if (iv == null) return
        Glide.with(iv).load(d.getSenderAvatar()).into(iv)
    }

    override fun getBubbleRenderer(data: ImEntityConverter): BaseBubbleRenderer? {
        return if (Random.nextBoolean()) {
            BubbleRenderer1
        } else super.getBubbleRenderer(data)
    }
}