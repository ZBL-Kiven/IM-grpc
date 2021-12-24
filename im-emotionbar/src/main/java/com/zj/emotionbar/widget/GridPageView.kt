package com.zj.emotionbar.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zj.emotionbar.R
import com.zj.emotionbar.data.EmoticonPack

/**
 * @author: JayQiu
 * @date: 2021/12/9
 * @description:
 */
class GridPageView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null, def: Int = 0) : FrameLayout(context, attr, def) {
    private var mRvList: RecyclerView? = null
    private var mllLoading: LinearLayout? = null

    init {
        View.inflate(context, R.layout.view_grid_page, this)
        initView()
    }

    private fun initView() {
        mllLoading = findViewById(R.id.im_emotion_ll_loading)
        mRvList = findViewById(R.id.im_emotion_ll_grid)
    }

    fun showData() {
        mRvList?.visibility = View.VISIBLE
        mllLoading?.visibility = View.GONE
    }

    fun showLoading() {
        mRvList?.visibility = View.GONE
        mllLoading?.visibility = View.VISIBLE
    }

    fun getRecyclerView(): RecyclerView? = mRvList
}