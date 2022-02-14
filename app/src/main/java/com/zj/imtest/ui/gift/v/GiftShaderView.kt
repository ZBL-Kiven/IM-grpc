package com.zj.imtest.ui.gift.v

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieComposition
import com.zj.imtest.ui.gift.i.GiftShaderIn
import java.lang.IllegalArgumentException
import java.util.concurrent.LinkedBlockingQueue

class GiftShaderView<T : GiftShaderIn> @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) : LottieAnimationView(context, attributeSet, def), Animator.AnimatorListener {

    private val giftQueue = LinkedBlockingQueue<T>()
    private var curHasRunningAnim = false
    private var curData: LottieComposition? = null
    private var onPrepared = false

    init {
        addAnimatorListener(this)
        scaleType = ScaleType.CENTER_CROP
        addLottieOnCompositionLoadedListener {
            curData = it
            onPrepared = true
            playAnimation()
        }
        setFailureListener {
            it.printStackTrace()
            curHasRunningAnim = false
            onPrepared = false
        }
    }

    fun post(curData: T) {
        curData.getSource()?.let {
            if (!it.endsWith(".json", true) && !it.endsWith(".lottie", true)) {
                throw IllegalArgumentException("the data sources only support '.json' or '.lottie' extension")
            }
        }
        giftQueue.add(curData)
        loadNew()
    }

    private fun loadNew() {
        if (!curHasRunningAnim) {
            val d = giftQueue.poll()
            if (d == null) {
                cancelAnimation()
                return
            }
            curHasRunningAnim = true
            if (d.getSource().isNullOrEmpty()) return
            setAnimationFromUrl(d.getSource(), "${d.getUniqueId()}")
        }
    }

    fun clear() {
        curHasRunningAnim = false
        onPrepared = false
        giftQueue.clear()
        cancelAnimation()
        curData = null
    }

    override fun onAnimationStart(animation: Animator?) {
        curHasRunningAnim = true
    }

    override fun onAnimationEnd(animation: Animator?) {
        curHasRunningAnim = false
        onPrepared = false
        loadNew()
    }

    override fun onAnimationCancel(animation: Animator?) {}

    override fun onAnimationRepeat(animation: Animator?) {}
}