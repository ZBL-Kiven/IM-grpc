package com.zj.imtest

import com.zj.ccIm.core.ImConfigIn

object IMConfig : ImConfigIn {

    override fun getUserId(): Int {
//        return 151253

                return 151120 //v
    }

    override fun getUserName(): String {
        return "if 油"
    }

    override fun getUserAvatar(): String {
        return "https://img1.baidu.com/it/u=2693627099,4115094120&fm=26&fmt=auto&gp=0.jpg"
    }

    override fun getToken(): String {
//        return "NjRlN2ViMjYtNjAxNi00NDIxLTkwNmEtZjc4NWU3ZWExZmI5"

                return "NWNhNTQ3NGYtMjFjZi00MTQyLWJlODYtZjAwZjU4M2YzZDgw"  //v
    }

    override fun getGrpcAddress(): Pair<String, Int> {

        return Pair("grpc.ccdev.lerjin.com", 50003)

        //        return Pair("192.168.50.213", 50003)
    }

    override fun getIMHost(): String {

        return "https://im.ccdev.lerjin.com"

        //        return "http://172.16.1.75:8086"
    }

    override fun getHeatBeatsTimeOut(): Long {
        return 5
    }

    override fun getIdleTimeOut(): Long {
        return 5 * 1000
    }

}