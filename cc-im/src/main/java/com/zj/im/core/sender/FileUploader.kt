package com.zj.im.core.sender

import android.content.Context
import com.zj.im.sender.OnStatus
import com.zj.im.core.Constance
import com.zj.im.core.Constance.toMd5
import com.zj.im.core.bean.SendMessageReqEn
import com.zj.protocol.sender.MsgSender
import java.lang.IllegalArgumentException
import java.lang.NullPointerException
import com.google.gson.Gson
import com.zj.im.core.IMHelper
import java.net.URL

class FileUploader(private val context: Context, private val d: SendMessageReqEn, private val callId: String, private val isDeleteFileAfterUpload: Boolean, private var onStatus: OnStatus?) {

    private var sendingToken: MsgSender.UploadTask? = null
    private var inRunning = false

    init {
        start()
    }

    private val observer = object : MsgSender.SendingObserver {
        override fun onCompleted(uploadId: String) {
            sendingToken?.destroy()
            sendingToken = null
        }

        override fun onError(uploadId: String, exception: Throwable) {
            exception.printStackTrace()
            onStatus?.call(uploadId, 0, isOK = false, isCancel = false)
        }

        override fun onProgress(uploadId: String, progress: Int) {
            onStatus?.call(uploadId, progress, isOK = false, isCancel = false)
        }

        override fun onSuccess(uploadId: String, body: String, totalBytes: Long) {
            val resp = Gson().fromJson(body, UploadResp::class.java)
            if (resp.success) {
                d.uploadDataTotalByte = totalBytes
                d.url = resp.url
                onStatus?.call(uploadId, 100, isOK = true, isCancel = false)
            } else onError(callId, Exception("The upload may have been successful, but the server still returns failure !"))
        }
    }

    private fun start() {
        inRunning = true
        val timeStamp = System.currentTimeMillis()
        val path = d.localFilePath
        if (path.isNullOrEmpty()) {
            observer.onError(callId, NullPointerException("file path is null or empty !!"));return
        }
        if (d.msgType == Constance.MsgType.text.name) {
            observer.onError(callId, IllegalArgumentException("the send msg type is not supported from text !!"));return
        }
        val serverUploadUrl = URL("${IMHelper.imConfig.getIMHost()}/im/upload/file")
        val headers = mutableMapOf("Content-Type" to "multipart/form-data", "userId" to "${IMHelper.imConfig.getUserId()}", "token" to IMHelper.imConfig.getToken(), "timeStamp" to "$timeStamp")
        val sign = "{$timeStamp;${IMHelper.imConfig.getUserId()};${d.msgType}".toMd5()
        val fileInfo = MsgSender.FileInfo(path, "file", "")
        val params = mapOf("sign" to sign, "type" to d.msgType)
        sendingToken = MsgSender.with(context.applicationContext, serverUploadUrl).callId(callId).addHeader(headers).addParams(params).addFileInfo(fileInfo).deleteFileAfterUpload(isDeleteFileAfterUpload).start(observer)
    }

    fun cancel() {
        onStatus?.call(callId, 0, isOK = false, isCancel = true)
        sendingToken?.cancel()
    }

    private class UploadResp {
        var url: String? = ""
        var success: Boolean = false
    }
}