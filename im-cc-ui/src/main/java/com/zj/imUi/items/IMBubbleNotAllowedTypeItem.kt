package com.zj.imUi.items

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.zj.imUi.R
import com.zj.imUi.base.BaseBubble
import com.zj.imUi.interfaces.ImMsgIn

/**
 * author: 李 祥
 * date:   2021/9/14 11:04 下午
 * description:
 */
class IMBubbleNotAllowedTypeItem @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0) : BaseBubble(context, attrs, def) {

    private val tvName: AppCompatTextView
    private val iconIsOwner: AppCompatImageView
    private val llName:LinearLayout
    private val tvTip:IMContentTextView

    init {
        LayoutInflater.from(context).inflate(R.layout.im_msg_bubble_content_not_allowed_type,this,true)
        tvName = findViewById(R.id.im_msg_bubble_content_not_allowed_type_tv_nickname)
        iconIsOwner = findViewById(R.id.im_msg_bubble_content_not_allowed_type_img_owner)
        llName = findViewById(R.id.im_msg_bubble_content_not_allowed_type_ll_title)
        tvTip = findViewById(R.id.im_msg_bubble_content_not_allowed_type_tv_tip)
    }
    override fun init(data: ImMsgIn,chatType:Any) {
        if (data.getSelfUserId() == data.getSenderId()) {
            llName.visibility = View.GONE
        } else {
            llName.visibility = View.VISIBLE
            if (data.getSenderId() == data.getOwnerId()) iconIsOwner.visibility = View.VISIBLE
            else iconIsOwner.visibility = View.GONE
        }
        tvName.text = data.getSenderName()
        tvTip.onSetData(data)

    }

    override fun onResume() {}

    override fun onStop() {}

    override fun onDestroy() {
        removeAllViews()
    }


}



