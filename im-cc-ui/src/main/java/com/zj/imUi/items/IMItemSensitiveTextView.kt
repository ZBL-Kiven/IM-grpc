package com.zj.imUi.items

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.zj.imUi.R
import com.zj.imUi.base.BaseBubble
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.views.ut.DPUtils

@SuppressLint("ResourceAsColor")
class IMItemSensitiveTextView @JvmOverloads constructor(context: Context,
    attrs: AttributeSet? = null,
    def: Int = 0) : BaseBubble(context, attrs, def) {


    private var tvContent: AppCompatTextView = AppCompatTextView(context)

    private var contentLayout: LinearLayout = LinearLayout(context, attrs, def)
    private var basePadding = DPUtils.dp2px(12f)

    init {
        //需设置成垂直的
        contentLayout.orientation = LinearLayout.VERTICAL
        val lp = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        lp.setMargins(basePadding,0,basePadding,0)
        contentLayout.layoutParams = lp

        tvContent.textSize = 13f
        tvContent.maxLines = 3
        tvContent.ellipsize =TextUtils.TruncateAt.END
        tvContent.gravity = Gravity.CENTER_HORIZONTAL
        tvContent.setPadding(basePadding,basePadding/2,basePadding,basePadding/2)
        tvContent.setTextColor(ContextCompat.getColor(context, R.color.im_msg_bg_color_white))
        tvContent.setBackgroundResource(R.drawable.im_msg_item_sensitive_cornor_bg)

        val lpTvL = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        lpTvL.gravity = Gravity.CENTER

        contentLayout.addView(tvContent,lpTvL)

    }


    override fun init(data: ImMsgIn, chatType: Any) {
        if (childCount == 0){
            addView(contentLayout)
        }
        if (data.getMsgIsSensitive()) {
            tvContent.text = data.getExtSensitiveMsgContent()
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