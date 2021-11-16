package com.zj.imtest

import android.widget.Toast
import com.zj.ccIm.core.ImConfigIn
import com.zj.im.chat.exceptions.IMException
import kotlin.system.exitProcess

object IMConfig : ImConfigIn {
    override fun getUserId(): Int {
        return 151120
    }

    override fun getToken(): String {
        return "sanhe12345"
    }

    override fun getIMHost(): String {
        return "https://im.ccdev.lerjin.com"
    }

    override fun getGrpcAddress(): Pair<String, Int> {
        return Pair("grpc.ccdev.lerjin.com", 50003)
    }

    override fun getApiHeader(): Map<String, String> {
        return BaseApp.getHeader()
    }

    override fun logAble(): Boolean {
        return true
    }

    override fun debugAble(): Boolean {
        return true
    }

    override fun getUserName(): String {
        return "if 油"
    }

    override fun getUserAvatar(): String {
        return "https://img1.baidu.com/it/u=2693627099,4115094120&fm=26&fmt=auto&gp=0.jpg"
    }

    override fun onAuthenticationError() {
        BaseApp.context.let {
            Toast.makeText(it, "TOKEN IS INVALID", Toast.LENGTH_SHORT).show()
        }
        exitProcess(0)
    }

    override fun getHeatBeatsTimeOut(): Long {
        return 3000
    }

    override fun getIdleTimeOut(): Long {
        return 24 * 60 * 60 * 1000
    }

    override fun onSdkDeadlyError(e: IMException) {
        BaseApp.initChat()
    }
}