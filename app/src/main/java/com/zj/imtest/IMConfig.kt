package com.zj.imtest

import com.zj.ccIm.core.ImConfigIn
import java.net.URL

object IMConfig : ImConfigIn {

    override fun getUserId(): Int {
        return 120539
    }

    override fun getToken(): String {
        return "ZDU2OTk4ODctMGI1Yi00MjdmLWFhYTUtMTU1ODMxOWY3ZmVh"
    }

    override fun getGrpcAddress(): Pair<String, Int> {
        //grpc.ccdev.lerjin.com
        return Pair("172.16.1.75", 50003)
    }

    override fun getIMHost(): String {
//        return "https://im.ccdev.lerjin.com"
        return  "http://172.16.1.75:8086"
    }

}