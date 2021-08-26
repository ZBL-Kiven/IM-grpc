package com.zj.ccIm.core.sender

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zj.api.base.BaseRetrofit
import com.zj.api.interceptor.HeaderProvider
import com.zj.api.okhttp3.ProgressListener
import com.zj.api.okhttp3.ProgressRequestBody
import com.zj.ccIm.core.api.ImApi
import com.zj.ccIm.core.bean.UploadRespEn
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import java.io.File
import java.net.URL
import java.util.*


@Suppress("unused")
object MsgSender {

    fun with(context: Context?, url: URL): Builder {
        return Builder(context, url)
    }

    @Suppress("SpellCheckingInspection")
    class UploadTask(private val builder: Builder, private val observer: SendingObserver) : ProgressListener {

        private var reqCompo: BaseRetrofit.RequestCompo? = null
        private var totalBytes: Long = 0

        init {
            upload()
        }

        private fun upload() {
            val files = builder.fileInfo
            val part = files.firstOrNull()?.let {
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
            reqCompo = ImApi.getSenderApi(header).call({ it.upload(map, part) }, Schedulers.io(), Schedulers.newThread()) { isSuccess, data, throwable ->

                Log.e("------ ", "   $isSuccess    ${data.toString()}    ${throwable?.message}")

                if (isSuccess && data?.success == true) {
                    observer.onSuccess(builder.callId, data, totalBytes)
                } else {
                    observer.onError(builder.callId, throwable)
                }
                observer.onCompleted(builder.callId)
            }
        }

        fun cancel() {
            observer.onError(builder.callId, InterruptedException("canceled!!"))
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
    class Builder(private var context: Context?, val url: URL) {

        var deleteCompressFile: Boolean = false
        var callId: String = UUID.randomUUID().toString()
        var headers: MutableMap<String, String>? = null
        var req: Map<String, String?>? = null
        var fileInfo = mutableListOf<FileInfo>()
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

        fun addFileInfo(info: FileInfo): Builder {
            fileInfo.add(info)
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
            fileInfo.clear()
            headers?.clear()
            req = null
            callId = "-recycled-"
        }
    }

    interface SendingObserver {
        fun onCompleted(uploadId: String)
        fun onError(uploadId: String, exception: Throwable?)
        fun onProgress(uploadId: String, progress: Int)
        fun onSuccess(uploadId: String, body: UploadRespEn, totalBytes: Long)
    }

    data class FileInfo(val name: String, val paramName: String, val path: String, val uploadIndex: Int = 0)


    inline fun <reified T> dataToMap(d: T): MutableMap<String, String?>? {
        val json = Gson().toJson(d)
        val token = object : TypeToken<MutableMap<String, String?>>() {}.type
        return Gson().fromJson(json, token)
    }

    inline fun <reified T> jsonToData(str: String): T? {
        return Gson().fromJson(str, T::class.java)
    }

    inline fun <T, reified TYPE> jsonToTypeData(str: String): T {
        val token = object : TypeToken<TYPE>() {}.type
        return Gson().fromJson(str, token)
    }
}