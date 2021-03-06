package com.zj.emotionbar.widget

import android.content.Context
import android.graphics.PointF
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import android.util.DisplayMetrics

@Suppress("unused")
class SmoothScrollLayoutManager(val context: Context, orientation: Int, reverseLayout: Boolean) : LinearLayoutManager(context, orientation, reverseLayout) {

    private var millisecondsPerInch = 100f
    var isMoveToTop = false

    constructor(context: Context) : this(context, VERTICAL, false)


    override fun smoothScrollToPosition(recyclerView: RecyclerView,
                                        state: RecyclerView.State?, position: Int) {

        val smoothScroller = object : LinearSmoothScroller(recyclerView.context) {

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return millisecondsPerInch / displayMetrics.densityDpi
            }

            override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                return this@SmoothScrollLayoutManager.computeScrollVectorForPosition(targetPosition)
            }

            override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int {
                return if (isMoveToTop) boxStart-viewStart
                else
                    super.calculateDtToFit(viewStart, viewEnd, boxStart, boxEnd, snapPreference)
            }
        }

        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }

    fun setScrollSpeed(millisecondsPerInch: Float) {
        this.millisecondsPerInch = millisecondsPerInch
    }
}
