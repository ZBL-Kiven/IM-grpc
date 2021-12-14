package com.zj.emotionbar.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.zj.emotionbar.R
import com.zj.emotionbar.data.EmoticonPack

/**
 * @author: JayQiu
 * @date: 2021/12/9
 * @description:
 */
class PayPageView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null, def: Int = 0, private val payAction: (() -> Unit)?) : LinearLayout(context, attr, def) {
    private var mTvPrice: TextView? = null

    init {
        View.inflate(context, R.layout.view_pay_page, this)
        initView()
    }

    private fun initView() {
        findViewById<LinearLayout>(R.id.im_emotion_ll_pay).setOnClickListener {
            payAction?.invoke()
        }
        mTvPrice = findViewById(R.id.im_emotion_tv_Price)
    }

    fun showData(price: Int? = 0) {
        mTvPrice?.text = "$price"
    }
}