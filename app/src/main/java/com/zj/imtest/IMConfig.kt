package com.zj.imtest

import com.zj.im.core.ImConfigIn
import java.net.URL

object IMConfig : ImConfigIn {

    override fun getUserId(): Int {
        return 120539
    }

    override fun getToken(): String {
        return "ZDU2OTk4ODctMGI1Yi00MjdmLWFhYTUtMTU1ODMxOWY3ZmVh"
    }

    override fun getGrpcAddress(): URL {
        return URL("grpc.ccdev.lerjin.com:50003")
    }

    override fun getIMHost(): String {
        return "https://im.ccdev.lerjin.com"
    }

}