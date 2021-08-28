package com.zj.imtest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.lang.StringBuilder
import java.util.*
import com.zj.album.AlbumIns
import com.zj.album.nModule.FileInfo
import com.zj.album.nutils.MimeType
import com.zj.album.options.AlbumOptions
import com.zj.album.ui.preview.images.transformer.TransitionEffect
import com.zj.album.ui.views.image.easing.ScaleEffect
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.bean.MessageTotalDots
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.ccIm.core.sender.Sender
import com.zj.database.DbHelper
import com.zj.database.entity.MessageInfoEntity
import com.zj.imtest.ui.MsgAdapter


@Suppress("UNUSED_PARAMETER")
class MainActivity : AppCompatActivity() {


    private lateinit var rv: RecyclerView
    private lateinit var et: EditText
    private var adapter: MsgAdapter? = null
    private val groupId = 6L
    private var lastSelectData: FileInfo? = null
        set(value) {
            et.setText(value?.path ?: "")
            field = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rv = findViewById(R.id.main_erv)
        et = findViewById(R.id.main_edit)
        adapter = MsgAdapter(this)
        rv.adapter = adapter
        initIm()
    }

    fun registerMsg(view: View) {
        /**
         * 进入聊天页面，调用此接口后，即时聊天消息接收开始工作
         * */
        IMHelper.registerChatRoom(groupId, 117656)
    }

    fun leaveChatRoom(view: View) {
        /**
         * 离开聊天页面，调用此接口后，即时聊天消息接收停止工作
         * */
        IMHelper.leaveChatRoom(groupId)
    }

    fun sendText(view: View) {
        /**
         *  调用以发送一条消息，多类型的消息可参考 [Sender] ,
         *  发送消息时 callId 会被默认指定为 UUID (不传入任何值的情况下)。
         *  为保证消息回流得到认证，此值尽量保持唯一。
         * */
        Sender.sendText("好说，好说", groupId, null)
    }

    fun sendImg(view: View) {
        lastSelectData?.let {
            val path = it.path
            val duration = it.duration
            if (lastSelectData?.isImage == true) Sender.sendImg(path, 200, 200, groupId)
            if (lastSelectData?.isVideo == true) Sender.sendVideo(path, 200, 200, duration, groupId)
        }
    }

    /**====================================================== READ ME ⬆️ ===========================================================*/

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
        adapter?.clear()
    }

    /*================= file test ===================*/
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        start()
    }

    private fun start() {
        AlbumIns.with(this).setOriginalPolymorphism(true).simultaneousSelection(true).maxSelectedCount(9).mutableTypeSize().addNewRule("Image", 3, AlbumOptions.ofStaticImage()).addNewRule("Gif", 1, EnumSet.of(MimeType.GIF)).addNewRule("Video", 2, AlbumOptions.ofVideo()).set().mimeTypes(AlbumOptions.pairOf(AlbumOptions.ofImage(), AlbumOptions.ofVideo())).sortWithDesc(true).useOriginDefault(false).imgSizeRange(1, 20000000).videoSizeRange(1, 200000000).imageScaleEffect(ScaleEffect.QUAD).pagerTransitionEffect(TransitionEffect.Zoom).start { _, data ->
            lastSelectData = data?.firstOrNull()
        }
    }

    fun startAlbum(@Suppress("UNUSED_PARAMETER") v: View?) {
        val i = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (i != PackageManager.PERMISSION_GRANTED) {
            start()
        } else ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
    }

    private fun initIm() {

        //初始化 IM 聊天 模块，（异步完成）
        IMHelper.init(this.application, IMConfig)

        IMHelper.addReceiveObserver<MessageInfoEntity>(0x1124).listen { d, list, pl ->
            if (d != null) when (pl) {
                ClientHubImpl.PAYLOAD_ADD -> {
                    adapter?.let {
                        it.add(d);rv.scrollToPosition(it.maxPosition)
                    }
                }
                ClientHubImpl.PAYLOAD_CHANGED -> adapter?.update(d)
                ClientHubImpl.PAYLOAD_DELETE -> adapter?.removeAll(d)
            }
            if (!list.isNullOrEmpty()) adapter?.change(list)
        }

        IMHelper.addReceiveObserver<MessageTotalDots>(0x1125).listen { r, _, _ ->
            Log.e("----- ", "on all unread count changed , cur is ${r?.dots}")
        }
    }
}