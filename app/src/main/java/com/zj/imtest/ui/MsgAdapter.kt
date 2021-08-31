package com.zj.imtest.ui


import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zj.database.entity.MessageInfoEntity
import com.zj.imUi.list.BaseImMsgAdapter
import com.zj.imUi.ui.ImMsgView

class MsgAdapter(recyclerView: RecyclerView) : BaseImMsgAdapter<MessageInfoEntity>(recyclerView, ViewBuilder { _, _, _ ->
    ImMsgView(recyclerView.context).apply {
        ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}) {

    override fun initData(holder: com.zj.views.list.holders.BaseViewHolder<MessageInfoEntity>?, position: Int, module: MessageInfoEntity?, payloads: MutableList<Any>?) {
        (holder?.itemView as? ImMsgView)?.let {
            if (payloads.isNullOrEmpty()) it.setData(ImEntityConverter(module))
            else it.notifyChange(payloads.firstOrNull())
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
        return (f.clientMsgId.isNotEmpty() && f.clientMsgId == s.clientMsgId) || (f.serverMsgId.isNotEmpty() && f.serverMsgId == s.serverMsgId)
    }

    override fun getTimeLine(d: MessageInfoEntity?): String? {
        return d?.diffInCreateTime
    }
}