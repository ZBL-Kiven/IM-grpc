package com.zj.imUi.base

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.zj.imUi.Constance
import com.zj.imUi.ImItemDispatcher
import com.zj.imUi.R
import com.zj.imUi.bubble.BubbleRenderer
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imUi.widget.MsgPop

@Suppress("unused")
abstract class BaseImItem<T : ImMsgIn> @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0) : RelativeLayout(context, attrs, def), BaseBubbleConfig<T> {

    companion object {

        fun onLog(s: String) {
            Log.e("------ BaseImItem", "  error case: $s")
        }
    }

    open var bubbleView: BaseBubble? = null
    open var ivAvatar: ImageView? = null

    fun setData(data: T?) {
        if (data == null) {
            onLog("set data failed ,the data must not be null!!")
        } else {
            initAvatar(data)
            initBubble(data)
        }
    }

    private fun initAvatar(data: T) {
        if (ivAvatar == null) ivAvatar = ImageView(context).apply { id = R.id.im_item_message_avatar }
        addViewToSelf(ivAvatar, getAvatarLayoutParams(data))
        onLoadAvatar(ivAvatar, data)
    }

    private fun initBubble(data: T) {
        if (bubbleView == null) bubbleView = ImItemDispatcher.getItemWithData(data, context)
        bubbleView?.setBubbleRenderer(getBubbleRenderer(data))
        addViewToSelf(bubbleView, getBubbleLayoutParams(data))
        bubbleView?.onSetData(data)
        if (!(data.getType() != Constance.MSG_TYPE_TEXT && data.getSelfUserId() == data.getSenderId())) {
            bubbleView?.setOnLongClickListener {
                MsgPop(context, data).show(it)
                true
            }
        }
    }

    private fun addViewToSelf(view: View?, layoutParams: LayoutParams) {
        if (view == null) return
        var needAdd = true
        (view.parent as? ViewGroup)?.let {
            needAdd = if (it != this@BaseImItem) {
                removeView(view);true
            } else false
        }
        view.layoutParams = layoutParams
        if (needAdd) addView(view)
    }

    open fun getBubbleRenderer(data: T): BaseBubbleRenderer? {
        return BubbleRenderer
    }

    open fun onResume() {
        bubbleView?.onResume()
    }

    open fun onStop() {
        bubbleView?.onStop()
    }

    open fun onDestroyed() {
        onStop()
        bubbleView?.onDestroyed()
        removeAllViews()
    }
}