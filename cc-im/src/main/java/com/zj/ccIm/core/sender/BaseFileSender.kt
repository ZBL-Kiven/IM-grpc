package com.zj.ccIm.core.sender

import android.content.Context
import com.zj.im.sender.OnSendBefore
import com.zj.im.sender.OnStatus
import com.zj.database.entity.SendMessageReqEn
import java.lang.NullPointerException

open class BaseFileSender(protected val context: Context?, private val d: SendMessageReqEn, private val callId: String) : OnSendBefore<Any?> {

    private var uploader: FileUploader? = null
    protected var onStatus: OnStatus<Any?>? = null

    final override fun call(onStatus: OnStatus<Any?>) {
        this.onStatus = onStatus
        startUpload(false)
    }

    protected open fun startUpload(isDeleteFileAfterUpload: Boolean) {
        context?.let { uploader = FileUploader(it, d, callId, isDeleteFileAfterUpload, this.onStatus) } ?: onStatus?.call(true, callId, 0, d, isOK = false, NullPointerException("context should not be null !!"))
    }

    open fun cancel() {
        uploader?.cancel()
    }
}