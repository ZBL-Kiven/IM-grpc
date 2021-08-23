package com.zj.imtest

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zj.database.entity.MessageInfoEntity
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.sender.Sender
import com.zj.database.DbHelper
import com.zj.database.entity.SessionInfoEntity
import java.lang.StringBuilder


class MainActivity : AppCompatActivity() {


    private lateinit var text: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text = findViewById(R.id.main_text)
        IMHelper.init(this.application, IMConfig)

        IMHelper.addReceiveObserver<MessageInfoEntity>(0x1122).listen { m, list, s ->
            text.append("\non message ==> d = ${m?.textContent?.text}   lstD = $list  s = $s")
        }

        IMHelper.addReceiveObserver<SessionInfoEntity>(0x1124).listen { d, list, s ->
            text.append("\non sessions got ==> d = ${d?.groupId}   lstD = $list  s = $s")
        }
    }

    fun registerMsg(view: View) {
        IMHelper.registerMsgObserver(6, 117656)
    }

    fun leaveChatRoom(view: View) {
        IMHelper.leaveChatRoom(6)
    }

    fun queryCurrent(view: View) {
        val sb = StringBuilder()
        DbHelper.get(this)?.db?.let {
            sb.append("messageCount = ${it.messageDao().findAll().size}\n")
            sb.append("sendingCount = ${it.sendMsgDao().findAll().size}\n")
            sb.append("sessionsCount = ${it.sessionDao().findAll().size}\n")
            sb.append("sessionLastMsgCount = ${it.sessionMsgDao().findAll().size}")
        }
        Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show()
    }

    fun clearText(view: View) {
        text.text = ""
    }

    fun sendText(view: View) {
        Sender.sendText("好说，好说", 6)
    }
}