package com.zj.ccIm.core.api

import com.zj.ccIm.core.bean.*
import com.zj.database.entity.SessionLastMsgInfo
import io.reactivex.Observable
import org.json.JSONObject
import retrofit2.http.*

interface FunctionApi {

    @GET("/im/group/read/list")
    fun fetchSessions(@Query("timeStamp") ts: Long): Observable<FetcherSessionBean?>

    @GET("/im/message/read/last/message")
    fun fetchSessionLastMessage(@Query("groupId") groupIds: List<Long>?): Observable<List<SessionLastMsgInfo>?>

    @GET("/im/group/read/private/list")
    fun fetchPrivateOwnerSessions(@Query("timeStamp") ts: Long): Observable<FetchPrivateOwnerSessionBean?>

    @GET("/im/message/read/last/group/private/message")
    fun fetchPrivateOwnerLastMessage(@Query("ownerId") groupIds: List<Int>?): Observable<List<SessionLastMsgInfo>?>

    @POST("/im/group/private/chat/remove")
    fun deleteSession(@Body data: DeleteSessionInfo): Observable<JSONObject>
}