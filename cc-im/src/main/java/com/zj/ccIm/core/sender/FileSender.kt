package com.zj.ccIm.core.sender

import com.zj.ccIm.core.Constance
import com.zj.ccIm.core.MsgType
import com.zj.ccIm.logger.ImLogs
import com.zj.compress.CompressUtils
import com.zj.database.entity.SendMessageReqEn

internal class FileSender(private val d: SendMessageReqEn) : BaseFileSender(d, d.clientMsgId) {

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
            ImLogs.d("MsgFileUploader", "image path transformed to $p0")
        }

        override fun onStart() {
            ImLogs.d("MsgFileUploader", "image start compress ... ")
        }

        override fun onSuccess(path: String?) {
            ImLogs.d("MsgFileUploader", "image compress success ")
            val deleteOriginalFile = path != d.localFilePath
            d.tempFilePath = path
            super@FileSender.startUpload(deleteOriginalFile)
        }

        override fun onError(code: Int, e: Throwable?) {
            ImLogs.d("MsgFileUploader", "image compress error with code : $code case: ${e?.message} ")
            onStatus?.call(true, d.clientMsgId, 0, d, false, e)
        }
    }

    private val onVideoCompressListener = object : com.zj.compress.videos.CompressListener {
        override fun onSuccess(s: String?) {
            ImLogs.d("MsgFileUploader", "video start compress ... ")
            val deleteOriginalFile = s != d.localFilePath
            d.tempFilePath = s
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
        when (d.msgType) {
            MsgType.IMG.type -> {
                val c = (Constance.app) ?: return
                val cu = CompressUtils.with(c).load(d.localFilePath)
                cu.asImage().ignoreBy(300).setTargetPath(mImageOutputPath).start(onImgCompressListener)
            }

            MsgType.VIDEO.type -> {
                val c = (Constance.app) ?: return
                val cu = CompressUtils.with(c).load(d.localFilePath)
                cu.asVideo().setLevel(2000).setOutPutFileName(mVideoOutputPath).start(onVideoCompressListener)
            }

            else -> {
                d.tempFilePath = d.localFilePath
                super.startUpload(false)
            }
        }
    }
}