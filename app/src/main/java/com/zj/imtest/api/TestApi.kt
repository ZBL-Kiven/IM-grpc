package com.zj.imtest.api

import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TestApi {
    /**
     * 针对用户个人进行关注和取消关注的操作
     */
    @FormUrlEncoded
    @POST("/user/relationship/follow")
    fun setUserRelationshipFollow(@Field("userid") userid: Int, @Field("token") token: String, @Field("target_userid") target_userid: Int, @Field("status") status: Int): Observable<BaseCCResp<String?>>

}