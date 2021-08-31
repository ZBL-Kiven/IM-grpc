package com.zj.imUi.base

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
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
        if (ivAvatar == null) ivAvatar = ImageView(context).apply { id = R.id.im_item_message_avatar }
        addViewToSelf(ivAvatar, getAvatarLayoutParams(data))
        onLoadAvatar(ivAvatar, data)
    }

    private fun initBubble(data: T) {
        if (bubbleView == null) {
            bubbleView = ImItemDispatcher.getItemWithData(data, context)
            bubbleView?.id = R.id.im_item_message_bubble
        }
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
        if (data.getSelfUserId() != data.getSenderId()) return
        if (ivSendStatusNo == null) ivSendStatusNo = ImageView(context).apply { id = R.id.im_item_message_send_lose }
        if (amSending == null) {
            amSending = ProgressBar(context).apply { id = R.id.im_item_message_sending }
            amSending?.indeterminateDrawable = ContextCompat.getDrawable(context, R.drawable.icon_countdown_ex)
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