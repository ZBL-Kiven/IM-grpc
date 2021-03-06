package com.zj.ccIm.core.sender

import com.zj.api.base.BaseRetrofit
import com.zj.api.interceptor.HeaderProvider
import com.zj.api.okhttp3.ProgressListener
import com.zj.api.okhttp3.ProgressRequestBody
import com.zj.ccIm.core.api.ImApi
import com.zj.ccIm.core.bean.UploadRespEn
import com.zj.compress.videos.FileUtils
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.net.URL
import java.util.*


@Suppress("unused")
internal object FileUploader {

    fun with(url: URL): Builder {
        return Builder(url)
    }

    @Suppress("SpellCheckingInspection")
    internal class UploadTask(private val builder: Builder, private val observer: SendingObserver) : ProgressListener {

        private var reqCompo: BaseRetrofit.RequestCompo? = null
        private var totalBytes: Long = 0

        init {
            upload()
        }

        private fun upload() {
            val part = builder.fileInfo?.let {
                val f = File(it.path)
                val rq = RequestBody.create(MediaType.parse(builder.contentType), f)
                val progressBody = ProgressRequestBody(rq, it.uploadIndex, this)
                MultipartBody.Part.createFormData(it.paramName, it.name, progressBody)
            } ?: return
            val map = mutableMapOf<String, RequestBody>()
            builder.req?.forEach { (s, s1) ->
                val paramsPart = RequestBody.create(MediaType.parse(builder.contentType), s1 ?: "")
                map[s] = paramsPart
            }
            val header = builder.headers?.let {
                object : HeaderProvider {
                    override fun headers(): Map<out String, String> {
                        return it
                    }
                }
            }
            reqCompo = ImApi.getRecordApi(header).call({ it.upload(map, part) }, Schedulers.io(), Schedulers.io()) { isSuccess, data, throwable, a ->
                if (isSuccess && data?.success == true) {
                    if (builder.deleteCompressFile) FileUtils.delete(builder.fileInfo?.path)
                    observer.onSuccess(builder.callId, data, totalBytes)
                } else {
                    observer.onError(builder.callId, throwable, a)
                }
                observer.onCompleted(builder.callId)
            }
        }

        fun cancel() {
            observer.onError(builder.callId, InterruptedException("canceled!!"), null)
            reqCompo?.cancel()
        }

        fun destroy() {
            builder.invalid()
            reqCompo?.cancel()
        }

        override fun onProgress(fileIndex: Int, progress: Int, contentLength: Long) {
            this.totalBytes = contentLength
            observer.onProgress(builder.callId, progress)
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    internal class Builder(val url: URL) {

        var deleteCompressFile: Boolean = false
        var callId: String = UUID.randomUUID().toString()
        var headers: MutableMap<String, String>? = null
        var req: Map<String, String?>? = null
        var fileInfo: FileInfo? = null
        var contentType: String = "multipart/form-data"
        lateinit var uploadTask: UploadTask

        fun contentType(contentType: String): Builder {
            this.contentType = contentType
            return this
        }

        fun addHeader(headers: MutableMap<String, String>): Builder {
            this.headers = headers
            return this
        }

        fun callId(cid: String): Builder {
            this.callId = cid
            return this
        }

        fun setFileInfo(info: FileInfo): Builder {
            fileInfo = info
            return this
        }

        fun deleteFileAfterUpload(isDelete: Boolean): Builder {
            this.deleteCompressFile = isDelete
            return this
        }

        fun addParams(req: Map<String, String?>?): Builder {
            this.req = req
            return this
        }

        fun start(observer: SendingObserver): UploadTask {
            uploadTask = UploadTask(this, observer)
            return uploadTask
        }

        internal fun invalid() {
            headers?.clear()
            fileInfo = null
            req = null
            callId = "-recycled-"
        }
    }

    internal interface SendingObserver {
        fun onCompleted(uploadId: String)
        fun onError(uploadId: String, exception: Throwable?, errorBody: Any?)
        fun onProgress(uploadId: String, progress: Int)
        fun onSuccess(uploadId: String, body: UploadRespEn, totalBytes: Long)
    }

    internal data class FileInfo(val name: String, val paramName: String, val path: String, val uploadIndex: Int = 0)
}