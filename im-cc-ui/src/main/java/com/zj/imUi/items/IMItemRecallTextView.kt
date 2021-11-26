package com.zj.imUi.items

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.zj.imUi.R
import com.zj.imUi.base.BaseBubble
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.views.ut.DPUtils

@SuppressLint("ResourceAsColor")
class IMItemRecallTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0) : BaseBubble(context, attrs, def) {

    private var tvContent: AppCompatTextView
    private var contentLayout: View = LayoutInflater.from(context).inflate(R.layout.im_msg_bubble_content_recall, this, false)
    private var basePadding = DPUtils.dp2px(12f)

    init {
        with(contentLayout) {
            tvContent = findViewById(R.id.im_msg_bubble_content_recall_tv)
        }
        tvContent.textSize = 13f
        tvContent.gravity = Gravity.CENTER_HORIZONTAL
        tvContent.setPadding(basePadding, basePadding / 2, basePadding, basePadding / 2)
        tvContent.setTextColor(ContextCompat.getColor(context, R.color.im_msg_bg_color_white))
        tvContent.setBackgroundResource(R.drawable.im_msg_item_sensitive_cornor_bg)
    }


    override fun init(data: ImMsgIn, chatType: Any) {
        if (childCount == 0) {
            addView(contentLayout)
        }
        if (data.getMsgIsRecalled()) {
            if (data.getRecallContent(context)!=null){
                tvContent.text = data.getRecallContent(context)
            }else when (data.getMsgRecallRole()) {
                1 -> tvContent.text = context.getString(R.string.im_chat_recall_owner_text)
                2 -> tvContent.text = context.getString(R.string.im_chat_recall_admin_text)
                else -> {
                    Log.e("im-ui:role",data.getMsgRecallRole().toString())
                }
            }
        }
    }

    override fun onResume() {
    }

    override fun onStop() {
    }

    override fun onDestroy() {
        removeAllViews()
    }

}