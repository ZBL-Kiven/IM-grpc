package com.zj.imtest.ui

import com.zj.ccIm.core.bean.ChannelRegisterInfo
import com.zj.database.entity.MessageInfoEntity
import com.zj.imtest.ui.base.BaseMessageFragment

class GroupFragment : BaseMessageFragment() {

    override fun getMessageFilter(data: MessageInfoEntity?, payload: String?): Boolean {
        val isNotPrivateAnswer = data?.replyMsg?.questionContent?.published != false
        return super.getMessageFilter(data, payload) && isNotPrivateAnswer
    }

    override fun createData(groupId: Long, ownerId: Int, targetUserId: Int): ChannelRegisterInfo {
        return ChannelRegisterInfo.buildWithOwnerClapHouse(this, groupId)
    }


}