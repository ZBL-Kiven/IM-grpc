package com.zj.ccIm.core.sender

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.zj.ccIm.CcIM
import com.zj.ccIm.core.Constance
import com.zj.ccIm.core.IMChannelManager
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.MsgType
import com.zj.database.entity.MessageInfoEntity
import com.zj.database.entity.SendMessageReqEn
import com.zj.im.sender.OnSendBefore

@Suppress("unused")
open class MsgSender internal constructor(private val config: SendMsgConfig) {

    fun sendRewardTextMsg(content: String, groupId: Long, diamondNum: Int = 0, rewardMsgType: MsgType, isPublic: Boolean): String {
        val sen = SendMessageReqEn()
        sen.content = content
        sen.msgType = MsgType.QUESTION.type
        sen.groupId = groupId
        sen.clientMsgId = config.callId
        sen.answerMsgType = rewardMsgType.type
        sen.diamondNum = diamondNum
        sen.public = isPublic
        setRetryProp(sen)
        send(sen)
        return config.callId
    }

    fun sendText(content: String, groupId: Long, replyMsg: MessageInfoEntity? = null): String {
        val sen = SendMessageReqEn()
        sen.content = content
        sen.msgType = MsgType.TEXT.type
        sen.groupId = groupId
        sen.clientMsgId = config.callId
        sen.replyMsg = replyMsg
        setRetryProp(sen)
        send(sen)
        return config.callId
    }

    fun sendUrlImg(url: String, width: Int, height: Int, groupId: Long, replyMsg: MessageInfoEntity? = null): String {
        val sen = SendMessageReqEn()
        sen.url = url
        sen.clientMsgId = url
        sen.msgType = MsgType.IMG.type
        sen.groupId = groupId
        sen.clientMsgId = config.callId
        sen.width = width
        sen.height = height
        sen.replyMsg = replyMsg
        setRetryProp(sen)
        send(sen)
        return config.callId
    }

    fun sendImg(filePath: String, width: Int, height: Int, groupId: Long, replyMsg: MessageInfoEntity? = null): String {
        val sen = SendMessageReqEn()
        sen.localFilePath = filePath
        sen.msgType = MsgType.IMG.type
        sen.groupId = groupId
        sen.clientMsgId = config.callId
        sen.width = width
        sen.height = height
        sen.replyMsg = replyMsg
        setRetryProp(sen)
        send(sen)
        return config.callId
    }

    fun sendAudio(filePath: String, duration: Long, groupId: Long, replyMsg: MessageInfoEntity? = null): String {
        val sen = SendMessageReqEn()
        sen.localFilePath = filePath
        sen.tempFilePath = filePath
        sen.msgType = MsgType.AUDIO.type
        sen.groupId = groupId
        sen.clientMsgId = config.callId
        sen.duration = duration
        sen.replyMsg = replyMsg
        setRetryProp(sen)
        send(sen)
        return config.callId
    }

    fun sendUrlAudio(url: String, duration: Long, groupId: Long, replyMsg: MessageInfoEntity? = null): String {
        val sen = SendMessageReqEn()
        sen.url = url
        sen.msgType = MsgType.AUDIO.type
        sen.groupId = groupId
        sen.clientMsgId = config.callId
        sen.duration = duration
        sen.replyMsg = replyMsg
        setRetryProp(sen)
        send(sen)
        return config.callId
    }

    fun sendVideo(filePath: String, width: Int, height: Int, duration: Long, groupId: Long, replyMsg: MessageInfoEntity? = null): String {
        val sen = SendMessageReqEn()
        sen.localFilePath = filePath
        sen.msgType = MsgType.VIDEO.type
        sen.groupId = groupId
        sen.clientMsgId = config.callId
        sen.width = width
        sen.height = height
        sen.duration = duration
        sen.replyMsg = replyMsg
        setRetryProp(sen)
        send(sen)
        return config.callId
    }

    fun sendUrlVideo(url: String, width: Int, height: Int, duration: Long, groupId: Long, replyMsg: MessageInfoEntity? = null): String {
        val sen = SendMessageReqEn()
        sen.url = url
        sen.msgType = MsgType.VIDEO.type
        sen.groupId = groupId
        sen.clientMsgId = config.callId
        sen.width = width
        sen.height = height
        sen.duration = duration
        sen.replyMsg = replyMsg
        setRetryProp(sen)
        send(sen)
        return config.callId
    }

    fun resendMessage(clientId: String) {
        IMHelper.withDb {
            it.sendMsgDao().findByCallId(clientId)
        }?.let { resendMessage(it) }
    }

    internal fun resendMessage(info: SendMessageReqEn) {
        val sendBefore = if (MsgType.hasUploadType(info.msgType)) {
            if (!info.url.isNullOrEmpty()) null else FileSender(info)
        } else null
        send(info, true, sendBefore)
    }

    private fun setRetryProp(sen: SendMessageReqEn) {
        sen.ignoreConnectionState = config.connectionStateCheck
        sen.ignoreSendConditionState = config.sendConditionCheck
        sen.autoRetryResend = !config.fromCustom && MsgType.canRetryType(sen.msgType)
        sen.autoResendWhenBootStart = !config.fromCustom && MsgType.canRetryWhenBootStartType(sen.msgType)
    }

    private fun send(sen: SendMessageReqEn, isRecent: Boolean = false, sendBefore: OnSendBefore<Any?>? = FileSender.getIfSupport(sen)) {
        val p = if (sendBefore != null) checkPermission() else null
        if (sendBefore == null || p?.first == true) {
            IMChannelManager.sendMsgWithChannel(sen, sen.clientMsgId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = sen.ignoreSendConditionState, ignoreConnecting = sen.ignoreConnectionState, isRecent, sendBefore = sendBefore, config.customSendListener?.callback)
        } else {
            CcIM.postError(SecurityException("from:MsgSender . PERMISSION_DENIED with permission ${p?.second}"))
        }
    }

    private fun checkPermission(): Pair<Boolean, String>? {
        return CcIM.getAppContext()?.let {
            val i = ContextCompat.checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            val i1 = ContextCompat.checkSelfPermission(it, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            val s = when {
                !i && !i1 -> "${Manifest.permission.READ_EXTERNAL_STORAGE},${Manifest.permission.WRITE_EXTERNAL_STORAGE}"
                !i -> Manifest.permission.READ_EXTERNAL_STORAGE
                !i1 -> Manifest.permission.WRITE_EXTERNAL_STORAGE
                else -> "NONE"
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Pair(i, s) else Pair(i && i1, s)
        }
    }
}