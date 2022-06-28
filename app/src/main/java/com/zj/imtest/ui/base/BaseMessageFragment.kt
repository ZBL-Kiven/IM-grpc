package com.zj.imtest.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.MsgType
import com.zj.ccIm.core.bean.ChannelRegisterInfo
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.cf.fragments.BaseTabFragment
import com.zj.database.entity.MessageInfoEntity
import com.zj.im.chat.enums.SendMsgState
import com.zj.imUi.base.BaseImItem
import com.zj.imtest.R
import com.zj.imtest.ui.data.MsgAdapter
import com.zj.imtest.ui.data.MsgInfoTransfer
import com.zj.loading.BaseLoadingView
import com.zj.loading.DisplayMode
import com.zj.views.list.refresh.layout.RefreshLayout

abstract class BaseMessageFragment : BaseTabFragment() {

    private lateinit var sessionKey: String
    private var rvContent: RecyclerView? = null
    private var blv: BaseLoadingView? = null
    private var rlv: RefreshLayout? = null
    private var adapter: MsgAdapter? = null
    private lateinit var msgReqInfo: ChannelRegisterInfo

    abstract fun createData(groupId: Long, ownerId: Int, targetUserId: Int): ChannelRegisterInfo

    open fun initMessageObservers(sessionKey: String) {}

    open fun onMessage(msg: MessageInfoEntity) {}

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

        //  blv = rootView?.findViewById(R.id.im_reward_setting_loading)
        rlv = rootView?.findViewById(R.id.im_frag_msg_rl)
        rootView?.findViewById<RecyclerView>(R.id.im_frag_msg_rv)?.let {
            adapter = MsgAdapter(it)
            it.adapter = adapter
            rvContent = it
        }
        blv?.setOnTapListener {
            activity?.finish()
        }
        rlv?.setOnRefreshListener {
            getHistoryMessage()
        }
    }

    override fun onResumed() {
        super.onResumed()
        initMessage()
    }

    override fun onPaused() {
        super.onPaused()
        if (::sessionKey.isInitialized) {
            IMHelper.leaveChatRoom(sessionKey)
        }
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
        val observer = msgReqInfo.setMessageReceiveObserver(MsgInfoTransfer::class.java).filterIn(::getMessageFilter)
        observer.ignoreNullData(false)
        observer.listen { d, list, pl ->
            if (d != null) {
                d.replyMsg?.let {
                    if (adapter?.isMsgReplying(it) == true) adapter?.setRewardViewState(it, false)
                }
                when (pl) {
                    ClientHubImpl.PAYLOAD_ADD -> if (d.sendingState == SendMsgState.SENDING.type && d.msgType == MsgType.GIFT.type) return@listen else adapter?.update(d)
                    ClientHubImpl.PAYLOAD_CHANGED -> adapter?.update(d)
                    ClientHubImpl.PAYLOAD_CHANGED_SEND_STATE -> {
                        if (d.msgType == MsgType.GIFT.type) {
                            if (d.sendingState == SendMsgState.SUCCESS.type || d.sendingState == SendMsgState.NONE.type) adapter?.update(d)
                        } else {
                            adapter?.update(d, BaseImItem.NOTIFY_CHANGE_SENDING_STATE)
                        }
                    }
                    ClientHubImpl.PAYLOAD_DELETE -> adapter?.removeIfEquals(d)
                    ClientHubImpl.PAYLOAD_REFUSE_FROM_SENSITIVE_WORDS -> {
                        Toast.makeText(context, "你不对劲！", Toast.LENGTH_SHORT).show()
                        adapter?.update(d, BaseImItem.NOTIFY_CHANGE_SENDING_STATE)
                    }
                    ClientHubImpl.PAYLOAD_REFUSE_FROM_SENSITIVE_WORDS_OTHER -> {
                        Toast.makeText(context, "重复发言！", Toast.LENGTH_SHORT).show()
                        adapter?.update(d, BaseImItem.NOTIFY_CHANGE_SENDING_STATE)
                    }
                    else -> adapter?.removeIfEquals(d)
                }
                onMessage(d)
            }
            if (!list.isNullOrEmpty()) {
                adapter?.change(list)
            }
            blv?.setMode(DisplayMode.NORMAL)
        }
    }

    private fun getHistoryMessage() {
        val firstMsgId = (adapter?.data?.firstOrNull()?.msgId) ?: return
        val req = msgReqInfo.toReqBody(firstMsgId, 1)
        IMHelper.getChatMsg(req) {
            try {
                if (it.isOK && !it.data.isNullOrEmpty()) {
                    val data = it.data?.filterNotNull() ?: return@getChatMsg
                    adapter?.add(data, 0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                rlv?.finishRefresh()
            }
        }
    }
}