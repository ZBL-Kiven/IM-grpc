package com.zj.imtest.ui.base

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.bean.ChannelRegisterInfo
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.cf.fragments.BaseTabFragment
import com.zj.database.entity.MessageInfoEntity
import com.zj.imUi.base.BaseImItem
import com.zj.imtest.R
import com.zj.imtest.ui.data.MsgAdapter
import com.zj.loading.BaseLoadingView
import com.zj.loading.DisplayMode

abstract class BaseMessageFragment : BaseTabFragment() {

    private lateinit var sessionKey: String
    private var rvContent: RecyclerView? = null
    private var blv: BaseLoadingView? = null
    private var adapter: MsgAdapter? = null
    private lateinit var msgReqInfo: ChannelRegisterInfo

    abstract fun createData(groupId: Long, ownerId: Int, targetUserId: Int): ChannelRegisterInfo

    open fun initMessageObservers(sessionKey: String) {}

    open fun getMessageFilter(data: MessageInfoEntity?, payload: String?): Boolean {
        return data?.channelKey == sessionKey
    }

    open fun getKeyboardScrollerView(): RecyclerView? {
        return rvContent
    }

    fun setData(groupId: Long, ownerId: Int, targetUserId: Int): BaseMessageFragment {
        msgReqInfo = createData(groupId, ownerId, targetUserId)
        return this
    }

    fun getData(): ChannelRegisterInfo {
        return msgReqInfo
    }

    override fun getView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    override fun onCreate() {
        super.onCreate()

        //        blv = rootView?.findViewById(R.id.im_reward_setting_loading)
        rootView?.findViewById<RecyclerView>(R.id.im_frag_msg_rv)?.let {
            adapter = MsgAdapter(it)
            it.adapter = adapter
            rvContent = it
        }
        blv?.setOnTapListener {
            activity?.finish()
        }
        initMessage()
    }

    private fun initMessage() {
        if (!::msgReqInfo.isInitialized) {
            blv?.setMode(DisplayMode.NO_NETWORK)
            return
        }
        IMHelper.registerChatRoom(msgReqInfo)?.let {
            sessionKey = it
        }
        if (::sessionKey.isInitialized) {
            initMessageObservers(sessionKey)
            initMessageObserver()
        }
    }

    private fun initMessageObserver() {
        val observer = msgReqInfo.setMessageReceiveObserver().filterIn(::getMessageFilter)
        observer.ignoreNullData = false
        observer.listen { d, list, pl ->
            if (d != null) {
                d.replyMsg?.let {
                    if (adapter?.isMsgReplying(it) == true) adapter?.setRewardViewState(it, false)
                }
                when (pl) {
                    ClientHubImpl.PAYLOAD_ADD, ClientHubImpl.PAYLOAD_CHANGED -> adapter?.update(d)
                    ClientHubImpl.PAYLOAD_CHANGED_SEND_STATE -> adapter?.update(d, BaseImItem.NOTIFY_CHANGE_SENDING_STATE)
                    ClientHubImpl.PAYLOAD_DELETE -> adapter?.removeIfEquals(d)
                    else -> adapter?.removeIfEquals(d)
                }
            }
            if (!list.isNullOrEmpty()) {
                adapter?.change(list)
            }
            blv?.setMode(DisplayMode.NORMAL)
        }
    }

    override fun onDestroyed() {
        super.onDestroyed()
        Log.e("------ ", "onFragment destroy ${System.currentTimeMillis()}")
    }
}