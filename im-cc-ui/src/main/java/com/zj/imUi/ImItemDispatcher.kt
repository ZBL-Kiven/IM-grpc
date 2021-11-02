package com.zj.imUi

import android.content.Context
import com.zj.imUi.base.BaseBubble
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imUi.items.*


@Suppress("unused")
enum class MsgType(val type: String) {

    TEXT("text"), IMG("img"), AUDIO("audio"), VIDEO("video"), QUESTION("question");

    companion object {
        fun hasNormalMsg(type: String?): Boolean {
            return type == TEXT.type || type == IMG.type || type == AUDIO.type
        }
    }

}

object ImItemDispatcher {
    fun  getItemWithData(imIn: ImMsgIn, context: Context): BaseBubble {
        return if (imIn.getMsgIsRecalled() == true){
            IMItemSensitiveTextView(context)
        }else when (imIn.getType()) {
            UiMsgType.MSG_TYPE_QUESTION -> IMRewardItem(context)
            UiMsgType.MSG_TYPE_CC_VIDEO -> IMContentCCVideoView(context)
            UiMsgType.MSG_TYPE_IMG, UiMsgType.MSG_TYPE_TEXT, UiMsgType.MSG_TYPE_AUDIO -> IMBubbleContentItem(context)
            else -> IMBubbleNotAllowedTypeItem(context)
        }
    }
}