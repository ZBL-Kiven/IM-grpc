package com.zj.imUi.utils

import android.content.res.Resources
import android.graphics.*
import android.view.View
import androidx.recyclerview.widget.RecyclerView


abstract class ImMessageDecoration<R, T : ImMessageDecoration.TimeLineViewBuilder<R>>(private val tli: T) :
    RecyclerView.ItemDecoration() {

    private val leftDividerWidth: Int = dp2px(10)
    private val rightDividerWidth: Int = dp2px(10)
    private val topDividerWidth: Int = dp2px(16)
    private val bottomDividerWidth: Int = dp2px(16)
    private val textBackgroundCorners = dp2px(4) * 1.0f
    private val textBackgroundHorPadding: Int = dp2px(12)
    private val textBackgroundVerPadding: Int = dp2px(8)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    abstract fun getItem(p: Int): R?

    override fun getItemOffsets(outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State) {
        val pos = parent.getChildAdapterPosition(view)
        val data = getItem(pos)
        var top = topDividerWidth

        val position = parent.getChildAdapterPosition(view)
        if (!tli.getTimeLine(data).isNullOrEmpty() && position != 0){
            top += tli.getTopMargin() + tli.getTextSize()
        }
//        if (!tli.getTimeLine(data).isNullOrEmpty()) {
//            top += tli.getTopMargin() + tli.getTextSize()
//        }
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
                paint.textAlign = Paint.Align.CENTER
                val textWidth = paint.measureText(tlTxt)
                val top = view.top - (tli.getTopMargin() + topDividerWidth + tli.getTextSize()) / 2f
                val bl = left - textWidth / 2f - textBackgroundHorPadding
                val bt = top - paint.textSize / 2f - textBackgroundVerPadding - paint.descent()
                val br = left + textWidth / 2f + textBackgroundHorPadding
                val bb = bt + paint.textSize + textBackgroundVerPadding * 2
                val r = RectF(bl, bt, br, bb)
                paint.color = tli.getTextBackgroundColor()
                c.drawRoundRect(r, textBackgroundCorners, textBackgroundCorners, paint)
                paint.color = tli.getTextColor()
                c.drawText(tlTxt, left, top, paint)
            }
        }
    }

    private fun dp2px(i: Int): Int {
        return (Resources.getSystem().displayMetrics.density * i + 0.5f).toInt()
    }

    interface TimeLineViewBuilder<R> {

        fun getTopMargin(): Int

        fun getTimeLine(d: R?): String?

        fun getTextSize(): Int

        fun getTextColor(): Int

        fun getTextBackgroundColor(): Int
    }
}