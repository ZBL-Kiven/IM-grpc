package com.zj.imtest.core.sender

import com.zj.imtest.core.bean.SendMessageReqEn
import com.zj.imtest.core.bean.SendMessageRespEn
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST


interface SenderApi {

    @POST("/im/message/send")
    fun sendMsg(@Body sendMessageReqEn: SendMessageReqEn): Observable<SendMessageRespEn?>


}