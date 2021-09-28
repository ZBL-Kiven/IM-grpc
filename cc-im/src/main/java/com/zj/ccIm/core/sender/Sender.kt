package com.zj.ccIm.core.sender

import android.app.Application
import com.zj.ccIm.core.Constance
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.MsgType
import com.zj.ccIm.logger.ImLogs
import com.zj.compress.CompressUtils
import com.zj.database.entity.MessageInfoEntity
import com.zj.database.entity.SendMessageReqEn
import java.lang.IllegalArgumentException
import java.lang.NullPointerException
import java.util.*

@Suppress("unused")
object Sender {

    fun sendRewardTextMsg(content: String, groupId: Long, diamondNum: Int = 0, rewardMsgType: MsgType, isPublic: Boolean, callId: String = UUID.randomUUID().toString()) {
        val sen = SendMessageReqEn()
        sen.content = content
        sen.msgType = MsgType.QUESTION.type
        sen.groupId = groupId
        sen.clientMsgId = callId
        sen.answerMsgType = rewardMsgType.type
        sen.diamondNum = diamondNum
        sen.public = isPublic
        IMHelper.send(sen, callId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = false, ignoreConnecting = false, sendBefore = null)
    }

    fun sendText(content: String, groupId: Long, replyMsg: MessageInfoEntity? = null, callId: String = UUID.randomUUID().toString()) {
        val sen = SendMessageReqEn()
        sen.content = content
        sen.msgType = MsgType.TEXT.type
        sen.groupId = groupId
        sen.clientMsgId = callId
        sen.replyMsg = replyMsg
        IMHelper.send(sen, callId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = false, ignoreConnecting = false, sendBefore = null)
    }

    fun sendUrlImg(url: String, width: Int, height: Int, groupId: Long, replyMsg: MessageInfoEntity? = null, callId: String = UUID.randomUUID().toString()) {
        val sen = SendMessageReqEn()
        sen.url = url
        sen.clientMsgId = url
        sen.msgType = MsgType.IMG.type
        sen.groupId = groupId
        sen.clientMsgId = callId
        sen.width = width
        sen.height = height
        sen.replyMsg = replyMsg
        IMHelper.send(sen, callId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = false, ignoreConnecting = false, sendBefore = null)
    }

    fun sendImg(filePath: String, width: Int, height: Int, groupId: Long, replyMsg: MessageInfoEntity? = null, callId: String = UUID.randomUUID().toString()) {
        val sen = SendMessageReqEn()
        sen.localFilePath = filePath
        sen.msgType = MsgType.IMG.type
        sen.groupId = groupId
        sen.clientMsgId = callId
        sen.width = width
        sen.height = height
        sen.replyMsg = replyMsg
        IMHelper.send(sen, callId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = false, ignoreConnecting = false, sendBefore = MsgFileUploader(sen))
    }

    fun sendAudio(filePath: String, duration: Long, groupId: Long, replyMsg: MessageInfoEntity? = null, callId: String = UUID.randomUUID().toString()) {
        val sen = SendMessageReqEn()
        sen.localFilePath = filePath
        sen.msgType = MsgType.AUDIO.type
        sen.groupId = groupId
        sen.clientMsgId = callId
        sen.duration = duration
        sen.replyMsg = replyMsg
        IMHelper.send(sen, callId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = false, ignoreConnecting = false, sendBefore = MsgFileUploader(sen))
    }

    fun sendUrlAudio(url: String, duration: Long, groupId: Long, replyMsg: MessageInfoEntity? = null, callId: String = UUID.randomUUID().toString()) {
        val sen = SendMessageReqEn()
        sen.url = url
        sen.msgType = MsgType.AUDIO.type
        sen.groupId = groupId
        sen.clientMsgId = callId
        sen.duration = duration
        sen.replyMsg = replyMsg
        IMHelper.send(sen, callId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = false, ignoreConnecting = false, sendBefore = MsgFileUploader(sen))
    }

    fun sendVideo(filePath: String, width: Int, height: Int, duration: Long, groupId: Long, replyMsg: MessageInfoEntity? = null, callId: String = UUID.randomUUID().toString()) {
        val sen = SendMessageReqEn()
        sen.localFilePath = filePath
        sen.msgType = MsgType.VIDEO.type
        sen.groupId = groupId
        sen.clientMsgId = callId
        sen.width = width
        sen.height = height
        sen.duration = duration
        sen.replyMsg = replyMsg
        IMHelper.send(sen, callId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = false, ignoreConnecting = false, sendBefore = null)
    }

    fun sendUrlVideo(url: String, width: Int, height: Int, duration: Long, groupId: Long, replyMsg: MessageInfoEntity? = null, callId: String = UUID.randomUUID().toString()) {
        val sen = SendMessageReqEn()
        sen.url = url
        sen.msgType = MsgType.VIDEO.type
        sen.groupId = groupId
        sen.clientMsgId = callId
        sen.width = width
        sen.height = height
        sen.duration = duration
        sen.replyMsg = replyMsg
        IMHelper.send(sen, callId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = false, ignoreConnecting = false, sendBefore = null)
    }

    fun resendMessage(clientId: String) {
        IMHelper.withDb {
            it?.sendMsgDao()?.findByCallId(clientId)
        }?.let { resendMessage(it) }
    }

    internal fun resendMessage(info: SendMessageReqEn) {
        val sendBefore = if (MsgType.hasUploadType(info.msgType)) {
            if (!info.url.isNullOrEmpty()) null else MsgFileUploader(info)
        } else null
        IMHelper.resend(info, info.clientMsgId, Constance.SEND_MSG_DEFAULT_TIMEOUT, isSpecialData = false, ignoreConnecting = false, sendBefore = sendBefore)
    }

    class MsgFileUploader(private val d: SendMessageReqEn) : BaseFileSender(Constance.app, d, d.clientMsgId) {

        private val mVideoOutputPath = "/compress/im/${d.clientMsgId}.mp4"
        private val mImageOutputPath = "/compress/im/${d.clientMsgId}"

        private val onImgCompressListener = object : com.zj.compress.images.CompressListener {

            override fun onFileTransform(p0: String?) {
                ImLogs.d("MsgFileUploader", "image path transformed")
            }

            override fun onStart() {
                ImLogs.d("MsgFileUploader", "image start compress ... ")
            }

            override fun onSuccess(path: String?) {
                ImLogs.d("MsgFileUploader", "image compress success ")
                val deleteOriginalFile = path != d.localFilePath
                d.localFilePath = path
                super@MsgFileUploader.startUpload(deleteOriginalFile)
            }

            override fun onError(code: Int, e: Throwable?) {
                ImLogs.d("MsgFileUploader", "image compress error with code : $code case: ${e?.message} ")
                onStatus?.call(true, d.clientMsgId, 0, d, false, e)
            }
        }

        private val onVideoCompressListener = object : com.zj.compress.videos.CompressListener {
            override fun onSuccess(p0: String?) {
                ImLogs.d("MsgFileUploader", "video start compress ... ")
                val deleteOriginalFile = p0 != d.localFilePath
                d.localFilePath = p0
                super@MsgFileUploader.startUpload(deleteOriginalFile)
            }

            override fun onCancel() {
                ImLogs.d("MsgFileUploader", "video compress canceled! ")
                cancel()
            }

            override fun onProgress(p0: Float) {
                ImLogs.d("MsgFileUploader", "video compress progress changed => ${p0 * 100}%")
            }

            override fun onError(p0: Int, case: String) {
                ImLogs.d("MsgFileUploader", "video compress error case : $case ")
                onStatus?.call(true, d.clientMsgId, 0, d, false, IllegalArgumentException(case))
            }

            override fun onFileTransform(p0: String?) {
                ImLogs.d("MsgFileUploader", "video compress , compatible with inaccessible files , the file is patched to : $p0 ")
            }
        }

        override fun startUpload(isDeleteFileAfterUpload: Boolean) {
            val c = (context?.applicationContext as? Application) ?: throw NullPointerException("context should not be null !!")
            val cu = CompressUtils.with(c).load(d.localFilePath)
            when (d.msgType) {
                MsgType.IMG.type -> {
                    cu.asImage().ignoreBy(1024).setTargetPath(mImageOutputPath).start(onImgCompressListener)
                }

                MsgType.VIDEO.type -> {
                    cu.asVideo().setLevel(2000).setOutPutFileName(mVideoOutputPath).start(onVideoCompressListener)
                }

                MsgType.AUDIO.type -> {
                    super.startUpload(true)
                }
            }
        }
    }
}