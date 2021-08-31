package com.zj.imUi.list


import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zj.imUi.R
import com.zj.imUi.utils.ImMessageDecoration
import com.zj.imUi.utils.TimeLineInflateModel
import com.zj.views.list.adapters.BaseAdapter
import com.zj.views.list.listeners.ItemClickListener
import com.zj.views.ut.DPUtils


@Suppress("unused")
abstract class BaseImMsgAdapter<T>(private val recyclerView: RecyclerView, builder: ViewBuilder) : BaseAdapter<T>(builder), ImMessageDecoration.TimeLineViewBuilder<T> {

    abstract fun exchangeWhenUpdate(new: T, old: T)
    abstract fun getSendTime(d: T): Long
    abstract fun setTimeLine(ts: String?, d: T)
    abstract fun equalsOf(f: T, s: T): Boolean
    open fun onItemClick(position: Int, v: View?, m: T?) {}
    open fun onItemLongClick(position: Int, v: View?, m: T?): Boolean {
        return false
    }

    init {
        initRecycler()
    }

    private fun initRecycler() {
        setOnItemClickListener(object : ItemClickListener<T>() {
            override fun onItemClick(position: Int, v: View?, m: T?) {
                this@BaseImMsgAdapter.onItemClick(position, v, m)
            }

            override fun onItemLongClick(position: Int, v: View?, m: T?): Boolean {
                return this@BaseImMsgAdapter.onItemLongClick(position, v, m)
            }
        })
        val imDecoration = object : ImMessageDecoration<T, BaseImMsgAdapter<T>>(this) {
            override fun getItem(p: Int): T? {
                return this@BaseImMsgAdapter.getItem(p)
            }
        }
        recyclerView.itemAnimator = null
        recyclerView.addItemDecoration(imDecoration)
    }

    override fun add(info: T) {
        onChangeTimeline(info)
        super.add(info)
        recyclerView.post { recyclerView.scrollToPosition(maxPosition) }
    }

    override fun add(data: MutableList<T>?) {
        data?.forEach { onChangeTimeline(it) }
        super.add(data)
        recyclerView.post { recyclerView.scrollToPosition(maxPosition) }
    }

    override fun add(info: List<T>, position: Int) {
        data?.forEach { onChangeTimeline(it) }
        super.add(info, position)
        val pos = position + info.size
        if (pos in 0..maxPosition) {
            recyclerView.post { recyclerView.scrollToPosition(pos) }
        }
    }

    fun update(infoEntity: T) {
        val index = data.indexOfLast { equalsOf(it, infoEntity) }
        if (index in 0..maxPosition) {
            val local = data[index]
            exchangeWhenUpdate(infoEntity, local)
            data[index] = infoEntity
            notifyItemChanged(index)
        } else {
            add(infoEntity)
        }
    }

    override fun change(d: List<T>?) {
        d?.forEach { onChangeTimeline(it) }
        super.change(d)
        recyclerView.post { recyclerView.scrollToPosition(maxPosition) }
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

    private fun onChangeTimeline(d: T) {
        val curTime = getSendTime(d)
        val res = context?.let { ctx ->
            TimeLineInflateModel.inflateTimeLine(ctx, curTime, lastTime)
        }
        lastTime = curTime
        setTimeLine(res, d)
    }

    override fun getTopMargin(): Int {
        return DPUtils.dp2px(40f)
    }

    override fun getBottomMargin(): Int {
        return DPUtils.dp2px(40f)
    }

    override fun getTextSize(): Int {
        return DPUtils.sp2px(12f)
    }

    override fun getTextColor(): Int {
        return ContextCompat.getColor(context ?: return 0, R.color.text_color_gray)
    }

}