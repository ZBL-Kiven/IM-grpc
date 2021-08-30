package com.zj.imUi.base

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.zj.imUi.interfaces.ImMsgIn
import kotlin.math.min

abstract class BaseBubble @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0) : RelativeLayout(context, attrs, def) {

    protected var curData: ImMsgIn? = null
    private var baseBubbleRenderer: BaseBubbleRenderer? = null

    internal fun onSetData(data: ImMsgIn) {
        this.curData = data
        init(data)
        postInvalidate()
    }

    fun setBubbleRenderer(renderer: BaseBubbleRenderer?) {
        this.baseBubbleRenderer = renderer
        if (curData != null) invalidate()
    }

    override fun dispatchDraw(canvas: Canvas?) {
        if (canvas == null || width <= 0 || height <= 0) return
        curData?.let { d ->
            baseBubbleRenderer?.let {
                it.getBubble(context, d, width, height)?.draw(canvas) ?: it.onDrawBubble(context, canvas, d, width, height)
            }
        }
        super.dispatchDraw(canvas)
    }

    //设置气泡最大宽度为屏幕80%
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = MeasureSpec.getSize(widthMeasureSpec)
        val maxWidth = (resources.displayMetrics.widthPixels * 0.8).toInt()
        val min = min(size, maxWidth)
        val measureSpec = MeasureSpec.makeMeasureSpec(min, MeasureSpec.AT_MOST)
        super.onMeasure(measureSpec, heightMeasureSpec)
    }

    fun onDestroyed() {
        baseBubbleRenderer = null
        onDestroy()
    }

    abstract fun init(data: ImMsgIn)

    abstract fun onResume()

    abstract fun onStop()

    abstract fun onDestroy()
}