package com.zj.imUi.base

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.zj.imUi.bubble.BubbleRenderer
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imUi.ImItemDispatcher
import com.zj.imUi.R
import com.zj.imUi.UiMsgType
import com.zj.imUi.widget.BasePopFlowWindow

@Suppress("unused")
abstract class BaseImItem<T : ImMsgIn> @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0) : RelativeLayout(context, attrs, def), BaseBubbleConfig<T> {

    companion object {
        const val NOTIFY_CHANGE_AUDIO = "notify_change_audio"
        const val NOTIFY_CHANGE_VIDEO = "notify_change_video"
        const val NOTIFY_CHANGE_SENDING_STATE = "notify_change_send_state"
        const val NOTIFY_CHANGE_REWARD_STATE = "notify_change_reward_state"
        const val NOTIFY_CHANGE_BTN_ENABLED = "notify_change_reply_btn_enable"

        fun onLog(s: String) {
            Log.e("------ BaseImItem", "  error case: $s")
        }
    }

    private var curData: T? = null
    private var lastDataType: String? = null
    open var type: Any? = null
    open var tvNickname: TextView? = null
    open var bubbleView: BaseBubble? = null
    open var ivAvatar: ImageView? = null
    open var ivSendStatus: ImageView? = null

    fun setData(data: T?, chatType: Any?) {
        if (data == null) {
            onLog("set data failed ,the data must not be null!!")
        } else {
            this.curData = data
            this.type = chatType
            removeAllViews()
            initBubble(data, type)
            initAvatar(data)
            initSendStatus(data)
            lastDataType = data.getUiTypeWithMessageType()
        }
    }

    open fun initName(data: T) {
        if (data.getSelfUserId() == data.getSenderId()) {
            removeIfNotContains(tvNickname, true)
            return
        }
        if (tvNickname == null) {
            tvNickname = TextView(context)
            tvNickname?.id = R.id.im_item_message_nickname
        }
        addViewToSelf(tvNickname, getTvNickNameLayoutParams(data))
        tvNickname?.text = data.getSenderName()
        tvNickname?.setTextColor(ContextCompat.getColor(context, R.color.im_msg_text_color_gray_nickname))
    }

    fun notifyChange(d: T?, pl: Any?) {
        if (d != curData) curData = d
        when (pl) {
            NOTIFY_CHANGE_SENDING_STATE -> setLoadingState(d)
        }
        bubbleView?.notifyChange(pl)
    }

    open fun initAvatar(data: T) {
        if (data.getSelfUserId() == data.getSenderId() || data.getMsgIsRecalled() || data.getMsgIsSensitive() || data.getUiTypeWithMessageType() == UiMsgType.MSG_TYPE_SYS_REFUSE||data.getUiTypeWithMessageType() == UiMsgType.MSG_TYPE_CC_GIFT) {
            removeIfNotContains(ivAvatar, true)
            return
        }
        if (ivAvatar == null) {
            ivAvatar = ImageView(context)
            ivAvatar?.id = R.id.im_item_message_avatar
        }
        addViewToSelf(ivAvatar, getAvatarLayoutParams(data))
        onLoadAvatar(ivAvatar, data)
        ivAvatar?.setOnClickListener {
            data.jumpToUserHomePage()
        }
    }

    private fun initBubble(data: T, chatType: Any?) {
        val dataType = data.getUiTypeWithMessageType()
        if (bubbleView != null) {
            val curQuestion = lastDataType == UiMsgType.MSG_TYPE_QUESTION
            if (curQuestion != (dataType == UiMsgType.MSG_TYPE_QUESTION)) {
                onDestroyed()
                bubbleView = null
            } //  ????????????????????????cc_video????????????????????????
            val curCCVideo = lastDataType == UiMsgType.MSG_TYPE_CC_VIDEO
            if (curCCVideo != (dataType == UiMsgType.MSG_TYPE_CC_VIDEO)) {
                onDestroyed()
                bubbleView = null
            }
            val curCCLive = lastDataType == UiMsgType.MSG_TYPE_CC_LIVE
            if (curCCLive != (dataType == UiMsgType.MSG_TYPE_CC_LIVE)) {
                onDestroyed()
                bubbleView = null
            }
            val curRecall = lastDataType == UiMsgType.MSG_TYPE_RECALLED
            if (curRecall != (dataType == UiMsgType.MSG_TYPE_RECALLED)) {
                onDestroyed()
                bubbleView = null
            }
            val curSensitive = lastDataType == UiMsgType.MSG_TYPE_SENSITIVE
            if (curSensitive != (dataType == UiMsgType.MSG_TYPE_SENSITIVE)) {
                onDestroyed()
                bubbleView = null
            }
            val curRefuse = lastDataType == UiMsgType.MSG_TYPE_SYS_REFUSE
            if (curRefuse != (dataType == UiMsgType.MSG_TYPE_SYS_REFUSE)) {
                onDestroyed()
                bubbleView = null
            }
            val curEmotion = lastDataType == UiMsgType.MSG_TYPE_CC_EMOTION
            if (curEmotion != (dataType == UiMsgType.MSG_TYPE_CC_EMOTION)) {
                onDestroyed()
                bubbleView = null
            }
            val curGift = lastDataType == UiMsgType.MSG_TYPE_CC_GIFT
            if (curGift != (dataType == UiMsgType.MSG_TYPE_CC_GIFT)) {
                onDestroyed()
                bubbleView = null
            }
            val curNoneType = lastDataType != UiMsgType.MSG_NONE_MSG_TYPE
            if (curNoneType != (dataType == UiMsgType.MSG_NONE_MSG_TYPE)) {
                onDestroyed()
                bubbleView = null
            }
        }
        if (bubbleView == null) {
            bubbleView = ImItemDispatcher.getItemWithData(data, context)
            bubbleView?.id = R.id.im_item_message_bubble
        }
        bubbleView?.setBubbleRenderer(getBubbleRenderer(data))
        addViewToSelf(bubbleView, getBubbleLayoutParams(data))
        bubbleView?.onSetData({ data }, chatType as Int?)
        val isNormalMsg = !data.getMsgIsRecalled()
                && !data.getMsgIsReject()
                && !data.getMsgIsSensitive()
                && dataType != UiMsgType.MSG_TYPE_CC_LIVE
                && dataType != UiMsgType.MSG_TYPE_CC_VIDEO
                && dataType != UiMsgType.MSG_TYPE_QUESTION
                && dataType != UiMsgType.MSG_TYPE_CC_EMOTION
                && dataType != UiMsgType.MSG_TYPE_CC_GIFT
        bubbleView?.setOnLongClickListener {
            if (isNormalMsg && chatType == UiMsgType.GROUP_CHAT) {
                val popFlowWindow: BasePopFlowWindow<ImMsgIn> = BasePopFlowWindow()
                popFlowWindow.show(data, it, type as Int) { _, _, _ ->
                }
            }
            true
        }
    }

    private fun initSendStatus(data: T) {
        if (data.getSelfUserId() != data.getSenderId()) {
            removeIfNotContains(ivSendStatus, true)
            return
        }
        if (ivSendStatus == null) {
            ivSendStatus = ImageView(context)
            ivSendStatus?.id = R.id.im_item_message_send_state
        }
        val loadingLp = getSendingLayoutParams(data)
        addViewToSelf(ivSendStatus, loadingLp)
        setLoadingState(data)
        ivSendStatus?.setOnClickListener {
            curData?.resend()
        }
    }

    private fun setLoadingState(d: T?) {
        if (d == null) return
        val res = when {
            d.getSendState() < 0 -> R.drawable.im_msg_item_widget_reward_icon_sendlose
            d.getSendState() in 1..2 -> R.drawable.im_msg_item_img_loading
            else -> android.R.color.transparent
        }
        ivSendStatus?.setImageResource(res)
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
                it.removeView(view)
                true
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