package com.zj.imUi.items

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.zj.imUi.R
import com.zj.imUi.UiMsgType
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imUi.utils.AutomationImageCalculateUtils
import com.zj.imUi.widget.BasePopFlowWindow
import com.zj.views.ut.DPUtils


class IMContentEmotionImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0) : AppCompatImageView(context, attrs, def), ImContentIn {

    private var anim: ObjectAnimator = ObjectAnimator.ofInt(this, "ImageLevel", 0, 10000)

    init {
        anim.duration = 800
        anim.repeatCount = ObjectAnimator.INFINITE
        anim.start()
        this.scaleType = ScaleType.CENTER_INSIDE
    }

    override fun onSetData(data: ImMsgIn?) {
        if (data == null) return
        loadImg(data)

        this.setOnClickListener { //            Toast.makeText(context, "IMContentImageView图片点击", Toast.LENGTH_SHORT).show()
            data.onViewLargePic()
        }
    }

    override fun chatType(chatType: Any) {
    }

    private fun loadImg(data: ImMsgIn) {
        val corners = if (data.getImgContentUrl() != null) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, context.resources.displayMetrics).toInt()
        else TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, context.resources.displayMetrics).toInt()


        data.getEmotionUrl()?.let {
            Glide.with(this).load(it).centerInside().placeholder(R.drawable.im_msg_item_img_loading).error(R.drawable.im_msg_item_img_loading).apply(RequestOptions.bitmapTransform(RoundedCorners(corners))).addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    anim.cancel()
                    return false
                }
            }).into(this)
        }
    }


    override fun onResume(data: ImMsgIn?) {
    }

    override fun onStop(data: ImMsgIn?) {
    }

    override fun onDestroy(data: ImMsgIn?) {
    }
}