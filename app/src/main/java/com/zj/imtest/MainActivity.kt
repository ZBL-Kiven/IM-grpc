package com.zj.imtest

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import com.zj.album.AlbumIns
import com.zj.album.nModule.FileInfo
import com.zj.album.nutils.MimeType
import com.zj.album.options.AlbumOptions
import com.zj.album.ui.preview.images.transformer.TransitionEffect
import com.zj.album.ui.views.image.easing.ScaleEffect
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.bean.*
import com.zj.ccIm.core.fecher.FetchMsgChannel
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.ccIm.core.sender.MsgSender
import com.zj.ccIm.core.IMHelper.Sender
import com.zj.ccIm.live.LiveIMHelper
import com.zj.ccIm.live.LiveInfoEn
import com.zj.ccIm.live.LiveReqInfo
import com.zj.database.entity.MessageInfoEntity
import com.zj.database.entity.PrivateOwnerEntity
import com.zj.database.entity.SessionInfoEntity
import com.zj.imUi.base.BaseImItem
import com.zj.imtest.api.CCApi
import com.zj.imtest.ui.MsgAdapter


@Suppress("UNUSED_PARAMETER")
class MainActivity : AppCompatActivity() {

    companion object {
        var app: Application? = null
    }

    private var incId = 0
        get() {
            return field++
        }
    private lateinit var rv: RecyclerView
    private lateinit var et: TextView
    private lateinit var tv: TextView
    private var adapter: MsgAdapter? = null
    private val groupId = 100L
    private val ownerId = 151473
    private var curSpark = 0
    private var curDiamond = 100
    private var lastSelectData: FileInfo? = null
        set(value) {
            et.text = value?.path ?: ""
            field = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = this.application
        setContentView(R.layout.activity_main)
        rv = findViewById(R.id.main_erv)
        tv = findViewById(R.id.main_tv_conn)
        et = findViewById(R.id.main_edit)
        adapter = MsgAdapter(rv)
        rv.adapter = adapter
        initIm()
    }

    fun registerMsg(view: View) {
        /**
         * 进入聊天页面，调用此接口后，即时聊天消息接收开始工作
         * */
        IMHelper.registerChatRoom(groupId, ownerId, IMConfig.getUserId(), FetchMsgChannel.FANS_CLAP_HOUSE, FetchMsgChannel.FANS_MESSAGE)
    }

    fun leaveChatRoom(view: View) {
        /**
         * 离开聊天页面，调用此接口后，即时聊天消息接收停止工作
         * */

        // IMHelper.leaveChatRoom()

        CCApi.getTestApi().call({ it.setUserRelationshipFollow(IMConfig.getUserId(), IMConfig.getToken(), ownerId, 0) })
    }

    fun sendText(view: View) {
        /**
         *  调用以发送一条消息，多类型的消息可参考 [MsgSender] ,
         *  发送消息时 callId 会被默认指定为 UUID (不传入任何值的情况下)。
         *  为保证消息回流得到认证，此值尽量保持唯一。
         * */
        Sender.sendText("你已被油王服务 $incId 次", groupId)
    }

    fun sendImg(view: View) {
        lastSelectData?.let {
            val path = it.path
            val duration = it.duration
            if (lastSelectData?.isImage == true) Sender.sendImg(path, 200, 200, groupId)

            // IMHelper.CustomSender.ignoreConnectionStateCheck(true).ignoreSendConditionCheck(true).build().sendImg(path, 200, 200, groupId)

            if (lastSelectData?.isVideo == true) Sender.sendVideo(path, 200, 200, duration, groupId)
        }
    }

    fun sendUrlImg(view: View) {

        //        val url = "https://img1.baidu.com/it/u=744731442,3904757666&fm=26&fmt=auto&gp=0.jpg"
        //        Sender.sendUrlImg(url, 640, 426, groupId)
        //        IMHelper.refreshPrivateOwnerSessions(object : FetchResultRunner() {
        //            override fun result(result: FetchResult) {
        //                Log.e("------ ", "thread in : ${Thread.currentThread().name}   refreshPrivateOwnerSessions ====> ${result.success}")
        //            }
        //        })

        //        IMHelper.deleteSession(Comment.DELETE_OWNER_SESSION, groupId, ownerId, IMConfig.getUserId())

        //        IMHelper.CustomSender.ignoreConnectionStateCheck(true).ignoreSendConditionCheck(true).build().sendRewardTextMsg("小费", groupId, 50, MsgType.TEXT, true)

        LiveIMHelper.joinToLiveRoom(LiveReqInfo(4, 31, false, IMConfig.getUserId()))

    }

    /**====================================================== READ ME ⬆️ ===========================================================*/

    fun queryCurrent(view: View) {
        val sb = IMHelper.queryAllDBColumnsCount()
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
        val i = ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)
        val i1 = ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (i == PackageManager.PERMISSION_GRANTED && i1 == PackageManager.PERMISSION_GRANTED) {
            start()
        } else ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
    }

    private fun initIm() {

        //初始化 IM 聊天 模块，（异步完成）
        IMHelper.init(this.application, IMConfig)

        IMHelper.registerConnectionStateChangeListener(this::class.java.simpleName) {
            tv.text = it.name
        }

        IMHelper.addReceiveObserver<MessageInfoEntity>(0x1124).listen { d, list, pl ->
            if (d != null) when (pl) {
                ClientHubImpl.PAYLOAD_ADD, ClientHubImpl.PAYLOAD_CHANGED -> adapter?.update(d)
                ClientHubImpl.PAYLOAD_CHANGED_SEND_STATE -> adapter?.update(d, BaseImItem.NOTIFY_CHANGE_SENDING_STATE)
                ClientHubImpl.PAYLOAD_DELETE -> adapter?.removeIfEquals(d)
                else -> adapter?.removeIfEquals(d)
            }
            if (!list.isNullOrEmpty()) adapter?.change(list)
        }

        IMHelper.addReceiveObserver<AssetsChanged>(0x1132, this).listen { r, _, _ ->
            r?.diamondNum?.let {
                curDiamond += it
            }
            r?.spark?.let {
                curSpark += it
            }
            Log.e("----- ", "on assets changed, diamond = $curDiamond    spark = $curSpark")
        }

        IMHelper.addReceiveObserver<GetMoreMessagesInfo>(0x1131, this).listen { r, _, _ ->
            Log.e("----- ", "on more msg got, ${r?.data}")
        }

        IMHelper.addReceiveObserver<SessionInfoEntity>(0x1128, this).listen { r, l, pl ->
            Log.e("----- ", "on session got ,with last msg : ${r?.sessionMsgInfo?.newMsg?.textContent?.text ?: l?.firstOrNull()?.sessionMsgInfo?.newMsg?.textContent?.text} , payload = $pl")
        }

        IMHelper.addReceiveObserver<PrivateFansEn>(0x1129, this).listen { r, l, pl ->
            Log.e("----- ", "on private fans chat got ,with last msg : ${r?.lastMsgInfo?.newMsg?.textContent?.text ?: l?.firstOrNull()?.lastMsgInfo?.newMsg?.textContent?.text} , payload = $pl")
        }

        IMHelper.addReceiveObserver<PrivateOwnerEntity>(0x1130, this).listen { r, l, pl ->
            Log.e("----- ", "on  private owner chat got ,with last msg : ${r?.sessionMsgInfo?.newMsg?.textContent?.text ?: l?.firstOrNull()?.sessionMsgInfo?.newMsg?.textContent?.text} , payload = $pl")
        }

        IMHelper.addReceiveObserver<MessageTotalDots>(0x1125, this).listen { r, _, pl ->
            Log.e("----- ", "on all unread count changed , cur is ${r?.dots} , payload = $pl")
        }

        IMHelper.addReceiveObserver<FetchResult>(0x1127, this).listen { r, _, pl ->
            Log.e("----- ", "=============> success = ${r?.success}  isFirst =  ${r?.isFirstFetch}   nullData = ${r?.isNullData} , payload = $pl")
        }

        IMHelper.addReceiveObserver<LiveInfoEn>(0x1132).listen { r, _, pl ->
            Log.e("----- ", "on Live msg ${r?.content} , payload = $pl")
        }
    }
}