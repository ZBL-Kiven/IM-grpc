package com.zj.imUi.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class VoicePlayView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : View(context, attributeSet, defStyle) {


    private val itemWidth = 4.px(context)

    private val paint = Paint().apply {
        color = Color.WHITE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = itemWidth
    }

    var isAnim = false
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    private lateinit var length: List<Float>
    private var direction = mutableListOf(true, true, true, true)

    private val maxHeight: Float get() = (0.7 * height / 2).toFloat()

    init {
        setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (!::length.isInitialized) {
            length = generate()
        }
        val internal = (width - itemWidth * 4) / 5
        var offset = internal + itemWidth / 2
        for (element in length) {
            val startY = (height / 2) - element
            val endY = height - startY
            canvas?.drawLine(offset, startY, offset, endY, paint)
            offset += (itemWidth + internal)
        }
        if (isAnim) {
            length = length.mapIndexed { index, item ->
                var value = item + if (direction[index]) 1.px(context) else (-1).px(context)
                if (value < 0) {
                    direction[index] = true
                    value = -value
                } else if (value >= maxHeight) {
                    direction[index] = false
                    value -= (value - maxHeight)
                }
                value
            }
            postInvalidateDelayed(24)
        }
    }

    private fun generate(): List<Float> {
        return listOf(
            maxHeight / 4,
            maxHeight,
            maxHeight / 2,
            maxHeight / 4,
        )
    }

    fun play() {

        //        VoicePlayer.play(file) {
        //            isAnim = false
        //            completion?.invoke()
        //        }

        isAnim = true
    }

    fun pause() {

        //        VoicePlayer.pause()
        isAnim = false
    }

    fun stop() {

        //        VoicePlayer.stop()
        isAnim = false
    }

}

private fun Int.px(context: Context): Float {
    return context.resources.displayMetrics.density * this
}