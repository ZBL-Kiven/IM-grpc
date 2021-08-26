package com.zj.ccIm.core.sender

import android.content.Context
import com.zj.im.sender.OnStatus
import com.zj.ccIm.core.Constance.toMd5
import com.zj.database.entity.SendMessageReqEn
import java.lang.IllegalArgumentException
import java.lang.NullPointerException
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.MsgType
import com.zj.ccIm.core.bean.UploadRespEn
import java.io.File
import java.net.URL

class FileUploader(private val context: Context, private val d: SendMessageReqEn, private val callId: String, private val isDeleteFileAfterUpload: Boolean, private var onStatus: OnStatus<Any?>?) {

    private var sendingToken: MsgSender.UploadTask? = null
    private var inRunning = false

    private val observer = object : MsgSender.SendingObserver {
        override fun onCompleted(uploadId: String) {
            sendingToken?.destroy()
            sendingToken = null
        }

        override fun onError(uploadId: String, exception: Throwable?) {
            exception?.printStackTrace()
            onStatus?.call(true, uploadId, 0, d, isOK = false, exception)
        }

        override fun onProgress(uploadId: String, progress: Int) {
            onStatus?.call(false, uploadId, progress, d, isOK = false, null)
        }

        override fun onSuccess(uploadId: String, body: UploadRespEn, totalBytes: Long) {
            if (body.success) {
                d.uploadDataTotalByte = totalBytes
                d.url = body.url
                d.localFilePath = null
                onStatus?.onSendingInfoChanged(callId, d)
                onStatus?.call(true, uploadId, 100, d, isOK = true, null)
            } else onError(callId, Exception("The upload may have been successful, but the server still returns failure !"))
        }
    }

    init {
        start()
    }

    private fun start() {
        inRunning = true
        val timeStamp = System.currentTimeMillis()
        val path = d.localFilePath
        val f = File(path ?: "")
        if (path.isNullOrEmpty() || !f.exists()) {
            observer.onError(callId, NullPointerException("the path $path can not be attach to a file"));return
        }
        if (d.msgType == MsgType.TEXT.type) {
            observer.onError(callId, IllegalArgumentException("the send msg type is not supported from text !!"));return
        }
        val serverUploadUrl = URL("${IMHelper.imConfig.getIMHost()}/im/upload/file")
        val headers = mutableMapOf("Content-Type" to "multipart/form-data", "userId" to "${IMHelper.imConfig.getUserId()}", "token" to IMHelper.imConfig.getToken(), "timeStamp" to "$timeStamp")
        val sign = "$timeStamp;${IMHelper.imConfig.getUserId()};${d.msgType}".toMd5()
        val fileInfo = MsgSender.FileInfo(f.name, "file", path = path)
        val params = mapOf("sign" to sign, "type" to d.msgType)
        sendingToken = MsgSender.with(context.applicationContext, serverUploadUrl).callId(callId).addHeader(headers).addParams(params).setFileInfo(fileInfo).deleteFileAfterUpload(isDeleteFileAfterUpload).start(observer)
    }


    fun cancel() {
        onStatus?.call(true, callId, 0, d, isOK = false, null)
        sendingToken?.cancel()
    }
}