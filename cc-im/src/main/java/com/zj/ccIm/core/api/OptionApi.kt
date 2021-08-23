package com.zj.ccIm.core.api

import com.zj.ccIm.core.bean.UpdateSessionInfoRespEn
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface OptionApi {

    @FormUrlEncoded
    @POST("/im/group/setting")
    fun updateSessionInfo(@Field("groupId") groupId: Long, @Field("disturbType") disturbType: Int? = null, @Field("top") top: Int? = null, @Field("des") des: String? = null, @Field("groupName") groupName: String? = null): Observable<UpdateSessionInfoRespEn?>

}