package com.zj.imUi.widget
import android.annotation.SuppressLint
import android.view.*
import android.widget.*
import androidx.appcompat.widget.AppCompatTextView
import com.zj.imUi.R
import com.zj.imUi.interfaces.ImMsgIn
import java.lang.ref.SoftReference
import kotlin.math.max

class BasePopFlowRejectWindow<T> : PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) {

    private var onReportOK: ((v: View?, data: T?, toString: String) -> Unit)? = null
    private var anchorView: SoftReference<View?>? = null
    private var data: ImMsgIn? = null
    private var isOwner: Boolean = false
    private var isSelfMessage: Boolean = false
    init {
        isFocusable = true
        isOutsideTouchable = true
    }

    fun show(data: T, v: View, onReportOK: ((v: View?, data: T?, reportContent: String) -> Unit)? = null) {
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
            contentView = LayoutInflater.from(v.context).inflate(R.layout.im_pop_new_reject_content, null, false)
            //充满父布局
            width = v.width
            height = v.height
            initReportData()
            showPop(v)
        }
    }

    private fun initReportData() {
        isSelfMessage = data?.getSenderId() == data?.getSelfUserId()
        isOwner = data?.getSelfUserId() == data?.getOwnerId()
        val content = contentView.findViewById<AppCompatTextView>(R.id.im_pop_new_reject_content_tv)
        content.setOnClickListener {
            data?.getMsgId()?.let { it1 -> data?.rejectRewardMsg(it1) }
            dismiss()
        }

    }

    private fun showPop(anchor: View) {
        contentView.measure(makeDropDownMeasureSpec(width), makeDropDownMeasureSpec(height))
        val x = max((anchor.width - contentView.measuredWidth) / 2, 0)
        val y = max((anchor.height - contentView.measuredHeight) / 2, 0)
        showAsDropDown(anchor, x, -contentView.measuredHeight - y, Gravity.CENTER)
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