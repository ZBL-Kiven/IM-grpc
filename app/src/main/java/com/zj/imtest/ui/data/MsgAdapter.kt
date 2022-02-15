package com.zj.imtest.ui.data

import androidx.recyclerview.widget.RecyclerView
import com.zj.database.entity.MessageInfoEntity
import com.zj.imUi.UiMsgType
import com.zj.imUi.base.BaseImItem
import com.zj.imUi.list.BaseImMsgAdapter
import com.zj.imUi.ui.ImMsgView
import com.zj.views.list.adapters.BaseAdapter.ViewBuilder


class MsgAdapter(private val recyclerView: RecyclerView) : BaseImMsgAdapter<MessageInfoEntity>(recyclerView, ViewBuilder { _, _, _ -> ImMsgView(recyclerView.context) }) {

    override fun add(data: MutableList<MessageInfoEntity>?) {
        super.add(data)
        recyclerView.post { recyclerView.scrollToPosition(maxPosition) }
    }

    override fun add(info: MessageInfoEntity) {
        super.add(info)
        recyclerView.post { recyclerView.scrollToPosition(maxPosition) }
    }

    override fun add(info: List<MessageInfoEntity>, position: Int) {
        super.add(info, position)
        if (position > 0) {
            recyclerView.post { recyclerView.scrollToPosition(position) }
        }
    }

    override fun change(d: List<MessageInfoEntity>?) {
        super.change(d)
        recyclerView.post { recyclerView.scrollToPosition(maxPosition) }
    }


    override fun initData(holder: com.zj.views.list.holders.BaseViewHolder<MessageInfoEntity>?, position: Int, module: MessageInfoEntity?, payloads: MutableList<Any>?) {
        val m = ImEntityConverter(module)
        (holder?.itemView as? ImMsgView)?.let {
            if (payloads.isNullOrEmpty()) {
                it.setData(m, UiMsgType.GROUP_CHAT)
                if (module != null) {
                    onChangeTimeline(module)
                }
            } else it.notifyChange(m, payloads.firstOrNull())
        }
    }

    override fun exchangeWhenUpdate(new: MessageInfoEntity, old: MessageInfoEntity) {
        new.diffInCreateTime = old.diffInCreateTime
    }

    override fun getSendTime(d: MessageInfoEntity): Long {
        return d.sendTime
    }

    override fun setTimeLine(ts: String?, d: MessageInfoEntity) {
        d.diffInCreateTime = ts
    }

    override fun equalsOf(f: MessageInfoEntity, s: MessageInfoEntity): Boolean {
        return f.clientMsgId.isNotEmpty() && f.clientMsgId == s.clientMsgId
    }

    override fun getTimeLine(d: MessageInfoEntity?): String? {
        return d?.diffInCreateTime
    }

    fun setRewardViewState(f: MessageInfoEntity, inReplying: Boolean) {
        if (inReplying) {
            replyingMsgInfo.add(f.clientMsgId)
        } else {
            replyingMsgInfo.remove(f.clientMsgId)
        }
        notifyItemRangeChanged(0, itemCount, BaseImItem.NOTIFY_CHANGE_BTN_ENABLED)
    }

    fun isMsgReplying(f: MessageInfoEntity): Boolean {
        return replyingMsgInfo.contains(f.clientMsgId)
    }

    companion object {
        val replyingMsgInfo = mutableSetOf<String>()
    }
}