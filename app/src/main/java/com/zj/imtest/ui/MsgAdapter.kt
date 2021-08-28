package com.zj.imtest.ui

import android.content.Context
import com.zj.database.entity.MessageInfoEntity
import com.zj.views.list.adapters.BaseAdapter
import com.zj.views.list.holders.BaseViewHolder

class MsgAdapter(context: Context) : BaseAdapter<MessageInfoEntity>(ViewBuilder { _, _, _ ->
    ImMsgView(context)
}) {

    override fun initData(holder: BaseViewHolder<MessageInfoEntity>?, position: Int, module: MessageInfoEntity?, payloads: MutableList<Any>?) {
        (holder?.itemView as? ImMsgView)?.setData(ImEntityConverter(module))
    }

}