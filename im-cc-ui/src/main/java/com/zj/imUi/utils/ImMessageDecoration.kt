package com.zj.imUi.utils

import android.content.res.Resources
import android.graphics.*
import android.view.View
import androidx.recyclerview.widget.RecyclerView


abstract class ImMessageDecoration<R, T : ImMessageDecoration.TimeLineViewBuilder<R>>(private val tli: T) : RecyclerView.ItemDecoration() {

    private val leftDividerWidth: Int = dp2px(10)
    private val rightDividerWidth: Int = dp2px(10)
    private val topDividerWidth: Int = dp2px(16)
    private val bottomDividerWidth: Int = dp2px(16)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    abstract fun getItem(p: Int): R?

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val pos = parent.getChildAdapterPosition(view)
        val data = getItem(pos)
        var top = topDividerWidth
        if (!tli.getTimeLine(data).isNullOrEmpty()) {
            top += (tli.getTopMargin() + tli.getTextSize())
        }
        outRect.set(leftDividerWidth, top, rightDividerWidth, bottomDividerWidth)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val left = parent.width / 2f + parent.paddingLeft
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val view = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(view)
            val tlTxt = tli.getTimeLine(getItem(position))
            if (position != 0 && !tlTxt.isNullOrEmpty()) {
                paint.textSize = tli.getTextSize().toFloat()
                paint.color = tli.getTextColor()
                paint.textAlign = Paint.Align.CENTER
                val top = view.top - tli.getBottomMargin() * 1.0f
                c.drawText(tlTxt, left, top, paint)
            }
        }
    }

    private fun dp2px(i: Int): Int {
        return (Resources.getSystem().displayMetrics.density * i + 0.5f).toInt()
    }

    interface TimeLineViewBuilder<R> {

        fun getTopMargin(): Int

        fun getBottomMargin(): Int

        fun getTimeLine(d: R?): String?

        fun getTextSize(): Int

        fun getTextColor(): Int
    }
}