package com.zj.imtest

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.zj.ccIm.core.ImConfigIn
import com.zj.im.chat.exceptions.IMException
import kotlin.system.exitProcess

class IMConfig(private val uid: Int) : ImConfigIn {

    companion object {
        const val defaultUid = 151118
    }

    override fun getUserId(): Int {
        return uid
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
        Handler(Looper.getMainLooper()).let {
            it.post {
                BaseApp.context.let { ctx ->
                    Toast.makeText(ctx, "User : ${getUserId()} has kicked out by other device!", Toast.LENGTH_SHORT).show()
                }
                BaseApp.backToSplash()
            }
        }
    }

    override fun getHeatBeatsTimeOut(): Long {
        return 3000
    }

    override fun getIdleTimeOut(): Long {
        return 24 * 60 * 60 * 1000
    }

    override fun onSdkDeadlyError(e: IMException) {
        BaseApp.initChat(uid)
    }
}