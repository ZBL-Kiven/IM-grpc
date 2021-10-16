package com.zj.imtest

import android.widget.Toast
import com.zj.ccIm.core.ImConfigIn

object IMConfig : ImConfigIn {

    override fun getUserId(): Int {

        return 151120

        // return 151120 //v
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

    override fun onAuthenticationError() {
        MainActivity.app?.let {
            Toast.makeText(it, "TOKEN IS INVALID", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getGrpcAddress(): Pair<String, Int> {

        return Pair("grpc.ccdev.lerjin.com", 50003)

        //        return Pair("192.168.50.213", 50003)
    }

    override fun getIMHost(): String {

        return "https://im.ccdev.lerjin.com"

//                return "http://172.16.1.75:8086"
    }

    override fun getHeatBeatsTimeOut(): Long {
        return 5
    }

    override fun getIdleTimeOut(): Long {
        return 5 * 1000
    }

}