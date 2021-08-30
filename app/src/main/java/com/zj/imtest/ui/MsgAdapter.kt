package com.zj.imtest.ui

import android.content.Context
import com.zj.database.entity.MessageInfoEntity
import com.zj.views.list.adapters.BaseAdapter
import com.zj.views.list.holders.BaseViewHolder

class MsgAdapter(context: Context) : BaseAdapter<MessageInfoEntity>(ViewBuilder { _, _, _ ->
    ImMsgView(context)
}) {

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
        (holder?.itemView as? ImMsgView)?.setData(ImEntityConverter(module))
    }

    private fun equalsOf(f: MessageInfoEntity, s: MessageInfoEntity): Boolean {
        return (f.clientMsgId.isNotEmpty() && f.clientMsgId == s.clientMsgId) || (f.serverMsgId.isNotEmpty() && f.serverMsgId == s.serverMsgId)
    }

}