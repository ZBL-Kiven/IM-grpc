package com.zj.imtest.ui


import com.zj.ccIm.core.bean.ChannelRegisterInfo
import com.zj.database.entity.MessageInfoEntity
import com.zj.imtest.BaseApp
import com.zj.imtest.ui.base.BaseMessageFragment

class MessageFragment : BaseMessageFragment() {

    override fun getMessageFilter(data: MessageInfoEntity?, payload: String?): Boolean {
        val isSelfOrOwner = (data?.sender?.senderId == BaseApp.config.getUserId()) || (data?.sender?.senderId == getData().ownerId)
        return super.getMessageFilter(data, payload) && (data?.questionContent == null) && isSelfOrOwner
    }

    override fun createData(groupId: Long, ownerId: Int, targetUserId: Int): ChannelRegisterInfo {
        return if (ownerId == BaseApp.config.getUserId()) {
            ChannelRegisterInfo.buildWithOwnerMessage(this, groupId)
        } else {
            ChannelRegisterInfo.buildWithFansMessage(this, groupId, ownerId)
        }
    }
}