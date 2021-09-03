package com.zj.ccIm.core.api

import com.zj.ccIm.core.bean.UpdateSessionInfoRespEn
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface OptionApi {

    @POST("/im/group/setting")
    fun updateSessionInfo(@Body configs: RequestBody): Observable<UpdateSessionInfoRespEn?>

}