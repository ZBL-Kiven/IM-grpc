package com.zj.ccIm.core.sender

import android.content.Context
import com.zj.im.sender.OnSendBefore
import com.zj.im.sender.OnStatus
import com.zj.database.entity.SendMessageReqEn
import java.lang.Exception
import java.lang.NullPointerException

open class BaseFileSender(protected val context: Context?, private val d: SendMessageReqEn, private val callId: String) : OnSendBefore<Any?> {

    private var uploader: FileUploader? = null
    protected var onStatus: OnStatus<Any?>? = null

    final override fun call(onStatus: OnStatus<Any?>) {
        this.onStatus = onStatus
        startUpload(false)
    }

    protected open fun startUpload(isDeleteFileAfterUpload: Boolean) {
        if (isDeleteFileAfterUpload) onStatus?.onSendingInfoChanged(callId, d)
        context?.let { uploader = FileUploader(it, d, callId, isDeleteFileAfterUpload, this.onStatus) } ?: onStatus?.call(true, callId, 0, isOK = false, NullPointerException("context should not be null !!"))
    }

    open fun cancel() {
        uploader?.cancel()
    }


    companion object {

        private val c = arrayOf(" B", " KB", " M", " G", " T", " P")

        fun numFormat(num: Long, index: Int = 0): String {
            return try {
                if (num < 1000) return "${(num * 10f).toInt() / 10f} ${c[index]}"
                else {
                    numFormat(num / 1000, index + 1)
                }
            } catch (e: Exception) {
                e.printStackTrace();""
            }
        }
    }

}