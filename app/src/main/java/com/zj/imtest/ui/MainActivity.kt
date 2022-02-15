package com.zj.imtest.ui

import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.bean.MessageTotalDots
import com.zj.ccIm.core.sender.exchange.MessageInfoEntityDataExchange
import com.zj.cf.managers.TabFragmentManager
import com.zj.database.entity.MessageInfoEntity
import com.zj.database.entity.SessionInfoEntity
import com.zj.emotionbar.adapt2cc.CCEmojiLayout
import com.zj.emotionbar.data.Emoticon
import com.zj.emotionbar.data.EmoticonPack
import com.zj.emotionbar.epack.emoticon.EmoticonEntityUtils
import com.zj.im.chat.enums.ConnectionState
import com.zj.imtest.BaseApp
import com.zj.imtest.R
import com.zj.imtest.ui.base.BaseMessageFragment
import com.zj.imtest.ui.input.InputDelegate


@Suppress("UNUSED_PARAMETER")
class MainActivity : AppCompatActivity() {

    private lateinit var fragmentManager: TabFragmentManager<Long, BaseMessageFragment>

    private val groupId = 32L
    private val ownerId = 151120
    private val targetUserId = 151254 //151473
    private var tvConn: View? = null
    private var tvName: TextView? = null
    private var tvGroupInfo: TextView? = null
    private var tvGroupDesc: TextView? = null
    private var ivHeadPic: ImageView? = null
    private var inputLayout: CCEmojiLayout<MessageInfoEntity, Emoticon>? = null
    private var inputDelegate: InputDelegate? = null
    private var groupInfoDesc = ""
        set(value) {
            field = value;updateText()
        }
    private var badgeText = ""
        set(value) {
            field = value;updateText()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val container = findViewById<ViewPager2>(R.id.main_fg_container)
        val tab = findViewById<TabLayout>(R.id.main_fg_message_tab)
        tvConn = findViewById(R.id.main_tv_conn)
        tvName = findViewById(R.id.main_tv_name)
        ivHeadPic = findViewById(R.id.main_iv_head_pic)
        tvGroupInfo = findViewById(R.id.main_tv_group_info)
        inputLayout = findViewById(R.id.main_input_layout)
        tvGroupDesc = findViewById(R.id.main_tv_group_desc)
        inputDelegate = InputDelegate(inputLayout, groupId)
        inputLayout?.setOnFuncListener(inputDelegate)
        initConnectObserver()
        val f1 = GroupFragment().setData(groupId, ownerId, targetUserId)
        val f2 = MessageFragment().setData(groupId, ownerId, targetUserId)
        val fragments = arrayOf(f1, f2)
        fragmentManager = object : TabFragmentManager<Long, BaseMessageFragment>(this, container, 0, tab, groupId, groupId) {

            override fun onCreateFragment(d: Long, p: Int): BaseMessageFragment {
                return fragments[p]
            }

            override fun tabConfigurationStrategy(tab: TabLayout.Tab, position: Int) {
                tab.text = fragments[position].getData().curChannelName
            }

            override fun syncSelectState(selectId: String) {
                super.syncSelectState(selectId)
                val frg = getFragmentById(selectId)
                inputLayout?.setScrollerView(frg?.getKeyboardScrollerView())
                inputDelegate?.updateCurChannel(frg?.getData())
            }
        }
        initListener()
    }

    private fun initListener() {

        ivHeadPic?.setOnClickListener {
            IMHelper.CustomSender.ignoreConnectionStateCheck(true).ignoreSendConditionCheck(true).sendWithoutState().setCustomSendCallback(object : MessageInfoEntityDataExchange() {
                override fun onSendingStart(callId: String, d: MessageInfoEntity?) {
                    Log.e("=======>", "CustomSendingCallback: onStart $callId   d = ${d?.textContent?.text} , thread = ${Thread.currentThread().name} ,main = ${Looper.getMainLooper().thread.name}")
                }

                override fun onSendingProgress(callId: String, progress: Int) {
                    Log.e("=======>", "CustomSendingCallback: progress $callId , thread = ${Thread.currentThread().name} ,main = ${Looper.getMainLooper().thread.name}")
                }

                override fun onSendResult(isOK: Boolean, retryAble: Boolean, callId: String, d: MessageInfoEntity?, throwable: Throwable?, payloadInfo: Any?) {
                    Log.e("=======>", "CustomSendingCallback: onResult $callId  isOk = $isOK   d = ${d?.textContent?.text}   ext = ${d?.extContent.toString()} , thread = ${Thread.currentThread().name} ,main = ${Looper.getMainLooper().thread.name}")
                }
            }).build().sendText("bitch", groupId)
        }
    }

    private fun initConnectObserver() {
        IMHelper.getIMInterface().registerConnectionStateChangeListener(this::class.java.simpleName) {
            if (isFinishing) return@registerConnectionStateChangeListener
            when (it) {
                is ConnectionState.CONNECTED, is ConnectionState.PING, is ConnectionState.PONG -> tvConn?.setBackgroundResource(R.drawable.dots_green)
                is ConnectionState.OFFLINE, is ConnectionState.ERROR, is ConnectionState.INIT -> tvConn?.setBackgroundResource(R.drawable.dots_red)

                is ConnectionState.CONNECTION, is ConnectionState.RECONNECT -> tvConn?.setBackgroundResource(R.drawable.dots_yellow)
            }
        }
        IMHelper.addReceiveObserver<MessageTotalDots>(0x1100, this).listen { r, _, _ ->
            if (r == null) return@listen
            badgeText = "total = ${r.dotsOfAll.unreadMessages} clapHouse = ${r.clapHouseDots.unreadMessages}  owner = ${r.privateOwnerDots.unreadMessages}  fans = ${r.privateFansDots.unreadMessages}"
        }
        IMHelper.addReceiveObserver<SessionInfoEntity>(0x1101, this).filterIn { d, _ ->
            return@filterIn d.groupId == groupId
        }.listen { d, lst, _ ->
            var data: SessionInfoEntity? = d
            if (data == null && lst != null) {
                data = lst.firstOrNull()
            }
            if (data != null && !isFinishing) {
                val info = "( ${if (data.role == 1) "Owner" else if (data.role == 2) "Manager" else "Member"} | ${data.groupName} )"
                tvName?.text = BaseApp.config.getUserName()
                tvGroupInfo?.text = info
                val groupState = when (data.groupStatus) {
                    1 -> "Stopped"
                    3 -> "NotFollow"
                    else -> "Normal"
                }
                groupInfoDesc = "ownerId = ${data.ownerId} , myUid = ${BaseApp.config.getUserId()} , status = $groupState"
                ivHeadPic?.let { Glide.with(this).load(data.logo).circleCrop().into(it) }
            }
        }
        val packs = mutableListOf<EmoticonPack<Emoticon>>()
        packs.add(getEmoji(applicationContext, -1))
        packs.add(getEmoji(applicationContext, 1))
        packs.add(getEmoji(applicationContext, 3))
        inputLayout?.setEmoticon(packs)
    }

    private fun getEmoji(context: Context, id: Int): EmoticonPack<Emoticon> {
        val pack = EmoticonPack<Emoticon>()
        pack.id = id
        val emojiArray = mutableListOf<EmoticonEntityUtils.BigEmoticon>()

        for (i in 1..5) {
            emojiArray.add(EmoticonEntityUtils.BigEmoticon().apply {
                this.id = 34
                this.url = "https://pic1.zhimg.com/v2-d58ce10bf4e01f5086c604a9cfed29f3_r.jpg?source=1940ef5c"
                this.icon = "https://pic1.zhimg.com/v2-d58ce10bf4e01f5086c604a9cfed29f3_r.jpg?source=1940ef5c"
                this.pack = EmoticonPack<Emoticon>().apply { this.id = id }

            })
        }

        pack.emoticons = emojiArray.toMutableList()
        pack.status = EmoticonPack.EmoticonStatus.NORMAL
        pack.image = "https://th.bing.com/th/id/R.b93ec31aae5b4493f23f3635fa242bd5?rik=meQoAkd%2b3Sp1YA&riu=http%3a%2f%2f24.media.tumblr.com%2f1f2e6459f5c70b27e9f6aedafa48b643%2ftumblr_mzu7u7Dimp1siovl5o1_400.gif&ehk=59Tf%2bNhxvDrJYXI45sq19Ahlws9ljUK2qqE3LQQnYcQ%3d&risl=&pid=ImgRaw&r=0"
        if (id == 3) {
            pack.type = EmoticonPack.EmoticonType.PAY.type
        }
        return pack
    }

    private fun updateText() {
        val s = "$groupInfoDesc\n$badgeText"
        tvGroupDesc?.text = s
    }
}