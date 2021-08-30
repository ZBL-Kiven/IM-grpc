package com.zj.imUi

import android.content.Context
import com.zj.imUi.base.BaseBubble
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imUi.items.IMImageItem
import com.zj.imUi.items.IMTextItem

object ImItemDispatcher {

    inline fun <reified R : BaseBubble> getItemWithData(imIn: ImMsgIn, context: Context): R? {
        return when (imIn.getType()) {

            Constance.MSG_TYPE_TEXT -> IMTextItem(context) as R?
            Constance.MSG_TYPE_IMG -> IMImageItem(context) as R?

            else -> IMTextItem(context) as R?

        }
    }
}