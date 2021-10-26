package com.zj.imUi.widget

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import com.zj.imUi.R
import com.zj.imUi.UiMsgType
import com.zj.imUi.interfaces.ImMsgIn
import kotlin.math.max


@SuppressLint("InflateParams")
class MsgPop(context: Context, data: ImMsgIn) {

    private val popWindow: PopupWindow = PopupWindow(context)
    private val isOwner = data.getSelfUserId() == data.getOwnerId()
    private var isSelfMessage = data.getSelfUserId() == data.getSenderId()

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.im_msg_pop, null, false)
        val copy = view.findViewById<TextView>(R.id.im_msg_pop_copy).apply {
            visibility = if (data.getType() == UiMsgType.MSG_TYPE_TEXT) View.VISIBLE else View.GONE
            setOnClickListener {
                val cm = getSystemService(context, ClipboardManager::class.java)
                val mClipData = ClipData.newPlainText("cc", data.getTextContent())
                cm?.setPrimaryClip(mClipData)
                popWindow.dismiss()
            }
        }
        val reply = view.findViewById<TextView>(R.id.im_msg_pop_reply).apply {
            visibility = if (data.getType() != UiMsgType.MSG_TYPE_QUESTION && data.getSelfUserId() != data.getSenderId()) View.VISIBLE else View.GONE
            setOnClickListener {
                data.reply(data.getMsgId())
                popWindow.dismiss()
            }
        }
        val block = view.findViewById<TextView>(R.id.im_msg_pop_block).apply {
            visibility = if (data.getOwnerId() == data.getSelfUserId() && data.getSelfUserId() != data.getSenderId()) {
                View.VISIBLE
            } else {
                View.GONE
            }
            setOnClickListener {
                data.getSenderId()?.let { data.block(it) }
                popWindow.dismiss()
            }
        }


        val delete = view.findViewById<TextView>(R.id.im_msg_pop_delete).apply {
            visibility = if (data.getSendState() <0 ) {
                View.VISIBLE
            } else {
                View.GONE
            }
            setOnClickListener {
                data.deleteSendLossMsg()
                popWindow.dismiss()
            }
        }

        val recall = view.findViewById<TextView>(R.id.im_msg_pop_recall).apply {
            visibility = if (isOwner&&isSelfMessage) {
                View.VISIBLE
            } else {
                View.GONE
            }
            setOnClickListener {
                data.ownerRecallGroupMsg()
                popWindow.dismiss()
            }
        }

        view.findViewById<View>(R.id.im_msg_pop_line3).visibility = delete.visibility
        view.findViewById<View>(R.id.im_msg_pop_line4).visibility = recall.visibility
        view.findViewById<View>(R.id.im_msg_pop_line1).visibility = copy.visibility
        val visibility = if (reply.visibility == View.VISIBLE || block.visibility == View.VISIBLE) {
            View.VISIBLE
        } else {
            View.GONE
        }
        view.findViewById<View>(R.id.im_msg_pop_line2).visibility = visibility
        popWindow.contentView = view
        popWindow.width = ViewGroup.LayoutParams.WRAP_CONTENT
        popWindow.height = (context.resources.displayMetrics.density * 35).toInt()
        popWindow.setBackgroundDrawable(ColorDrawable())
        popWindow.isOutsideTouchable = true
        popWindow.isTouchable = true
    }

    fun show(anchor: View) {
        popWindow.contentView.measure(makeDropDownMeasureSpec(popWindow.width), makeDropDownMeasureSpec(popWindow.height))
        val x = max((anchor.width - popWindow.contentView.measuredWidth) / 2, 0)
        val y = max((anchor.height - popWindow.contentView.measuredHeight) / 2, 0)
        popWindow.showAsDropDown(anchor, x, -popWindow.contentView.measuredHeight - y, Gravity.BOTTOM or Gravity.START)
    }

    private fun makeDropDownMeasureSpec(measureSpec: Int): Int {
        val mode: Int = if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
            View.MeasureSpec.UNSPECIFIED
        } else {
            View.MeasureSpec.EXACTLY
        }
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode)
    }
}