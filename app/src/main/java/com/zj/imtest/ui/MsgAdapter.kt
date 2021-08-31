package com.zj.imtest.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zj.ccIm.core.sender.Sender
import com.zj.database.entity.MessageInfoEntity
import com.zj.imUi.ui.ImMsgView
import com.zj.imUi.utils.ImMessageDecoration
import com.zj.imUi.utils.TimeLineInflateModel
import com.zj.imtest.R
import com.zj.rebuild.im.uiConfig.ImEntityConverter
import com.zj.views.list.adapters.BaseAdapter
import com.zj.views.list.holders.BaseViewHolder
import com.zj.views.list.listeners.ItemClickListener
import com.zj.views.ut.DPUtils


class MsgAdapter(private val recyclerView: RecyclerView?, context: Context) : BaseAdapter<MessageInfoEntity>(ViewBuilder { _, _, _ ->
    ImMsgView(context).apply {
        ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}), ImMessageDecoration.TimeLineViewBuilder<MessageInfoEntity> {

    private val imDecoration = object : ImMessageDecoration<MessageInfoEntity, MsgAdapter>(this) {
        override fun getItem(p: Int): MessageInfoEntity? {
            return this@MsgAdapter.getItem(p)
        }
    }

    init {
        recyclerView?.itemAnimator = null
        recyclerView?.addItemDecoration(imDecoration)
        setOnItemClickListener(object : ItemClickListener<MessageInfoEntity?>() {
            override fun onItemClick(position: Int, v: View?, m: MessageInfoEntity?) {
                if (m != null && m.sendingState < 0) {
                    Sender.resendMessage(m.clientMsgId)
                }
            }
        })
    }

    override fun add(info: MessageInfoEntity) {
        onChangeTimeline(info)
        super.add(info)
        recyclerView?.post { recyclerView.scrollToPosition(maxPosition) }
    }

    override fun add(data: MutableList<MessageInfoEntity>?) {
        data?.forEach { onChangeTimeline(it) }
        super.add(data)
        recyclerView?.post { recyclerView.scrollToPosition(maxPosition) }
    }

    fun update(infoEntity: MessageInfoEntity) {
        val index = data.indexOfLast { equalsOf(it, infoEntity) }
        if (index in 0..maxPosition) {
            val local = data[index]
            infoEntity.diffInCreateTime = local.diffInCreateTime
            data[index] = infoEntity
            notifyItemChanged(index)
        } else {
            add(infoEntity)
        }
    }

    override fun change(d: List<MessageInfoEntity>?) {
        d?.forEach { onChangeTimeline(it) }
        super.change(d)
        recyclerView?.post { recyclerView.scrollToPosition(maxPosition) }
    }

    fun removeAll(infoEntity: MessageInfoEntity) {
        val lstIds = arrayListOf<Int>()
        data.forEachIndexed { i, v ->
            if (equalsOf(v, infoEntity)) lstIds.add(i)
        }
        lstIds.forEach { data.removeAt(it);notifyItemRemoved(it) }
        if (lstIds.isNotEmpty()) notifyItemChanged(0, itemCount)
    }

    override fun initData(holder: BaseViewHolder<MessageInfoEntity>?, position: Int, module: MessageInfoEntity?, payloads: MutableList<Any>?) {
        (holder?.itemView as? ImMsgView)?.setData(ImEntityConverter(module))
    }

    private var lastTime: Long = 0

    private fun onChangeTimeline(d: MessageInfoEntity) {
        val curTime = d.sendTime
        val res = context?.let { ctx ->
            TimeLineInflateModel.inflateTimeLine(ctx, curTime, lastTime)
        }
        lastTime = curTime
        d.diffInCreateTime = res
    }

    private fun equalsOf(f: MessageInfoEntity, s: MessageInfoEntity): Boolean {
        return (f.clientMsgId.isNotEmpty() && f.clientMsgId == s.clientMsgId) || (f.serverMsgId.isNotEmpty() && f.serverMsgId == s.serverMsgId)
    }

    override fun getTopMargin(): Int {
        return DPUtils.dp2px(40f)
    }

    override fun getBottomMargin(): Int {
        return DPUtils.dp2px(40f)
    }

    override fun getTimeLine(d: MessageInfoEntity?): String? {
        return d?.diffInCreateTime
    }

    override fun getTextSize(): Int {
        return DPUtils.sp2px(14f)
    }

    override fun getTextColor(): Int {
        return ContextCompat.getColor(context ?: return 0, R.color.bg_purple)
    }

}