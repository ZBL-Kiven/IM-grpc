package com.zj.imUi.base

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.zj.imUi.bubble.BubbleRenderer
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imUi.ImItemDispatcher
import com.zj.imUi.R
import com.zj.imUi.UiMsgType
import com.zj.imUi.widget.MsgPop

@Suppress("unused")
abstract class BaseImItem<T : ImMsgIn> @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0) : RelativeLayout(context, attrs, def), BaseBubbleConfig<T> {

    companion object {
        const val NOTIFY_CHANGE_AUDIO = "notify_change_audio"
        const val NOTIFY_CHANGE_VIDEO = "notify_change_video"
        const val NOTIFY_CHANGE_SENDING_STATE = "notify_change_send_state"

        fun onLog(s: String) {
            Log.e("------ BaseImItem", "  error case: $s")
        }
    }

    private var curData: T? = null
    open var bubbleView: BaseBubble? = null
    open var ivAvatar: ImageView? = null
    open var ivSendStatusNo: ImageView? = null
    open var amSending: ProgressBar? = null

    fun setData(data: T?) {
        if (data == null) {
            onLog("set data failed ,the data must not be null!!")
        } else {
            this.curData = data
            removeAllViews()
            initAvatar(data)
            initBubble(data)
            initSendStatus(data)
        }
    }

    fun notifyChange(d: T?, pl: Any?) {
        if (d != curData) curData = d
        when (pl) {
            NOTIFY_CHANGE_SENDING_STATE -> setLoadingState(d)
            else -> bubbleView?.notifyChange(pl)
        }
    }

    private fun initAvatar(data: T) {
        if (ivAvatar == null) ivAvatar = ImageView(context)
        addViewToSelf(ivAvatar, getAvatarLayoutParams(data))
        onLoadAvatar(ivAvatar, data)
    }

    private fun initBubble(data: T) {
        if (bubbleView == null) bubbleView = ImItemDispatcher.getItemWithData(data, context)
        bubbleView?.setBubbleRenderer(getBubbleRenderer(data))
        addViewToSelf(bubbleView, getBubbleLayoutParams(data))
        bubbleView?.onSetData { curData }
        bubbleView?.setOnLongClickListener {
            val isNotSelf = data.getSelfUserId() != data.getSenderId()
            if (data.getType() == UiMsgType.MSG_TYPE_TEXT || isNotSelf) {
                MsgPop(context, data).show(it)
            }
            true
        }
    }

    private fun initSendStatus(data: T) {
        if (data.getSelfUserId() != data.getSenderId()) {
            removeIfNotContains(ivSendStatusNo, true)
            removeIfNotContains(amSending, true)
            return
        }
        if (ivSendStatusNo == null) ivSendStatusNo = ImageView(context)
        if (amSending == null) {
            amSending = ProgressBar(context)
        }
        val loadingLp = getSendingLayoutParams(data)
        addViewToSelf(ivSendStatusNo, loadingLp)
        addViewToSelf(amSending, loadingLp)
        ivSendStatusNo?.setImageResource(R.drawable.icon_sendlose)
        setLoadingState(data)
    }

    private fun setLoadingState(d: T?) {
        if (d == null) return
        ivSendStatusNo?.visibility = View.GONE
        amSending?.visibility = View.GONE
        (if (d.getSendState() < 0) ivSendStatusNo else if (d.getSendState() in 1..2) amSending else null)?.visibility = View.VISIBLE
    }

    private fun addViewToSelf(view: View?, layoutParams: LayoutParams) {
        if (view == null) return
        val needAdd = removeIfNotContains(view) ?: true
        view.layoutParams = layoutParams
        if (needAdd) addView(view)
    }

    private fun removeIfNotContains(view: View?, removeOnly: Boolean = false): Boolean? {
        return (view?.parent as? ViewGroup)?.let {
            if (removeOnly || it != this@BaseImItem) {
                it.removeView(view);true
            } else false
        }
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