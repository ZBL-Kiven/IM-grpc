package com.zj.ccIm.core.sender

import android.content.Context
import com.zj.im.sender.OnSendBefore
import com.zj.im.sender.OnStatus
import com.zj.ccIm.core.bean.SendMessageReqEn

open class BaseFileSender(private val context: Context, private val d: SendMessageReqEn, private val callId: String, private val isDeleteFileAfterUpload: Boolean) : OnSendBefore {

    private var uploader: FileUploader? = null
    private var onStatus: OnStatus? = null

    final override fun call(onStatus: OnStatus) {
        startUpload()
    }

    protected open fun startUpload() {
        uploader = FileUploader(context, d, callId, isDeleteFileAfterUpload, this.onStatus)
    }

    open fun cancel() {
        uploader?.cancel()
    }

}