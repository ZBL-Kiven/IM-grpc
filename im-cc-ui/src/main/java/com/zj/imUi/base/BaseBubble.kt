package com.zj.imUi.base

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.zj.imUi.interfaces.ImMsgIn

abstract class BaseBubble @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0) : RelativeLayout(context, attrs, def) {

    private var data: ImMsgIn? = null
    private var baseBubbleRenderer: BaseBubbleRenderer? = null

    internal fun onSetData(data: ImMsgIn) {
        this.data = data
        init(data)
        postInvalidate()
    }

    fun setBubbleRenderer(renderer: BaseBubbleRenderer?) {
        this.baseBubbleRenderer = renderer
        if (data != null) invalidate()
    }

    override fun dispatchDraw(canvas: Canvas?) {
        if (canvas == null || width <= 0 || height <= 0) return
        data?.let { d ->
            baseBubbleRenderer?.let {
                it.getBubble(context, d, width, height)?.draw(canvas) ?: it.onDrawBubble(context, canvas, d, width, height)
            }
        }
        super.dispatchDraw(canvas)
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