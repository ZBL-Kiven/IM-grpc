package com.zj.imUi.items

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.zj.imUi.R
import com.zj.imUi.base.BaseBubble
import com.zj.imUi.interfaces.ImMsgIn

class IMImageItem @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0) : BaseBubble(context, attrs, def) {

    private val tvName: TextView
    private val ivContent: ImageView

    init {
        LayoutInflater.from(context).inflate(R.layout.im_item_bubble_img_content, this, true)
        tvName = findViewById(R.id.im_item_bubble_text_content_tv_name)
        ivContent = findViewById(R.id.im_item_bubble_text_content_iv_content)
    }

    override fun init(data: ImMsgIn) {
        tvName.text = data.getSenderName()
        Glide.with(ivContent).load(data.getThumb()).into(ivContent)
    }

    override fun onResume() {

    }

    override fun onStop() {

    }

    override fun onDestroy() {
        removeAllViews()
    }
}