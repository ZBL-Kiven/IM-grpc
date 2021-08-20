package com.zj.im.core.api

import com.zj.database.entity.FetchSessionMsgInfo
import com.zj.im.core.bean.FetcherSessionBean
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface FetcherApi {

    @GET("/im/group/read/list")
    fun fetchSessions(@Query("timeStamp") ts: Long): Observable<FetcherSessionBean?>


    @GET("/im/message/read/last/message")
    fun fetchSessionLastMessage(@Query("groupId") groupIds: List<Long>?): Observable<List<FetchSessionMsgInfo>?>

}