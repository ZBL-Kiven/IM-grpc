package com.zj.imtest.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.bean.MessageTotalDots
import com.zj.cf.managers.TabFragmentManager
import com.zj.database.entity.MessageInfoEntity
import com.zj.emotionbar.adapt2cc.CCEmojiLayout
import com.zj.imtest.R
import com.zj.imtest.ui.base.BaseMessageFragment
import com.zj.imtest.ui.input.InputDelegate


@Suppress("UNUSED_PARAMETER")
class MainActivity : AppCompatActivity() {

    private lateinit var fragmentManager: TabFragmentManager<Long, BaseMessageFragment>

    private val groupId = 32L
    private val ownerId = 151473
    private val targetUserId = 151473
    private var tvConn: TextView? = null
    private var tvDotsInfo: TextView? = null
    private var inputLayout: CCEmojiLayout<MessageInfoEntity>? = null
    private var inputDelegate: InputDelegate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val container = findViewById<ViewPager2>(R.id.main_fg_container)
        val tab = findViewById<TabLayout>(R.id.main_fg_message_tab)
        tvConn = findViewById(R.id.main_tv_conn)
        tvDotsInfo = findViewById(R.id.main_tv_dots_info)
        inputLayout = findViewById(R.id.main_input_layout)
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
            tvConn?.text = it.name
        }
        IMHelper.addReceiveObserver<MessageTotalDots>(this::class.java.simpleName, this).listen { d, _, p ->
            Log.e("------- ", "msg total dots changed by : $p")
            if (d != null) {
                val s = "msg total dots: total = ${d.dotsOfAll.unreadMessages} clapHouse = ${d.clapHouseDots.unreadMessages}  owner = ${d.privateOwnerDots.unreadMessages}  fans = ${d.privateFansDots.unreadMessages}"
                tvDotsInfo?.text = s
            }
        }
    }

    fun queryCurrent(view: View) {
        val sb = IMHelper.queryAllDBColumnsCount()
        Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show()
    }
}