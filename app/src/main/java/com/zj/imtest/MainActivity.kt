package com.zj.imtest

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.zj.imtest.core.IMHelper
import com.zj.protocol.grpc.ImMessage


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        IMHelper.init(this.application)


        IMHelper.addReceiveObserver<String>(0x1123).listen { r, lr, payload ->
            Log.e("------ ", "on topic ==> d = $r   lstD = $lr  s = $payload")
        }

        IMHelper.addReceiveObserver<ImMessage>(0x1122).filterIn { t, _ -> t.hasSender() }.listen { m, list, s ->
            Log.e("------ ", "on message ==> d = ${m?.textContent?.text}   lstD = $list  s = $s")
        }
    }

    fun resume(view: View) {
        IMHelper.resume(0)
    }

    fun registerMsg(view: View) {
        IMHelper.registerMsgObserver(1, 1)
    }

    fun leaveChatRoom(view: View) {
        IMHelper.leaveChatRoom(1)
    }
}