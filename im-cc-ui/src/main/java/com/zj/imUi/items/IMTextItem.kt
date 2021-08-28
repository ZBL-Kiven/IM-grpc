package com.zj.imUi.items

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import com.zj.imUi.R
import com.zj.imUi.base.BaseBubble
import com.zj.imUi.interfaces.ImMsgIn

class IMTextItem @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0) : BaseBubble(context, attrs, def) {

    private val tvName: TextView
    private val tvContent: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.im_item_bubble_text_content, this, true)
        tvName = findViewById(R.id.im_item_bubble_text_content_tv_name)
        tvContent = findViewById(R.id.im_item_bubble_text_content_tv_content)
    }

    override fun init(data: ImMsgIn) {
        tvName.text = data.getSenderName()
        tvContent.text = data.getTextContent()
    }

    override fun onResume() {

    }

    override fun onStop() {

    }

    override fun onDestroy() {
        removeAllViews()
    }
}