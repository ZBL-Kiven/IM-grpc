package com.zj.imUi.items

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.zj.imUi.R
import com.zj.imUi.UiMsgType
import com.zj.imUi.base.BaseBubble
import com.zj.imUi.interfaces.ImMsgIn

@SuppressLint("ResourceAsColor")
class IMItemRecallTextView @JvmOverloads constructor(context: Context,
    attrs: AttributeSet? = null,
    def: Int = 0) : BaseBubble(context, attrs, def) {


    private var tvContent: AppCompatTextView = AppCompatTextView(context)

    private var contentLayout: LinearLayout = LinearLayout(context, attrs, def)

    init {
        //需设置成垂直的
        contentLayout.orientation = LinearLayout.VERTICAL
        val lp = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        lp.setMargins(12,0,12,0)
        contentLayout.layoutParams = lp

        tvContent.textSize = 12f
        tvContent.setTextColor(ContextCompat.getColor(context, R.color.im_msg_text_color_black))

        val lpTv = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        lpTv.gravity = Gravity.CENTER

        contentLayout.addView(tvContent,lpTv)

    }


    override fun init(data: ImMsgIn, chatType: Any) {
        if (childCount == 0){
            addView(contentLayout)
        }
        if (data.getMsgIsRecalled() == true) {
            tvContent.text = "群主撤回了一条消息"
        }
    }

    override fun onResume() {
    }

    override fun onStop() {
    }

    override fun onDestroy() {
    }


}