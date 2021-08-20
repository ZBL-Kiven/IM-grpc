package com.zj.im.core.api

import com.zj.im.core.bean.SendMessageReqEn
import com.zj.im.core.bean.SendMessageRespEn
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST


interface SenderApi {

    @POST("/im/message/send")
    fun sendMsg(@Body sendMessageReqEn: SendMessageReqEn): Observable<SendMessageRespEn?>


}