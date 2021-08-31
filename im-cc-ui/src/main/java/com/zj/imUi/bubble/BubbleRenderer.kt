package com.zj.imUi.bubble

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.zj.imUi.R
import com.zj.imUi.base.BaseBubbleRenderer
import com.zj.imUi.interfaces.ImMsgIn

object BubbleRenderer : BaseBubbleRenderer {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun getBubble(context: Context, data: ImMsgIn, width: Int, height: Int): Drawable? {
        return null
    }

    override fun drawBubble(context: Context, canvas: Canvas, data: ImMsgIn, width: Int, height: Int) {
        drawBackGround(context, canvas, data, width, height)
    }

    private var isSelfMessage = false
    private var isOwner = false
    private var isOwnerReplyQuestionIsPublic = true

    private fun drawBackGround(context: Context, canvas: Canvas, data: ImMsgIn, width: Int, height: Int) {
        val mBgColorOrigin = ContextCompat.getColor(context, R.color.bg_origin)
        isSelfMessage = data.getSelfUserId() == data.getSenderId()
        isOwner = data.getSenderId() == data.getOwnerId()
        if (!data.getPublished()) !isOwnerReplyQuestionIsPublic

        val path = Path()
        paint.strokeWidth = 1f
        paint.style = Paint.Style.FILL
        paint.color = setColor(context, data)
        if (isSelfMessage) { //发送者为自己
            val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
            val radii = floatArrayOf(dpToPx(context, 8f), dpToPx(context, 8f), dpToPx(context, 8f), dpToPx(context, 8f), dpToPx(context, 0f), dpToPx(context, 0f), dpToPx(context, 8f), dpToPx(context, 8f))
            path.addRoundRect(rectF, radii, Path.Direction.CW)
            canvas.drawPath(path, paint)
        } else {
            paint.style = Paint.Style.FILL
            val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
            val radii = floatArrayOf(dpToPx(context, 0f), dpToPx(context, 0f), dpToPx(context, 12f), dpToPx(context, 12f), dpToPx(context, 8f), dpToPx(context, 8f), dpToPx(context, 8f), dpToPx(context, 8f))
            path.addRoundRect(rectF, radii, Path.Direction.CW)
            canvas.drawPath(path, paint) //发送者为大V
            if (isOwner && isOwnerReplyQuestionIsPublic) {
                paint.color = mBgColorOrigin
                paint.style = Paint.Style.STROKE //画金色边框
                canvas.drawPath(drawRect(context, width, height), paint)
            }
        }
    }

    //金色边框
    private fun drawRect(context: Context, width: Int, height: Int): Path {
        val path = Path()
        val rectF = RectF(1f, 1f, width.toFloat() - 1, height.toFloat() - 1)
        val radii = floatArrayOf(dpToPx(context, 0f), dpToPx(context, 0f), dpToPx(context, 12f), dpToPx(context, 12f), dpToPx(context, 8f), dpToPx(context, 8f), dpToPx(context, 8f), dpToPx(context, 8f))
        path.addRoundRect(rectF, radii, Path.Direction.CW)
        return path
    }

    private fun dpToPx(context: Context, dipValue: Float): Float {
        val scale = context.applicationContext.resources.displayMetrics.density
        return dipValue * scale + 0.5f
    }

    private fun setColor(context: Context, data: ImMsgIn): Int {
        return if (isSelfMessage) {
            if (data.getQuestionStatus() == 1||data.getQuestionStatus()==2) {
                ContextCompat.getColor(context, R.color.replied_bg)
            } else {
                if (!data.getPublished()) {
                    if (isOwner) ContextCompat.getColor(context, R.color.bg_purple)
                    else ContextCompat.getColor(context, R.color.message_item_private)
                } else ContextCompat.getColor(context, R.color.bg_origin)
            }
        } else if (data.getQuestionStatus() == 1||data.getQuestionStatus()==2) {
            ContextCompat.getColor(context, R.color.replied_bg)
        } else {
            if (!data.getPublished()) ContextCompat.getColor(context, R.color.message_item_private)
            else ContextCompat.getColor(context, R.color.bg_color_white)
        }
    }
}