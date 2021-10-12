package com.zj.ccIm.core.api

import com.zj.ccIm.core.bean.SendMessageRespEn
import com.zj.ccIm.core.bean.UpdateSessionInfoRespEn
import com.zj.ccIm.core.bean.UploadRespEn
import com.zj.database.entity.MessageInfoEntity
import com.zj.database.entity.SendMessageReqEn
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


interface IMRecordSizeApi {

    @POST("/im/message/send")
    fun sendMsg(@Body sendMessageReqEn: SendMessageReqEn): Observable<SendMessageRespEn?>

    @Multipart
    @POST("/im/upload/file")
    fun upload(@PartMap params: @JvmSuppressWildcards Map<String, RequestBody>, @Part file: MultipartBody.Part): Observable<UploadRespEn?>

    @GET("/im/message/read/msg/interval")
    fun getOfflineMsgList(@Query("msgId") msgId: Long?, @Query("groupId") groupId: Long?, @Query("ownerId") ownerId: Int?, @Query("targetUserId") targetUserId: Int?, @Query("type") type: Int? = null, @Query("channel") channels: Array<out String>): Observable<Map<String, List<MessageInfoEntity?>?>?>

    @POST("/im/group/setting")
    fun updateSessionInfo(@Body configs: RequestBody): Observable<UpdateSessionInfoRespEn?>
}