package com.zj.imUi.bubble

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import com.zj.imUi.base.BaseBubbleRenderer
import com.zj.imUi.interfaces.ImMsgIn

object BubbleRenderer1 : BaseBubbleRenderer {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun getBubble(context: Context, data: ImMsgIn, width: Int, height: Int): Drawable? {
        return null
    }

    override fun drawBubble(context: Context, canvas: Canvas, data: ImMsgIn, width: Int, height: Int) {
        val rect = Rect(0, 0, width, height)
        paint.color = Color.YELLOW
        canvas.drawRect(rect, paint)
    }
}