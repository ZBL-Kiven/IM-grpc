package com.zj.imUi.bubble

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.zj.imUi.R
import com.zj.imUi.UiMsgType
import com.zj.imUi.base.BaseBubbleRenderer
import com.zj.imUi.interfaces.ImMsgIn

object BubbleRenderer : BaseBubbleRenderer {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var isSelfMessage = false
    private var isOwner = false
    private var isOwnerReplyQuestionIsPublic = true

    override fun getBubble(context: Context, data: ImMsgIn, width: Int, height: Int): Drawable? {
        return null
    }


    override fun drawBubble(context: Context,
        canvas: Canvas,
        data: ImMsgIn,
        width: Int,
        height: Int,
        chatType: Int) {
        drawBackGround(context, canvas, data, width, height, chatType)
    }


    private fun drawBackGround(context: Context,
        canvas: Canvas,
        data: ImMsgIn,
        width: Int,
        height: Int,
        chatType: Int) {
        val mBgColorOrigin = ContextCompat.getColor(context, R.color.im_msg_bg_origin)
        isSelfMessage = data.getSelfUserId() == data.getSenderId()
        isOwner = data.getSenderId() == data.getOwnerId()

        val path = Path()
        paint.strokeWidth = 1f
        paint.style = Paint.Style.FILL
        paint.color = setColor(context, data, chatType)
        if (isSelfMessage) { //发送者为自己
            val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
            val radii = floatArrayOf(dpToPx(context, 8f),
                dpToPx(context, 8f),
                dpToPx(context, 8f),
                dpToPx(context, 8f),
                dpToPx(context, 0f),
                dpToPx(context, 0f),
                dpToPx(context, 8f),
                dpToPx(context, 8f))
            path.addRoundRect(rectF, radii, Path.Direction.CW)
            canvas.drawPath(path, paint)
        } else {
            paint.style = Paint.Style.FILL
            val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
            val radii = floatArrayOf(dpToPx(context, 0f),
                dpToPx(context, 0f),
                dpToPx(context, 8f),
                dpToPx(context, 8f),
                dpToPx(context, 8f),
                dpToPx(context, 8f),
                dpToPx(context, 8f),
                dpToPx(context, 8f))
            path.addRoundRect(rectF, radii, Path.Direction.CW)
            canvas.drawPath(path, paint)
            isOwnerReplyQuestionIsPublic = true
            if (data.getReplyMsgQuestionIsPublished() != null) isOwnerReplyQuestionIsPublic = data.getReplyMsgQuestionIsPublished() == true
            if (isOwner && (isOwnerReplyQuestionIsPublic || chatType == UiMsgType.PRIVATE_CHAT)) {
                paint.strokeWidth = 1.5f
                paint.color = mBgColorOrigin
                paint.style = Paint.Style.STROKE //画金色边框
                canvas.drawPath(drawRect(context, width, height), paint)
            }
        }
    }

    //金色边框
    private fun drawRect(context: Context, width: Int, height: Int): Path {
        val path = Path()
        val rectF = RectF(0.5f, 0.5f, width.toFloat() - 0.5f, height.toFloat() - 0.5f)
        val radii = floatArrayOf(dpToPx(context, 0f),
            dpToPx(context, 0f),
            dpToPx(context, 8f),
            dpToPx(context, 8f),
            dpToPx(context, 8f),
            dpToPx(context, 8f),
            dpToPx(context, 8f),
            dpToPx(context, 8f))
        path.addRoundRect(rectF, radii, Path.Direction.CW)
        return path
    }

    private fun dpToPx(context: Context, dipValue: Float): Float {
        val scale = context.applicationContext.resources.displayMetrics.density
        return dipValue * scale + 0.5f
    }

    private fun setColor(context: Context, data: ImMsgIn, chatType: Any): Int {
        return if (chatType == UiMsgType.GROUP_CHAT) {
            if (isSelfMessage) { //自己发送的消息
                if (data.getType() == UiMsgType.MSG_TYPE_QUESTION) {
                    if (data.getQuestionStatus() == 1 || data.getQuestionStatus() == 2) {
                        ContextCompat.getColor(context, R.color.im_msg_replied_bg)
                    } else {
                        if (!data.getPublished()) { //打赏消息状态
                            ContextCompat.getColor(context, R.color.im_msg_message_item_private)
                        } else ContextCompat.getColor(context, R.color.im_msg_bg_origin)
                    }
                } else if (data.getReplyMsgQuestionIsPublished() == false) {
                    ContextCompat.getColor(context, R.color.im_msg_bg_purple)
                } else if (data.getReplyMsgQuestionIsPublished() == true) {
                    ContextCompat.getColor(context, R.color.im_msg_bg_origin)
                } else ContextCompat.getColor(context, R.color.im_msg_bg_origin)
            } else if (data.getType() == UiMsgType.MSG_TYPE_QUESTION) {
                if (data.getQuestionStatus() == 1 || data.getQuestionStatus() == 2) ContextCompat.getColor(
                    context,
                    R.color.im_msg_replied_bg)
                else if (data.getQuestionStatus() == 0 && !data.getPublished()) {
                    ContextCompat.getColor(context, R.color.im_msg_message_item_private)
                } else {
                    ContextCompat.getColor(context, R.color.im_msg_bg_color_white)
                }
            } else if (data.getReplyMsgQuestionIsPublished() == false) ContextCompat.getColor(
                context,
                R.color.im_msg_message_item_private)
            else if (data.getReplyMsgQuestionIsPublished() == true) ContextCompat.getColor(context,
                R.color.im_msg_bg_color_white)
            else {
                ContextCompat.getColor(context, R.color.im_msg_bg_color_white)
            }
        } else {
            if (isSelfMessage) {
                ContextCompat.getColor(context, R.color.im_msg_bg_origin)
            } else { //其他人的消息
                if (data.getType() == UiMsgType.MSG_TYPE_QUESTION) {
                    if (data.getQuestionStatus() == 1) {
                        ContextCompat.getColor(context, R.color.im_msg_bg_color_white)
                    } else {
                        if (!data.getPublished()) { //打赏消息状态
                            ContextCompat.getColor(context, R.color.im_msg_message_item_private)
                        } else ContextCompat.getColor(context, R.color.im_msg_bg_color_white)
                    }
                } else {
                    ContextCompat.getColor(context, R.color.im_msg_bg_color_white)
                }
            }
        }
    }
}