package com.zj.imtest

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.zj.ccIm.core.ImConfigIn
import com.zj.im.chat.exceptions.IMException


class IMConfig(private val uid: Int) : ImConfigIn {

    companion object {
        var defaultTestUid = 151147
        const val ROUTE_CALL_ID_REPLY_MESSAGE = "im_route_reply_message"
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
        Handler(Looper.getMainLooper()).post {
            BaseApp.context.let { ctx ->
                Toast.makeText(ctx, "User : ${getUserId()} has kicked out by other device!", Toast.LENGTH_SHORT).show()
            }
            BaseApp.backToSplash()
        }
    }

    override fun getHeatBeatsTimeOut(): Long {
        return 3000
    }

    override fun getIdleTimeOut(): Long {
        return 24 * 60 * 60 * 1000
    }

    override fun onAlertError(e: IMException) {
        e.printStackTrace()
    }

    override fun onSdkDeadlyError(e: IMException) {
        if (e.errorLevel == IMException.ERROR_LEVEL_DEADLY) BaseApp.initChat(uid)
        else Log.e(" ERROR !! =====>", e.message ?: "")
    }
}