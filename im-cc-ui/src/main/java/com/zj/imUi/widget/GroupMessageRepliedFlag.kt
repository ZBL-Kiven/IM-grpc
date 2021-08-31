package com.zj.imUi.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatTextView
import com.zj.imUi.R


@SuppressLint("InflateParams")
class GroupMessageRepliedFlag @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : androidx.appcompat.widget.AppCompatTextView(context, attributeSet, defStyle) {

    private var tvFlags: AppCompatTextView

    init {
        LayoutInflater.from(context).inflate(R.layout.im_msg_item_textview_replied_flag,null,true)
        tvFlags = findViewById(R.id.im_msg_item_textview_replied_reply_flag)
    }


}


