package com.zj.ccIm.core.api

import com.zj.database.entity.SendMessageReqEn
import com.zj.ccIm.core.bean.SendMessageRespEn
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST


interface SenderApi {

    @POST("/im/message/send")
    fun sendMsg(@Body sendMessageReqEn: SendMessageReqEn): Observable<SendMessageRespEn?>


}