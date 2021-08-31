package com.zj.imUi.widget.loading

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import java.util.*

/**
 * author: 李 祥
 * date:   2021/8/24 2:01 下午
 * description:
 */
class BallSpinFadeLoaderIndicator : Indicator() {
    var scaleFloats = floatArrayOf(
        SCALE,
        SCALE,
        SCALE,
        SCALE,
        SCALE,
        SCALE,
        SCALE,
        SCALE
    )
    var alphas = intArrayOf(
        ALPHA,
        ALPHA,
        ALPHA,
        ALPHA,
        ALPHA,
        ALPHA,
        ALPHA,
        ALPHA
    )


    override fun draw(canvas: Canvas?, paint: Paint?) {
        val radius: Int = width/ 10
        for (i in 0..7) {
            canvas?.save()
            //绘制点
            val point =
                circleAt(width, height, width/ 2.5f - radius, i * (Math.PI / 4))
            //平移
            canvas?.translate(point.x, point.y)
            //缩放
            canvas?.scale(scaleFloats[i], scaleFloats[i])
            //旋转
            canvas?.rotate((i * 45).toFloat())
            //透明度
            paint?.alpha = alphas[i]
            //方型线
            val rectF = RectF((-radius).toFloat(), -radius / 1.5f, 1.5f * radius, radius / 1.5f)
            if (paint != null) {
                canvas?.drawRoundRect(rectF, 5f, 5f, paint)
            }
            canvas?.restore()
        }
    }

    override fun onCreateAnimators(): ArrayList<ValueAnimator> {
        val animators = ArrayList<ValueAnimator>()
        val delays = intArrayOf(0, 120, 240, 360, 480, 600, 720, 780, 840)
        for (i in 0..7) {
            //缩放
            val scaleAnim = ValueAnimator.ofFloat(1f, 0.4f, 1f)
            scaleAnim.duration = 1000
            scaleAnim.repeatCount = -1
            scaleAnim.startDelay = delays[i].toLong()
            addUpdateListener(scaleAnim,
                ValueAnimator.AnimatorUpdateListener { animation ->
                    scaleFloats[i] = animation.animatedValue as Float
                    postInvalidate()
                })
            //透明
            val alphaAnim = ValueAnimator.ofInt(255, 77, 255)
            alphaAnim.duration = 1000
            alphaAnim.repeatCount = -1
            alphaAnim.startDelay = delays[i].toLong()
            addUpdateListener(alphaAnim,
                ValueAnimator.AnimatorUpdateListener { animation ->
                    alphas[i] = animation.animatedValue as Int
                    postInvalidate()
                })
            animators.add(scaleAnim)
            animators.add(alphaAnim)
        }
        return animators
    }

    /**
     * 圆O的圆心为(a,b),半径为R,点A与到X轴的为角α.
     * 则点A的坐标为(a+R*cosα,b+R*sinα)
     * @param width
     * @param height
     * @param radius
     * @param angle
     * @return
     */
    fun circleAt(
        width: Int,
        height: Int,
        radius: Float,
        angle: Double
    ): Point {
        val x = (width / 2 + radius * Math.cos(angle)).toFloat()
        val y = (height / 2 + radius * Math.sin(angle)).toFloat()
        return Point(x, y)
    }

    inner class Point(var x: Float, var y: Float)
    companion object {
        const val SCALE = 1.0f
        const val ALPHA = 255
    }
}