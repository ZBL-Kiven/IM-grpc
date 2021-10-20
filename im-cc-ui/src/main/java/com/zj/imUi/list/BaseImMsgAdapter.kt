package com.zj.imUi.list


import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.zj.imUi.ui.ImMsgView
import com.zj.imUi.utils.ImMessageDecoration
import com.zj.imUi.utils.TimeLineInflateModel
import com.zj.views.list.adapters.BaseAdapter
import com.zj.views.list.holders.BaseViewHolder
import com.zj.views.ut.DPUtils


@Suppress("unused")
abstract class BaseImMsgAdapter<T>(private val recyclerView: RecyclerView, builder: ViewBuilder) :
    BaseAdapter<T>(builder), ImMessageDecoration.TimeLineViewBuilder<T> {

    abstract fun exchangeWhenUpdate(new: T, old: T)
    abstract fun getSendTime(d: T): Long
    abstract fun setTimeLine(ts: String?, d: T)
    abstract fun equalsOf(f: T, s: T): Boolean

    init {
        initRecycler()
    }

    private fun initRecycler() {
        val imDecoration = object : ImMessageDecoration<T, BaseImMsgAdapter<T>>(this) {
            override fun getItem(p: Int): T? {
                return this@BaseImMsgAdapter.getItem(p)
            }
        }
        recyclerView.itemAnimator = null
        recyclerView.addItemDecoration(imDecoration)
    }

    override fun onViewRecycled(holder: BaseViewHolder<T>) {
        super.onViewRecycled(holder)
        (holder.itemView as? ImMsgView)?.onDestroyed()
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder<T>) {
        super.onViewAttachedToWindow(holder)
        (holder.itemView as? ImMsgView)?.onResume()
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder<T>) {
        super.onViewDetachedFromWindow(holder)
        (holder.itemView as? ImMsgView)?.onStop()
    }

    override fun add(info: T) {
        onChangeTimeline(info)
        super.add(info)
    }

    override fun add(data: MutableList<T>?) {
        data?.forEach { onChangeTimeline(it) }
        super.add(data)
    }

    override fun add(info: List<T>, position: Int) {
        data?.forEach { onChangeTimeline(it) }
        super.add(info, position)
    }

    fun update(infoEntity: T, pl: Any? = null) {
        val index = data.indexOfLast { equalsOf(it, infoEntity) }
        if (index in 0..maxPosition) {
            val local = data[index]
            exchangeWhenUpdate(infoEntity, local)
            data[index] = infoEntity
            notifyItemChanged(index, pl)
        } else {
            add(infoEntity)
        }
    }

    override fun change(d: List<T>?) {
        d?.forEach { onChangeTimeline(it) }
        super.change(d)
    }

    fun removeIfEquals(infoEntity: T) {
        val lstIds = arrayListOf<Int>()
        data.forEachIndexed { i, v ->
            if (equalsOf(v, infoEntity)) lstIds.add(i)
        }
        lstIds.forEach { data.removeAt(it);notifyItemRemoved(it) }
        if (lstIds.isNotEmpty()) notifyItemChanged(0, itemCount)
    }

    private var lastTime: Long = 0

    open fun onChangeTimeline(d: T) {
        val curTime = getSendTime(d)
        val res = context?.let { ctx ->
            TimeLineInflateModel.inflateTimeLine(ctx, curTime, lastTime)
        }
        if (!res.isNullOrEmpty()) lastTime = curTime
        setTimeLine(res, d)
    }

    override fun getTopMargin(): Int {
        return DPUtils.dp2px(40f)
    }

    override fun getTextSize(): Int {
        return DPUtils.sp2px(12f)
    }

    override fun getTextColor(): Int {
        return Color.parseColor("#FF9395AE")
    }

    override fun getTextBackgroundColor(): Int {
        return Color.WHITE
    }
}