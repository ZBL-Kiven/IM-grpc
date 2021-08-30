package com.zj.imtest.ui

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zj.imUi.base.BaseBubbleRenderer
import com.zj.imUi.base.BaseImItem
import com.zj.imUi.bubble.BubbleRenderer1
import com.zj.imUi.bubble.BubbleRenderer2
import com.zj.imtest.IMConfig
import com.zj.views.ut.DPUtils
import java.lang.IllegalArgumentException

class ImMsgView(context: Context) : BaseImItem<ImEntityConverter>(context) {


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(1080, h, oldw, h)
    }

    override fun getBubbleLayoutParams(d: ImEntityConverter): LayoutParams {
        return LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT).apply {
            if (d.getSenderId() == IMConfig.getUserId()) {
                ivAvatar?.visibility = View.GONE
                this@ImMsgView.gravity = Gravity.END
            } else {
                this@ImMsgView.gravity = Gravity.START
                ivAvatar?.let {
                    it.visibility = View.VISIBLE
                    addRule(ALIGN_START, it.id)
                }
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
        return when (data.getSendState()) {
            -2, -1 -> BubbleRenderer2
            1, 2 -> BubbleRenderer1
            0, 3 -> super.getBubbleRenderer(data)
            else -> throw IllegalArgumentException()
        }
    }
}