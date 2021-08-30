package com.zj.imUi

import android.content.Context
import com.zj.imUi.base.BaseBubble
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imUi.items.IMBubbleContentItem
import com.zj.imUi.items.IMRewardItem

object ImItemDispatcher {

    inline fun <reified R : BaseBubble> getItemWithData(imIn: ImMsgIn, context: Context): R? {
        return when (imIn.getType()) {
            Constance.MSG_TYPE_QUESTION-> IMRewardItem(context) as R?
            else -> IMBubbleContentItem(context) as R?
        }
    }
}