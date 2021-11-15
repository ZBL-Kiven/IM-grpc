package com.zj.imtest.ui

import com.zj.ccIm.core.bean.ChannelRegisterInfo
import com.zj.imtest.ui.base.BaseMessageFragment

class GroupFragment : BaseMessageFragment() {

    private var curSpark = 0
    private var curDiamond = 100

    override fun initMessageObservers(sessionKey: String) {

    }

    override fun createData(groupId: Long, ownerId: Int, targetUserId: Int): ChannelRegisterInfo {
        return ChannelRegisterInfo.buildWithOwnerClapHouse(this, groupId)
    }


}