package com.zj.imtest.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.zj.ccIm.core.IMHelper
import com.zj.cf.managers.TabFragmentManager
import com.zj.database.entity.MessageInfoEntity
import com.zj.database.entity.SessionInfoEntity
import com.zj.emotionbar.adapt2cc.CCEmojiLayout
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
    private val targetUserId = 151473
    private var tvConn: View? = null
    private var tvName: TextView? = null
    private var tvGroupInfo: TextView? = null
    private var tvGroupDesc: TextView? = null
    private var ivHeadPic: ImageView? = null
    private var inputLayout: CCEmojiLayout<MessageInfoEntity>? = null
    private var inputDelegate: InputDelegate? = null
    private var groupInfoDesc = ""
    private var badgeText = ""

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
        val f1 = MessageFragment().setData(groupId, ownerId, targetUserId)
        val f2 = GroupFragment().setData(groupId, ownerId, targetUserId)
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
    }

    private fun initConnectObserver() {
        IMHelper.getIMInterface().registerConnectionStateChangeListener(this::class.java.simpleName) {
            if (isFinishing) return@registerConnectionStateChangeListener
            when (it) {
                ConnectionState.CONNECTED, ConnectionState.PING, ConnectionState.PONG -> tvConn?.setBackgroundResource(R.drawable.dots_green)
                ConnectionState.NETWORK_STATE_CHANGE, ConnectionState.CONNECTED_ERROR, ConnectionState.INIT -> tvConn?.setBackgroundResource(R.drawable.dots_red)
                ConnectionState.CONNECTION, ConnectionState.RECONNECT -> tvConn?.setBackgroundResource(R.drawable.dots_yellow)
            }
        }
        IMHelper.addReceiveObserver<SessionInfoEntity>(this::class.java.simpleName, this).filterIn { d, _ ->
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
                val desc = "ownerId = ${data.ownerId} , myUid = ${BaseApp.config.getUserId()} , status = $groupState"
                ivHeadPic?.let { Glide.with(this).load(data.logo).circleCrop().into(it) }
            }
        }
    }
}