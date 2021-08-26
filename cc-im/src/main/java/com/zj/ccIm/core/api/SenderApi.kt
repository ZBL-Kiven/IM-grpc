package com.zj.ccIm.core.api

import com.zj.ccIm.core.bean.SendMessageRespEn
import com.zj.ccIm.core.bean.UploadRespEn
import com.zj.database.entity.SendMessageReqEn
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


interface SenderApi {

    @POST("/im/message/send")
    fun sendMsg(@Body sendMessageReqEn: SendMessageReqEn): Observable<SendMessageRespEn?>


    @Multipart
    @POST("/im/upload/file")
    fun upload(@PartMap params: @JvmSuppressWildcards Map<String, RequestBody>, @Part file: MultipartBody.Part): Observable<UploadRespEn?>


    @Multipart
    @POST("/im/upload/file")
    fun uploadFile(@Part("sign") sign: RequestBody, @Part("type") type: RequestBody, @Part file: MultipartBody.Part): Observable<UploadRespEn?>

}