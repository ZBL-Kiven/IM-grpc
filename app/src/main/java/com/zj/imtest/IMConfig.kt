package com.zj.imtest

import com.zj.ccIm.core.ImConfigIn

object IMConfig : ImConfigIn {

    override fun getUserId(): Int {
        return 117659
    }

    override fun getUserName(): String {
        return "if 油"
    }

    override fun getUserAvatar(): String {
        return "https://img1.baidu.com/it/u=2693627099,4115094120&fm=26&fmt=auto&gp=0.jpg"
    }

    override fun getToken(): String {
        return "sanhe12345"
    }

    override fun getGrpcAddress(): Pair<String, Int> {

        return Pair("grpc.ccdev.lerjin.com", 50003)

        //        return Pair("172.16.1.75", 50003)
    }

    override fun getIMHost(): String {

        return "https://im.ccdev.lerjin.com"

        //                return "http://172.16.1.75:8085"
    }

    override fun getHeatBeatsTimeOut(): Long {
        return 5000
    }

    override fun getIdleTimeOut(): Long {
        return 24 * 60 * 60 * 1000
    }

}