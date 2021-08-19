package com.zj.protocol.sender

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.gotev.uploadservice.UploadService

import net.gotev.uploadservice.data.UploadInfo
import net.gotev.uploadservice.network.ServerResponse
import net.gotev.uploadservice.observer.request.RequestObserverDelegate
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest
import java.net.URL
import java.util.*


@Suppress("unused")
object MsgSender {

    fun with(context: Context?, url: URL): Builder {
        return Builder(context, url)
    }

    @Suppress("SpellCheckingInspection")
    class UploadTask(owner: LifecycleOwner, context: Context?, private val builder: Builder, private val observer: SendingObserver) : LifecycleRegistry(owner) {

        private val delegate = object : RequestObserverDelegate {
            override fun onCompleted(context: Context, uploadInfo: UploadInfo) {
                observer.onCompleted(uploadInfo.uploadId)
            }

            override fun onCompletedWhileNotObserving() {
                observer.onCompleted(builder.callId)
            }

            override fun onError(context: Context, uploadInfo: UploadInfo, exception: Throwable) {
                observer.onError(uploadInfo.uploadId, exception)
            }

            override fun onProgress(context: Context, uploadInfo: UploadInfo) {
                observer.onProgress(builder.callId, uploadInfo.progressPercent)
            }

            override fun onSuccess(context: Context, uploadInfo: UploadInfo, serverResponse: ServerResponse) {
                observer.onSuccess(uploadInfo.uploadId, serverResponse.bodyString, uploadInfo.totalBytes)
            }
        }

        init {
            if (context == null) {
                delegate.onCompletedWhileNotObserving()
            } else {
                val request = MultipartUploadRequest(context, builder.url.toString()).setMethod("post")
                builder.headers?.forEach { (k, v) ->
                    request.addHeader(k, v)
                }
                builder.req?.let {
                    it.forEach { (s, s1) -> request.addParameter(s, s1 ?: "") }
                }
                builder.fileInfo.forEach {
                    request.addFileToUpload(it.path, it.paramName, it.name, it.contentType)
                }
                request.setAutoDeleteFilesAfterSuccessfulUpload(builder.deleteCompressFile)
                request.setUploadID(builder.callId)
                request.subscribe(context, builder, delegate)
                request.startUpload()
            }
            currentState = State.RESUMED
        }

        fun cancel() {
            observer.onError(builder.callId, InterruptedException("canceled!!"))
            currentState = State.DESTROYED
            UploadService.stopUpload(builder.callId)
        }

        fun destroy() {
            builder.invalid()
            currentState = State.DESTROYED
            UploadService.stopAllUploads()
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    class Builder(private var context: Context?, val url: URL) : LifecycleOwner {

        var deleteCompressFile: Boolean = false
        var callId: String = UUID.randomUUID().toString()
        var headers: MutableMap<String, String>? = null
        var req: Map<String, String?>? = null
        var fileInfo = mutableListOf<FileInfo>()
        lateinit var uploadTask: UploadTask

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
            uploadTask = UploadTask(this, context?.applicationContext, this, observer)
            return uploadTask
        }

        internal fun invalid() {
            fileInfo.clear()
            headers?.clear()
            req = null
            callId = "-recycled-"
        }

        override fun getLifecycle(): Lifecycle {
            return uploadTask
        }
    }

    interface SendingObserver {
        fun onCompleted(uploadId: String)
        fun onError(uploadId: String, exception: Throwable)
        fun onProgress(uploadId: String, progress: Int)
        fun onSuccess(uploadId: String, body: String, totalBytes: Long)
    }

    data class FileInfo(val name: String, val paramName: String, val path: String, val contentType: String? = "mutipart/form-data")

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