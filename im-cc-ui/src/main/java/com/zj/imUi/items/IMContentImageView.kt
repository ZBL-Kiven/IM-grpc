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
import com.zj.views.ut.DPUtils


class IMContentImageView @JvmOverloads constructor(context: Context,
    attrs: AttributeSet? = null,
    def: Int = 0) : AppCompatImageView(context, attrs, def), ImContentIn {

    private var anim: ObjectAnimator = ObjectAnimator.ofInt(this, "ImageLevel", 0, 10000)

    init {
        anim.duration = 800
        anim.repeatCount = ObjectAnimator.INFINITE
        anim.start()
        this.scaleType = ScaleType.CENTER_INSIDE
    }

    override fun onSetData(data: ImMsgIn?) {
        if (data == null) return
        val arrayInt: Array<Int>? = setImgLp(data)
        if (arrayInt != null) {
            loadImg(data, arrayInt)
        } else{
            loadImg2(data)
        }
    }

    override fun chatType(chatType:Any) {
    }

    private fun loadImg(data: ImMsgIn, arrayInt: Array<Int>) {
        val corners = if (data.getImgContentUrl() != null)
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, context.resources.displayMetrics).toInt()
        else
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, context.resources.displayMetrics).toInt()

        val lp = FrameLayout.LayoutParams(arrayInt[0], arrayInt[1])
        layoutParams = lp
        val imgUrl: String? =
            if (data.getReplyMsgType() == UiMsgType.MSG_TYPE_IMG && data.getReplyMsgImgContent() != null) { data.getReplyMsgImgContent() }
            else if (data.getAnswerContentImgContentUrl()!=null) data.getAnswerContentImgContentUrl()
            else data.getImgContentUrl()

        imgUrl?.let {
            Glide.with(this).load(it)
                .override(arrayInt[0],arrayInt[1])
                .centerInside()
                .placeholder(R.drawable.im_msg_item_img_loading)
                .error(R.drawable.im_msg_item_img_loading)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(corners)))
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean): Boolean {
                        anim.cancel()
                        return false
                    }
                }).into(this)
        }
    }
    private fun loadImg2(data: ImMsgIn) {
        val corners = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, context.resources.displayMetrics).toInt()
        val imgW = DPUtils.dp2px(44f)
        val imgH = DPUtils.dp2px(44f)
        val lp = FrameLayout.LayoutParams(imgW,imgH)
        layoutParams = lp
        val imgUrl: String? = data.getReplyMsgImgContent()
        imgUrl?.let {
            Glide.with(this).load(it)
                .override(imgW,imgH)
                .placeholder(R.drawable.im_msg_item_img_loading)
                .error(R.drawable.im_msg_item_img_loading)
                //                .thumbnail(0.1f)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(corners)))
                .centerCrop()
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean): Boolean {
                        anim.cancel()
                        return false
                    }
                }).into(this)
        }
    }


    private fun setImgLp(data: ImMsgIn): Array<Int>? {
        var maxW = DPUtils.dp2px(201f)
        var maxH = DPUtils.dp2px(132f)

        val imgWidth =
            if (data.getImgContentWidth() != null && data.getImgContentHeight() != null) {
                data.getImgContentWidth()
            } else
                data.getReplyMsgImgWidth()

        val imgHeight: Int?
        if (data.getImgContentHeight() != null) {
            imgHeight = data.getImgContentHeight()
        } else  {
            imgHeight = if (data.getAnswerContentImgContentUrl()!=null) data.getAnswerContentImgContentHeight()
            else data.getReplyMsgImgHeight()
            maxW = DPUtils.dp2px(44f)
            maxH = DPUtils.dp2px(44f)
        }

        return run {
            val dataWidth = imgWidth ?: maxH
            val dataHeight = imgHeight ?: maxH
            AutomationImageCalculateUtils.proportionalWH(dataWidth, dataHeight, maxW, maxH, 0.5f)
        }
    }

    override fun onResume(data: ImMsgIn?) {
    }

    override fun onStop(data: ImMsgIn?) {
    }

    override fun onDestroy(data: ImMsgIn?) {
    }
}