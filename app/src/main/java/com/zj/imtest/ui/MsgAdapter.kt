package com.zj.imtest.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.zj.ccIm.core.sender.Sender
import com.zj.database.entity.MessageInfoEntity
import com.zj.imUi.ui.ImMsgView
import com.zj.views.list.adapters.BaseAdapter
import com.zj.views.list.holders.BaseViewHolder
import com.zj.views.list.listeners.ItemClickListener

class MsgAdapter(context: Context) : BaseAdapter<MessageInfoEntity>(ViewBuilder { _, _, _ ->
    ImMsgView(context).apply {
        ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}) {

    init {
        setOnItemClickListener(object : ItemClickListener<MessageInfoEntity?>() {
            override fun onItemClick(position: Int, v: View?, m: MessageInfoEntity?) {
                if (m != null && m.sendingState < 0) {
                    Sender.resendMessage(m.clientMsgId)
                }
            }
        })
    }

    fun update(infoEntity: MessageInfoEntity) {
        val index = data.indexOfLast { equalsOf(it, infoEntity) }
        if (index in 0..maxPosition) {
            data[index] = infoEntity
            notifyItemChanged(index)
        } else {
            add(infoEntity)
        }
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
        (holder?.itemView as? ImMsgView)?.setData(ImEntityConverter(module, null))
    }

    private fun equalsOf(f: MessageInfoEntity, s: MessageInfoEntity): Boolean {
        return (f.clientMsgId.isNotEmpty() && f.clientMsgId == s.clientMsgId) || (f.serverMsgId.isNotEmpty() && f.serverMsgId == s.serverMsgId)
    }

}