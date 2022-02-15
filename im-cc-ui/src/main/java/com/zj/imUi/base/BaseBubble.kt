package com.zj.imUi.base

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.zj.imUi.UiMsgType
import com.zj.imUi.interfaces.ImMsgIn
import kotlin.math.min

abstract class BaseBubble @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0) : RelativeLayout(context, attrs, def) {

    protected var curData: (() -> ImMsgIn?)? = null
    protected var chatType: Any? = null

    private var baseBubbleRenderer: BaseBubbleRenderer? = null

    internal fun onSetData(data: () -> ImMsgIn?, chatType: Int?) {
        val d = data.invoke() ?: return
        this.curData = data
        this.chatType = chatType
        chatType?.let { init(d, it) }
        postInvalidate()
    }

    fun setBubbleRenderer(renderer: BaseBubbleRenderer?) {
        this.baseBubbleRenderer = renderer
        if (curData?.invoke() != null) invalidate()
    }

    override fun dispatchDraw(canvas: Canvas?) {
        if (canvas == null || width <= 0 || height <= 0) return
        curData?.invoke()?.let { d ->
            baseBubbleRenderer?.let {
                it.getBubble(context, d, width, height)?.draw(canvas) ?: it.onDrawBubble(context, canvas, d, width, height, chatType as Int?)
            }
        }
        super.dispatchDraw(canvas)
    }

    //设置气泡最大宽度为屏幕80%
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = MeasureSpec.getSize(widthMeasureSpec)
        val isFull = (chatType == 3 && curData?.invoke()?.getSelfUserId() == curData?.invoke()?.getSenderId())
                || curData?.invoke()?.getMsgIsRecalled() == true
                || curData?.invoke()?.getMsgIsSensitive() == true
                || curData?.invoke()?.getUiTypeWithMessageType() == UiMsgType.MSG_TYPE_SYS_REFUSE
                || curData?.invoke()?.getUiTypeWithMessageType() == UiMsgType.MSG_TYPE_CC_GIFT
        val maxWidth = if (isFull) {
            (resources.displayMetrics.widthPixels * 1)
        } else (resources.displayMetrics.widthPixels * 0.8).toInt()
        val min = min(size, maxWidth)
        val measureSpec = MeasureSpec.makeMeasureSpec(min, MeasureSpec.AT_MOST)
        super.onMeasure(measureSpec, heightMeasureSpec)
    }

    fun onDestroyed() {
        baseBubbleRenderer = null
        onDestroy()
    }

    abstract fun init(data: ImMsgIn, chatType: Any)

    abstract fun onResume()

    abstract fun onStop()

    abstract fun onDestroy()

    open fun notifyChange(pl: Any?) {}

}