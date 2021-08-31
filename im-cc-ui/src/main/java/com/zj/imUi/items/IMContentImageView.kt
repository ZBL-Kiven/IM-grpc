package com.zj.imUi.items

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imUi.utils.AutomationImageCalculateUtils
import com.zj.views.ut.DPUtils

class IMContentImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0) : AppCompatImageView(context, attrs, def), ImContentIn {

    override fun onSetData(data: ImMsgIn?) {
        if (data == null) return
        val arrayInt: Array<Int>? = setImgLp(data)
        if (arrayInt != null) {
            val corners = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, context.resources.displayMetrics).toInt()
            val lp = FrameLayout.LayoutParams(arrayInt[0], arrayInt[1])
            layoutParams = lp
            data.getImgContentUrl()?.let {
                Glide.with(this).load(it).centerInside().override(arrayInt[0], arrayInt[1]).apply(RequestOptions.bitmapTransform(RoundedCorners(corners))).into(this)
            }
        }
    }

    private fun setImgLp(data: ImMsgIn): Array<Int>? {
        return if (data.getImgContentWidth() != null && data.getImgContentWidth() != null) {
            val maxW = DPUtils.dp2px(201f)
            val maxH = DPUtils.dp2px(132f)
            val dataWidth = data.getImgContentWidth() ?: maxH
            val dataHeight = data.getImgContentHeight() ?: maxH
            AutomationImageCalculateUtils.proportionalWH(dataWidth, dataHeight, maxW, maxH, 0.5f)
        } else null
    }

    override fun onResume(data: ImMsgIn?) {
    }

    override fun onStop(data: ImMsgIn?) {
    }

    override fun onDestroy(data: ImMsgIn?) {
    }
}