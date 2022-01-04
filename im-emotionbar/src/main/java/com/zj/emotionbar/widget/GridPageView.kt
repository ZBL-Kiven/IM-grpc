package com.zj.emotionbar.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zj.emotionbar.R
import com.zj.emotionbar.interfaces.GridPageClickListener

/**
 * @author: JayQiu
 * @date: 2021/12/9
 * @description:
 */
class GridPageView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null, def: Int = 0) : FrameLayout(context, attr, def) {
    private var mRvList: RecyclerView? = null
    private var mllLoading: LinearLayout? = null
    private var mTvRetry: TextView? = null
    private var mllRetry: LinearLayout? = null
    private var mTvPrice: TextView? = null
    private var mllPrice: LinearLayout? = null
    private var mllPay: LinearLayout? = null
    var onGridPageClickListener: GridPageClickListener? = null

    init {
        View.inflate(context, R.layout.view_grid_page, this)
        initView()
    }

    private fun initView() {
        mllLoading = findViewById(R.id.im_emotion_ll_loading)
        mRvList = findViewById(R.id.im_emotion_ll_grid)
        mTvRetry = findViewById(R.id.im_emotion_tv_retry)
        mllRetry = findViewById(R.id.im_emotion_ll_retry) //
        mTvPrice = findViewById(R.id.im_emotion_tv_price)
        mllPrice = findViewById(R.id.im_emotion_ll_price)
        mllPay = findViewById(R.id.im_emotion_ll_pay)
        mTvRetry?.setOnClickListener {
            onGridPageClickListener?.onRetryClickListener()
        }
        mllPay?.setOnClickListener {
            onGridPageClickListener?.onPayClickListener()
        }
    }

    fun showData() {
        mRvList?.visibility = View.VISIBLE
        mllLoading?.visibility = View.GONE
        mllRetry?.visibility = View.GONE
    }

    fun showPrice(price: Int? = 0) {
        mTvPrice?.text = "$price"
        mllLoading?.visibility = View.GONE
        mllRetry?.visibility = View.GONE
        mllPrice?.visibility = View.VISIBLE
    }

    fun showLoading() {
        mRvList?.visibility = View.GONE
        mllLoading?.visibility = View.VISIBLE
        mllRetry?.visibility = View.GONE
        mllPrice?.visibility = View.GONE
    }

    fun showError() {
        mRvList?.visibility = View.GONE
        mllLoading?.visibility = View.GONE
        mllRetry?.visibility = View.VISIBLE
        mllPrice?.visibility = View.GONE
    }

    fun getRecyclerView(): RecyclerView? = mRvList

}