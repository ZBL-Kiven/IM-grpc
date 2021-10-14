package com.zj.ccIm.core.sender

import android.app.Application
import com.zj.ccIm.core.Constance
import com.zj.ccIm.core.MsgType
import com.zj.ccIm.logger.ImLogs
import com.zj.compress.CompressUtils
import com.zj.database.entity.SendMessageReqEn

internal class FileSender(private val d: SendMessageReqEn) : BaseFileSender(Constance.app, d, d.clientMsgId) {

    companion object {

        fun getIfSupport(d: SendMessageReqEn): FileSender? {
            return when (d.msgType) {
                MsgType.IMG.type, MsgType.VIDEO.type, MsgType.AUDIO.type -> {
                    FileSender(d)
                }
                else -> null
            }
        }
    }

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
            super@FileSender.startUpload(deleteOriginalFile)
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
            super@FileSender.startUpload(deleteOriginalFile)
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