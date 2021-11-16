package com.zj.ccIm.core


import com.zj.ccIm.CcIM
import com.zj.ccIm.core.bean.ChannelRegisterInfo
import com.zj.ccIm.logger.ImLogs
import com.zj.database.entity.SendMessageReqEn
import com.zj.im.sender.OnSendBefore
import java.util.concurrent.LinkedBlockingDeque

internal object IMChannelManager {

    private var lastMsgRegister = LinkedBlockingDeque<ChannelRegisterInfo>()

    fun offerLast(req: ChannelRegisterInfo) {
        if (lastMsgRegister.isNullOrEmpty()) {
            lastMsgRegister.offerLast(req)
        } else {
            val last = lastMsgRegister.firstOrNull { it.key == req.key }
            if (last != null) {
                last.hasPendingCount++
            } else {
                lastMsgRegister.offerLast(req)
            }
        }
    }

    fun destroy(key: String): ChannelRegisterInfo? {
        val info = lastMsgRegister.firstOrNull { it.key == key } ?: return null
        return if (info.hasPendingCount > 0) {
            info.hasPendingCount--;null
        } else {
            lastMsgRegister.remove(info); info
        }
    }

    fun resumeRegisterInfo(req: ChannelRegisterInfo) {
        if (lastMsgRegister.size > 1) {
            lastMsgRegister.remove(req)
            offerLast(req)
        }
    }

    fun tryToRegisterAfterConnected(): Boolean {
        lastMsgRegister.forEach { b ->
            IMHelper.resumedChatRoomIfConnection(b)
            return true
        }
        return false
    }

    fun sendMsgWithChannel(sen: SendMessageReqEn, clientMsgId: String, sendMsgDefaultTimeout: Long, isSpecialData: Boolean, ignoreConnecting: Boolean, isRecent: Boolean, sendBefore: OnSendBefore<Any?>?) {
        if (sen.key.isEmpty()) {
            sen.key = lastMsgRegister.peekLast()?.key ?: ""
        }
        if (sen.key.isEmpty()) ImLogs.recordErrorInFile("sendMsgWithChannel", "you are sending a message without sending key ,this message may couldn't retry if failed!")
        ImLogs.recordLogsInFile("sendMsgWithChannel", "send new Msg by sending key:${sen.key}")
        if (isRecent) {
            CcIM.resend(sen, clientMsgId, sendMsgDefaultTimeout, isSpecialData, ignoreConnecting, sendBefore)
        } else {
            CcIM.send(sen, clientMsgId, sendMsgDefaultTimeout, isSpecialData, ignoreConnecting, sendBefore)
        }
    }

    fun clear() {
        lastMsgRegister.clear()
    }
}