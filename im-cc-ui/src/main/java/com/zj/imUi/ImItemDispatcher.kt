package com.zj.imUi

import android.content.Context
import com.zj.imUi.base.BaseBubble
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imUi.items.*

object ImItemDispatcher {
    fun getItemWithData(imIn: ImMsgIn, context: Context): BaseBubble {
        return when (imIn.getUiTypeWithMessageType()) {
            UiMsgType.MSG_TYPE_CC_LIVE -> IMContentCCLiveView(context)
            UiMsgType.MSG_TYPE_QUESTION -> IMRewardItem(context)
            UiMsgType.MSG_TYPE_CC_VIDEO -> IMContentCCVideoView(context)
            UiMsgType.MSG_TYPE_IMG, UiMsgType.MSG_TYPE_TEXT, UiMsgType.MSG_TYPE_AUDIO -> IMBubbleContentItem(context)
            UiMsgType.MSG_TYPE_RECALLED -> IMItemRecallTextView(context)
            UiMsgType.MSG_TYPE_SENSITIVE -> IMItemSensitiveTextView(context)
            UiMsgType.MSG_TYPE_SYS_REFUSE -> IMItemSystemRefuseTextView(context)
            UiMsgType.MSG_TYPE_CC_EMOTION->IMBubbleContentItem(context)
            else -> IMBubbleNotAllowedTypeItem(context)
        }
    }
}