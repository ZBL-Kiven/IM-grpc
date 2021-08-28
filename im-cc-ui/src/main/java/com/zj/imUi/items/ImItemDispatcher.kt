package com.zj.imUi.items

import android.content.Context
import com.zj.imUi.base.BaseBubble
import com.zj.imUi.interfaces.ImMsgIn

object ImItemDispatcher {

    inline fun <reified R : BaseBubble> getItemWithData(imIn: ImMsgIn, context: Context): R? {
        return when (imIn.getType()) {

            "text" -> IMTextItem(context) as R?

            else -> null

        }
    }
}