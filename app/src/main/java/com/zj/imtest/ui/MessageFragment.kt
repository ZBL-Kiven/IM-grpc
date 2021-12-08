package com.zj.imtest.ui


import com.zj.ccIm.core.bean.ChannelRegisterInfo
import com.zj.imtest.BaseApp
import com.zj.imtest.ui.base.BaseMessageFragment

class MessageFragment : BaseMessageFragment() {

    override fun createData(groupId: Long, ownerId: Int, targetUserId: Int): ChannelRegisterInfo {

        //                return if (ownerId == BaseApp.config.getUserId()) {
        //                    ChannelRegisterInfo.buildWithOwnerMessage(this, groupId)
        //                } else {
        //                    ChannelRegisterInfo.buildWithFansMessage(this, groupId, ownerId)
        //                }

        return if (ownerId == BaseApp.config.getUserId()) {
            ChannelRegisterInfo.buildWithFansPrivateChat(this, groupId, targetUserId)
        } else {
            ChannelRegisterInfo.buildWithOwnerPrivateChat(this, groupId, ownerId)
        }
    }
}