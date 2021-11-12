package com.zj.imUi.widget

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager

import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.zj.imUi.R
import com.zj.imUi.UiMsgType
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imUi.utils.FlowLayoutManager
import com.zj.imUi.utils.SpaceItemDecoration
import com.zj.views.list.adapters.BaseAdapterDataSet
import com.zj.views.list.holders.BaseViewHolder
import com.zj.views.list.views.EmptyRecyclerView
import com.zj.views.ut.DPUtils
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference
import kotlin.math.max

class BasePopFlowWindow<T> :
    PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) {

    private var onReportOK: ((v: View?, data: T?, toString: String) -> Unit)? = null
    private var anchorView: SoftReference<View?>? = null
    private var data: ImMsgIn? = null
    private var isOwner: Boolean = false
    private var isSelfMessage: Boolean = false
    private var isNormalMsg: Boolean = false

    init {
        isFocusable = true
        isOutsideTouchable = true
    }

    fun show(data: T,
        v: View,
        onReportOK: ((v: View?, data: T?, reportContent: String) -> Unit)? = null) {
        if (data == null) return
        anchorView = SoftReference(v)
        this.onReportOK = onReportOK
        this.data = data as? ImMsgIn
        initData(v)
    }

    @SuppressLint("InflateParams")
    private fun initData(v: View) {
        v.post {
            if (isShowing) dismiss()
            contentView =
                LayoutInflater.from(v.context).inflate(R.layout.im_pop_new_content, null, false)
            showPop(v)
            initReportData(v)
        }
    }

    private fun showPop(anchor: View) {
        contentView.measure(makeDropDownMeasureSpec(width), makeDropDownMeasureSpec(height))
        val x = max((anchor.width - contentView.measuredWidth) / 2, 0)
        val y = max((anchor.height - contentView.measuredHeight) / 2, 0)
        showAsDropDown(anchor, x, -contentView.measuredHeight - y, Gravity.BOTTOM or Gravity.START)
    }


    private fun initReportData(v: View) {
        isSelfMessage = data?.getSenderId() == data?.getSelfUserId()
        isOwner = data?.getSelfUserId() == data?.getOwnerId()
        isNormalMsg =
            (data?.getMsgIsReject() == false && data?.getMsgIsSensitive() == false && data?.getMsgIsRecalled() == false)

        val ctx = WeakReference(v.context)
        val rv = contentView.findViewById<EmptyRecyclerView<String>>(R.id.im_pop_rv_content)
        val flowLayoutManager = FlowLayoutManager()
        rv.addItemDecoration(SpaceItemDecoration(DPUtils.dp2px(6f)))
        rv.layoutManager = flowLayoutManager
        val reply = ctx.get()?.getString(R.string.im_ui_msg_reply)
        val copy = ctx.get()?.getString(R.string.im_ui_msg_copy)
        val block = ctx.get()?.getString(R.string.im_ui_msg_block)
        val recall = ctx.get()?.getString(R.string.im_ui_msg_button_recall)
        val refuse = "Refuse"
        val report = "Report"
        val delete = ctx.get()?.getString(R.string.im_chat_delete)
        val reportItems = mutableListOf(reply, copy, recall, block, refuse, report, delete)
        val filterList: MutableList<String?> = mutableListOf()
        data?.apply {
            if (isNormalMsg) {
                if (isSelfMessage) {
                    filterList.add(reportItems[1])
                    data?.getSendState().let {
                        if (it != null) {
                            if (it < 0) filterList.add(reportItems[6])
                            else {
                                if (isOwner && data?.getReplyMsgType() != UiMsgType.MSG_TYPE_QUESTION) filterList.add(reportItems[2])
                            }
                        }
                    }
                } else { //不是自己的消息
                    if (data?.getType() == UiMsgType.MSG_TYPE_TEXT) filterList.add(reportItems[1])
                    if (data?.getType() != UiMsgType.MSG_TYPE_QUESTION) filterList.add(reportItems[0])
                    if (isOwner) {
                        if (data?.getQuestionStatus() == 0) {
                            filterList.add(reportItems[4])
                        }else{
                            filterList.add(reportItems[2])
                            filterList.add(reportItems[3])
                        }
                    }
//                    else filterList.add(reportItems[5])
                }
            }
        }
        rv?.setData(R.layout.im_pop_item_layout,
            false,
            filterList,
            object : BaseAdapterDataSet<String?>() {
                override fun initData(p0: BaseViewHolder<String?>?, p1: Int, reportItem: String?) {
                    val flowText = p0?.getView<TextView>(R.id.im_pop_item_tv)
                    flowText?.text = reportItem
                    flowText?.setTextColor(ContextCompat.getColor(contentView.context,
                        R.color.im_msg_text_color_white))

                }

                override fun onItemClick(position: Int, v: View?, m: String?) {
                    when (m) {
                        reply -> {
                            data?.getMsgId()?.let { data?.reply(it) }
                        }
                        copy -> {
                            val cm = v?.let {
                                ContextCompat.getSystemService(it.context,
                                    ClipboardManager::class.java)
                            }
                            val mClipData = ClipData.newPlainText("cc", data?.getTextContent())
                            cm?.setPrimaryClip(mClipData)
                        }
                        block -> {
                            data?.getSenderId()?.let { data?.block(it) }
                        }
                        recall -> {
                            data?.ownerRecallGroupMsg()
                        }
                        refuse -> {
                            data?.getMsgId()?.let { data?.rejectRewardMsg(it) }
                        }
                        report -> {
                            data?.getMsgId()?.let { data?.reportGroupUserMsg(it) }
                        }
                        delete -> {
                            data?.deleteSendLossMsg()
                        }
                        else -> {
                            Toast.makeText(v?.context, m.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                    dismiss()
                }
            })
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