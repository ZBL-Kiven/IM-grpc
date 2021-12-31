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
    private var mllLoading: LinearLayout? = null
    private var mllPrice: LinearLayout? = null
    private var mllRetry: LinearLayout? = null
    private var mTvRetry: TextView? = null

    init {
        View.inflate(context, R.layout.view_pay_page, this)
        initView()
    }

    private fun initView() {
        findViewById<LinearLayout>(R.id.im_emotion_ll_pay).setOnClickListener {
            payAction?.invoke()
        }
        mTvPrice = findViewById(R.id.im_emotion_tv_price)
        mllPrice = findViewById(R.id.im_emotion_ll_price)
        mllLoading = findViewById(R.id.im_emotion_ll_loading)
        mTvRetry = findViewById(R.id.im_emotion_tv_retry)
        mllRetry = findViewById(R.id.im_emotion_ll_retry)
    }

    fun showData(price: Int? = 0) {
        mTvPrice?.text = "$price"
        mllLoading?.visibility = View.GONE
        mTvRetry?.visibility = View.GONE
    }

    fun showLoading() {
        mllPrice?.visibility = View.GONE
        mllLoading?.visibility = View.VISIBLE
        mllRetry?.visibility = View.GONE
    }

    fun showError() {
        mllPrice?.visibility = View.GONE
        mllLoading?.visibility = View.GONE
        mllRetry?.visibility = View.VISIBLE
    }

    fun setRetryOnClickListener(onClickListener: OnClickListener) {
        mTvRetry?.setOnClickListener(onClickListener)
    }
}