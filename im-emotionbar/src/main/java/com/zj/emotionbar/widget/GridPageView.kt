package com.zj.emotionbar.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zj.emotionbar.R

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

    init {
        View.inflate(context, R.layout.view_grid_page, this)
        initView()
    }

    private fun initView() {
        mllLoading = findViewById(R.id.im_emotion_ll_loading)
        mRvList = findViewById(R.id.im_emotion_ll_grid)
        mTvRetry = findViewById(R.id.im_emotion_tv_retry)
        mllRetry = findViewById(R.id.im_emotion_ll_retry)
    }

    fun showData() {
        mRvList?.visibility = View.VISIBLE
        mllLoading?.visibility = View.GONE
        mllRetry?.visibility = View.GONE
    }

    fun showLoading() {
        mRvList?.visibility = View.GONE
        mllLoading?.visibility = View.VISIBLE
        mllRetry?.visibility = View.GONE
    }

    fun showError() {
        mRvList?.visibility = View.GONE
        mllLoading?.visibility = View.GONE
        mllRetry?.visibility = View.VISIBLE
    }

    fun setRetryOnClickListener(onClickListener: View.OnClickListener) {
        mTvRetry?.setOnClickListener(onClickListener)
    }

    fun getRecyclerView(): RecyclerView? = mRvList
}