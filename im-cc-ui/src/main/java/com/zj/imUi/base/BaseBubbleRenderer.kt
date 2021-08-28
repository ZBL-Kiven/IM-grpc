package com.zj.imUi.base

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import com.zj.imUi.interfaces.ImMsgIn

interface BaseBubbleRenderer {


    fun getBubble(context: Context, data: ImMsgIn, width: Int, height: Int): Drawable?

    fun drawBubble(context: Context, canvas: Canvas, data: ImMsgIn, width: Int, height: Int)

    fun onDrawBubble(context: Context, canvas: Canvas?, data: ImMsgIn, width: Int, height: Int) {
        canvas?.save()?.let {
            drawBubble(context, canvas, data, width, height)
            canvas.restoreToCount(it)
        }
    }

}