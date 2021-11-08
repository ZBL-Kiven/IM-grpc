package com.zj.ccIm.core.sender

import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.MsgType
import com.zj.ccIm.core.bean.UploadRespEn
import com.zj.ccIm.core.toMd5
import com.zj.database.entity.SendMessageReqEn
import com.zj.im.sender.OnSendBefore
import com.zj.im.sender.OnStatus
import java.io.File
import java.net.URL

internal open class BaseFileSender(private val d: SendMessageReqEn, private val callId: String) : OnSendBefore<Any?> {

    protected var onStatus: OnStatus<Any?>? = null
    private var sendingToken: FileUploader.UploadTask? = null
    private var inRunning = false

    private val observer = object : FileUploader.SendingObserver {
        override fun onCompleted(uploadId: String) {
            sendingToken?.destroy()
            sendingToken = null
        }

        override fun onError(uploadId: String, exception: Throwable?, errorBody: Any?) {
            exception?.printStackTrace()
            onStatus?.call(true, uploadId, 0, d, isOK = false, exception, errorBody)
        }

        override fun onProgress(uploadId: String, progress: Int) {
            onStatus?.call(false, uploadId, progress, d, isOK = false, null)
        }

        override fun onSuccess(uploadId: String, body: UploadRespEn, totalBytes: Long) {
            if (body.success) {
                d.url = body.url
                onStatus?.call(true, uploadId, 100, d, isOK = true, null)
            } else onError(callId, Exception("The upload may have been successful, but the server still returns failure !"), null)
        }
    }

    final override fun call(onStatus: OnStatus<Any?>) {
        this.onStatus = onStatus
        try {
            startUpload(false)
        } catch (e: Exception) {
            onStatus.call(true, callId, 0, d, false, e)
            e.printStackTrace()
        }
    }

    protected open fun startUpload(isDeleteFileAfterUpload: Boolean) {
        inRunning = true
        val timeStamp = System.currentTimeMillis()
        val path = d.localFilePath
        val f = if (path.isNullOrEmpty()) null else File(path)
        if (path.isNullOrEmpty() || f == null || !f.exists()) {
            observer.onError(callId, NullPointerException("the path $path can not be attach to a file"), null);return
        }
        if (d.msgType == MsgType.TEXT.type) {
            observer.onError(callId, IllegalArgumentException("the send msg type is not supported from text !!"), null);return
        }
        val serverUploadUrl = URL("${IMHelper.imConfig?.getIMHost()}/im/upload/file")
        val headers = mutableMapOf("Content-Type" to "multipart/form-data", "userId" to "${IMHelper.imConfig?.getUserId() ?: ""}", "token" to (IMHelper.imConfig?.getToken() ?: ""), "timeStamp" to "$timeStamp")
        val sign = "$timeStamp;${IMHelper.imConfig?.getUserId()};${d.msgType}".toMd5()
        val fileInfo = FileUploader.FileInfo(f.name, "file", path = path)
        val params = mapOf("sign" to sign, "type" to d.msgType)
        sendingToken = FileUploader.with(serverUploadUrl).callId(callId).addHeader(headers).addParams(params).setFileInfo(fileInfo).deleteFileAfterUpload(isDeleteFileAfterUpload).start(observer)
    }

    open fun cancel() {
        onStatus?.call(true, callId, 0, d, isOK = false, null)
        sendingToken?.cancel()
    }
}