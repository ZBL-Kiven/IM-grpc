package com.zj.imtest.ui.gift.v

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.zj.imtest.R
import com.zj.imtest.ui.gift.i.AnimatorListener
import com.zj.imtest.ui.gift.i.GiftInfoIn
import com.zj.views.list.adapters.BaseAdapter
import com.zj.views.list.holders.BaseViewHolder
import java.lang.IllegalStateException
import java.util.concurrent.LinkedBlockingQueue
import kotlin.math.max


@Suppress("unused")
class GiftMarqueeView<T : GiftInfoIn> @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) : RecyclerView(context, attributeSet, def) {

    val checkStep: Int
    private val child: Int
    private val showCount: Int
    private val minDisappearDuration: Int
    private val adapter: GiftMarqueeAdapter
    private val giftQueue = LinkedBlockingQueue<T>()
    private var giftInflateIn: GiftInflateIn<T>? = null

    init {
        val ta = context.obtainStyledAttributes(attributeSet, R.styleable.GiftMarqueeView)
        try {
            child = ta.getResourceId(R.styleable.GiftMarqueeView_gift_child, -1)
            showCount = ta.getInt(R.styleable.GiftMarqueeView_gift_childCount, 0)
            checkStep = ta.getInt(R.styleable.GiftMarqueeView_gift_disappearStep, 0)
            minDisappearDuration = ta.getInt(R.styleable.GiftMarqueeView_gift_min_disappearDuration, 2000)
        } finally {
            ta.recycle()
        }
        if (child == -1) throw IllegalStateException("child layout must not be null!")
        adapter = GiftMarqueeAdapter()
        adapter.add((0 until showCount).map { HolderInfo() })
        setAdapter(adapter)
    }

    fun setGiftDataIn(giftInflateIn: GiftInflateIn<T>) {
        this.giftInflateIn = giftInflateIn
    }

    fun countDown() {
        adapter.notifyItemRangeChanged(0, adapter.itemCount, "countdownOrSet")
    }

    fun post(info: T) {
        adapter.data.indexOfFirst { it.curData?.typeEquals(info) == true }.let {
            val data = adapter.getItem(it)
            val cur = data?.curData
            val holder = findViewHolderForAdapterPosition(it)
            if (cur != null && giftInflateIn?.whenSameTypeGift(holder, info, cur) == true) {
                data.resetIdleTime()
                return
            }
        }
        giftQueue.offer(info)
    }

    fun clear() {
        adapter.clear()
        giftQueue.clear()
    }

    private fun inflateGiftData(view: View, curData: T?): Boolean {
        return if (curData != null) giftInflateIn?.onDataInflate(view, curData) ?: false else false
    }

    inner class GiftMarqueeAdapter : BaseAdapter<HolderInfo>(child) {

        override fun initData(holder: BaseViewHolder<HolderInfo>?, position: Int, module: HolderInfo?, payloads: MutableList<Any>?) {
            if (!payloads.isNullOrEmpty() && payloads.contains("countdownOrSet")) {
                module?.downTo(holder)
            } else {
                holder?.itemView?.visibility = View.INVISIBLE
            }
        }
    }

    inner class HolderInfo(private var idleTime: Int = 0, private var idleState: Boolean = true) : AnimatorListener() {

        var curData: T? = null
        private var inStarting = false
        private var inEnding = false
        private var inStanding = false

        fun resetIdleTime() {
            idleTime = checkStep + max(curData?.duration ?: 0, minDisappearDuration)
        }

        fun downTo(holder: BaseViewHolder<HolderInfo>?) {
            idleTime = (idleTime - checkStep).coerceAtLeast(0)
            holder?.itemView?.let {
                if (idleState) {
                    giftQueue.poll()?.let { d ->
                        curData = d
                        idleState = false
                        it.alpha = 0.0f
                        idleTime = checkStep + max(curData?.duration ?: 0, minDisappearDuration)
                    }
                } else if (idleTime <= 0) {
                    inStanding = false
                    curData = null
                    idleState = true
                    if (!inEnding) {
                        inEnding = true
                        it.clearAnimation()
                        it.post {
                            it.animate().alpha(0.0f).setDuration(checkStep.toLong()).setListener(this).start()
                        }
                    }
                } else if (!inStarting && !inStanding) {
                    inStarting = true
                    inStanding = true
                    inflateGiftData(it, curData)
                    it.clearAnimation()
                    it.post {
                        val width = it.width.toFloat()
                        it.translationX = -width
                        it.alpha = 1.0f
                        it.animate().translationX(0f).setDuration(checkStep.toLong()).setListener(this).start()
                    }
                }
                if (!inStarting && !inEnding) {
                    val v = if (idleState) INVISIBLE else VISIBLE
                    if (it.visibility != v) it.visibility = v
                }
            }
        }

        override fun onAnimationEnd(animation: Animator?) {
            if (inStarting) inStarting = false
            if (inEnding) inEnding = false
            animation?.removeAllListeners()
        }
    }

    interface GiftInflateIn<T> {

        fun whenSameTypeGift(holder: ViewHolder?, newData: T, curData: T): Boolean

        fun onDataInflate(view: View, curData: T): Boolean
    }
}