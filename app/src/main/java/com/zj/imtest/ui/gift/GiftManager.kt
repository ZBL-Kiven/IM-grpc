package com.zj.imtest.ui.gift

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.View
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zj.imtest.R
import com.zj.imtest.ui.gift.data.GiftInfo
import com.zj.imtest.ui.gift.v.GiftMarqueeView
import com.zj.imtest.ui.gift.v.GiftShaderView
import com.zj.timer.TimerManager


open class GiftManager(private var marqueeView: GiftMarqueeView<GiftInfo>? = null, private var shaderView: GiftShaderView<GiftInfo>? = null) {

    open val showGiftMarquee: Boolean = true
    open val showGiftShader: Boolean = true
    private var usePendingAnim: Boolean = true

    fun post(giftInfo: GiftInfo, usePendingAnim: Boolean = true) {
        this.usePendingAnim = usePendingAnim
        marqueeView?.post(giftInfo)
    }

    fun clear() {
        marqueeView?.clear()
    }

    fun release() {
        marqueeView?.clear()
        shaderView?.clear()
        val owner = (marqueeView?.context as? LifecycleOwner) ?: return
        TimerManager.removeObserver(owner, javaClass.name)
    }

    init {
        marqueeView?.setGiftDataIn(object : GiftMarqueeView.GiftInflateIn<GiftInfo> {
            override fun onDataInflate(view: View, curData: GiftInfo): Boolean {
                return this@GiftManager.onDataInflate(view, curData)
            }

            override fun whenSameTypeGift(holder: RecyclerView.ViewHolder?, newData: GiftInfo, curData: GiftInfo): Boolean {
                if (usePendingAnim) {
                    curData.num += newData.num
                    holder?.itemView?.let { onDataInflate(it, curData, true) }
                }
                return usePendingAnim
            }
        })
        marqueeView?.let {
            val owner = (it.context as? LifecycleOwner) ?: return@let
            TimerManager.addObserver(owner, it.checkStep.toLong(), javaClass.name, {
                marqueeView?.countDown()
            })
        }
    }

    private fun onDataInflate(view: View, curData: GiftInfo, withPendingAnim: Boolean = false): Boolean {
        if (showGiftMarquee) {
            val avatar = view.findViewById<ImageView>(R.id.item_gift_iv_avatar)
            Glide.with(avatar).load(curData.avatar).placeholder(R.color.black).into(avatar)
            val icon = view.findViewById<ImageView>(R.id.item_gift_iv_icon)
            Glide.with(icon).load(curData.image).placeholder(R.color.black).into(icon)
            val txt = view.findViewById<TextView>(R.id.item_gift_tv_num)
            txt.text = null
            txt.clearAnimation()
            txt.scaleX = 1.0f
            txt.scaleY = 1.0f
            if (curData.num > 1) {
                val span = "x${curData.num}"
                val spannable = SpannableString(span)
                span.forEachIndexed { index, c ->
                    val bmp = getBitmapByName(view.context, "ic_live_message_gifts_number_$c", "drawable") ?: return false
                    val imgSpan = ImageSpan(view.context, bmp)
                    spannable.setSpan(imgSpan, index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                txt.text = spannable
                if (withPendingAnim) {
                    txt.post {
                        txt.scaleX = .8f
                        txt.scaleY = .8f
                        txt.animate().scaleX(1.2f).scaleY(1.2f).setInterpolator(BounceInterpolator()).setDuration(1000).start()
                    }
                }
            }
        }
        onPlayGiftAnimIfNeed(curData)
        return showGiftMarquee
    }

    private fun onPlayGiftAnimIfNeed(curData: GiftInfo) {
        if (showGiftShader && !curData.bundle.isNullOrEmpty()) {
            shaderView?.post(curData)
        }
    }
}

fun getBitmapByName(context: Context, identifier: String, res: String, targetWidth: Int = -1, targetHeight: Int = -1): Bitmap? {
    val appInfo = context.applicationInfo
    runCatching {
        context.resources.getIdentifier(identifier, res, appInfo.packageName)
    }.getOrNull()?.let {
        if (it != 0) {
            return if (targetWidth > 0 && targetHeight > 0) {
                val src = BitmapFactory.decodeResource(context.resources, it)
                val w = src.width
                val h = src.height
                val scaleX = w * 1.0f / targetWidth
                val scaleY = h * 1.0f / targetHeight
                val matrix = Matrix()
                matrix.postScale(scaleX, scaleY)
                Bitmap.createBitmap(src, 0, 0, w, h, matrix, true)
            } else {
                BitmapFactory.decodeResource(context.resources, it)
            }
        }
    }
    return null
}