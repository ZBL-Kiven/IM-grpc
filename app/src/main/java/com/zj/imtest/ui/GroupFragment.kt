package com.zj.imtest.ui

import android.view.View
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.MsgType
import com.zj.ccIm.core.bean.ChannelRegisterInfo
import com.zj.ccIm.core.sender.GiftUnitType
import com.zj.database.entity.MessageInfoEntity
import com.zj.database.entity.MultiLanguage
import com.zj.im.chat.enums.SendMsgState
import com.zj.imtest.R
import com.zj.imtest.ui.base.BaseMessageFragment
import com.zj.imtest.ui.gift.GiftManager
import com.zj.imtest.ui.gift.data.GiftInfo
import com.zj.imtest.ui.gift.v.GiftMarqueeView
import com.zj.imtest.ui.gift.v.GiftShaderView

class GroupFragment : BaseMessageFragment() {

    private var marqueeView: GiftMarqueeView<GiftInfo>? = null
    private var shaderView: GiftShaderView<GiftInfo>? = null
    private var giftManager: GiftManager? = null
    private var sendGift: View? = null

    override fun onCreate() {
        super.onCreate()
        marqueeView = find(R.id.im_frag_msg_gift_marquee)
        shaderView = find(R.id.im_frag_msg_gift_shader)
        sendGift = find(R.id.im_frag_msg_gift_send)
        marqueeView?.visibility = View.VISIBLE
        shaderView?.visibility = View.VISIBLE
        giftManager = GiftManager(marqueeView, shaderView)
        sendGift?.setOnClickListener {
            IMHelper.Sender.senGift(getData().groupId, 14, 1, GiftUnitType.DIAMOND, 1)
        }
    }

    override fun onMessage(msg: MessageInfoEntity) {
        if (msg.msgType == MsgType.GIFT.type && (msg.sendingState == SendMsgState.SUCCESS.type || msg.sendingState == SendMsgState.NONE.type)) {
            val giftInfo = msg.giftMessage
            val gi = GiftInfo().apply {
                this.avatar = msg.sender?.senderAvatar
                this.senderId = msg.sender?.senderId
                this.image = giftInfo?.giftImage
                this.bundle = giftInfo?.bundle
                this.id = giftInfo?.giftId
                this.multiLanguage = giftInfo?.multiLanguage
                this.num = giftInfo?.amount ?: 1
            }
            giftManager?.post(gi, false)
        }
    }

    override fun getMessageFilter(data: MessageInfoEntity?, payload: String?): Boolean {
        val isNotPrivateAnswer = data?.replyMsg?.questionContent?.published != false
        return super.getMessageFilter(data, payload) && isNotPrivateAnswer
    }

    override fun createData(groupId: Long, ownerId: Int, targetUserId: Int): ChannelRegisterInfo {
        return ChannelRegisterInfo.buildWithOwnerClapHouse(this, groupId, ownerId)
    }
}