package com.zj.imtest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.lang.StringBuilder
import java.util.*
import com.zj.album.AlbumIns
import com.zj.album.nModule.FileInfo
import com.zj.album.nutils.MimeType
import com.zj.album.options.AlbumOptions
import com.zj.album.ui.preview.images.transformer.TransitionEffect
import com.zj.album.ui.views.image.easing.ScaleEffect
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.sender.Sender
import com.zj.database.DbHelper
import com.zj.database.entity.MessageInfoEntity
import com.zj.database.entity.SessionInfoEntity
import com.zj.im.chat.poster.DataHandler


@Suppress("UNUSED_PARAMETER")
class MainActivity : AppCompatActivity() {


    private lateinit var text: TextView
    private lateinit var et: EditText
    private val userId = IMConfig.getUserId()
    private val groupId = 6L
    private var lastSelectData: FileInfo? = null
        set(value) {
            et.setText(value?.path ?: "")
            field = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text = findViewById(R.id.main_text)
        et = findViewById(R.id.main_edit)

        /**====================================================== READ ME ⬇️️ ===========================================================*/

        //初始化 IM 聊天 模块，（异步完成）
        IMHelper.init(this.application, IMConfig)

        /**
         * 注册一个消息监听器，其中 [MessageInfoEntity]  表示一个类的描述，当下面这个监听器回调时，其类型必然是 [MessageInfoEntity] 。
         *
         * uniqueCode(0x1122) 代表一个监听器的标识，此标识尽量不要重名，以免影响问题快速排查和全日志系统的分析结果.
         *
         * d : 单条消息的数据实例，此时应该对消息列表做处理，处理方式参照 pl 。
         *
         * list : 当整体列表数据初始化或需要重新加载时，d 为空，list 不为空。
         *
         * s : 即 Payload， 此值在 List 返回时无意义。其他情况 ：[add,change,delete] / 特殊结构体情况下其值默认为 CallId 。
         *
         * */
        IMHelper.addReceiveObserver<MessageInfoEntity>(0x1122).listen { d, list, pl ->
            text.append("\non message ==> d = ${d?.textContent?.text}   lstD = $list  s = $pl")
        }

        /**
         * 同上，此处附加展示了监听器的新功能。 即消息过滤 ，
         * 如下例子所示，filterIn 返回值代表 '非我本人是群组' ，
         * 所以此监听器所能收到的消息为 ：「 类型为 [SessionInfoEntity] 且 非我本人是群组 的所有消息 」
         * s ： payload
         * */
        IMHelper.addReceiveObserver<SessionInfoEntity>(0x1124).filterIn { i, _ -> i.ownerId != userId }.listen { d, list, pl ->
            text.append("\non sessions got ==> d = ${d?.groupId}   lstD = $list  s = $pl")
        }

        /**
         * 同上，此处采用的是 addTransferObserver 。则需要为其指定 DataHandler 的转换规则，并可分别为转换前后的数据设置过滤
         * */
        IMHelper.addTransferObserver<String, MessageInfoEntity>(0x1125).addHandler(MsgDataHandler::class.java).filterIn { string, _ -> string.isNotEmpty() }.filterOut { messageInfo, _ -> messageInfo.msgType == "text" }.listen { d, list, pl ->
            text.append("\non sessions got ==> d = ${d?.groupId}   lstD = $list  s = $pl")
        }
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
        text.text = ""
    }

    class MsgDataHandler : DataHandler<String, MessageInfoEntity> {
        override fun handle(data: String): MessageInfoEntity {
            return MessageInfoEntity()
        }
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
}