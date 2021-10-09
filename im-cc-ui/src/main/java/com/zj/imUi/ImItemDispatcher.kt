package com.zj.imUi

import android.content.Context
import com.zj.imUi.base.BaseBubble
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imUi.items.IMBubbleContentItem
import com.zj.imUi.items.IMBubbleNotAllowedTypeItem
import com.zj.imUi.items.IMContentCCVideoView
import com.zj.imUi.items.IMRewardItem


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

    inline fun <reified R : BaseBubble> getItemWithData(imIn: ImMsgIn, context: Context,isGroupChat:Boolean): R? {
        return if(isGroupChat) {
            when (imIn.getType()) {
                UiMsgType.MSG_TYPE_QUESTION -> IMRewardItem(context) as R?
                UiMsgType.MSG_TYPE_CC_VIDEO -> IMContentCCVideoView(context) as R?
                UiMsgType.MSG_TYPE_IMG, UiMsgType.MSG_TYPE_TEXT, UiMsgType.MSG_TYPE_AUDIO -> IMBubbleContentItem(
                    context) as R?
                else -> IMBubbleNotAllowedTypeItem(context) as R?
            }
        }else
            when (imIn.getType()) {
                UiMsgType.MSG_TYPE_QUESTION -> IMRewardItem(context) as R?
                UiMsgType.MSG_TYPE_CC_VIDEO -> IMContentCCVideoView(context) as R?
                UiMsgType.MSG_TYPE_IMG, UiMsgType.MSG_TYPE_TEXT, UiMsgType.MSG_TYPE_AUDIO -> IMBubbleContentItem(
                    context) as R?
                else -> IMBubbleNotAllowedTypeItem(context) as R?
            }
    }
}