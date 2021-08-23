package com.zj.imtest

import com.zj.ccIm.core.ImConfigIn
import java.net.URL

object IMConfig : ImConfigIn {

    override fun getUserId(): Int {
        return 120539
    }

    override fun getToken(): String {
        return "sanhe12345"
    }

    override fun getGrpcAddress(): Pair<String, Int> {

//        return Pair("grpc.ccdev.lerjin.com", 80)

        return Pair("172.16.1.75", 50003)
    }

    override fun getIMHost(): String {

        return "https://im.ccdev.lerjin.com"

//        return "http://172.16.1.75:8085"
    }

}