package com.zj.ccIm.core.bean

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.zj.ccIm.CcIM
import com.zj.ccIm.annos.MsgFetchType
import com.zj.ccIm.core.IMChannelManager
import com.zj.ccIm.core.catching
import com.zj.ccIm.core.fecher.FetchMsgChannel
import com.zj.database.entity.MessageInfoEntity
import com.zj.im.chat.poster.UIHelperCreator
import java.lang.IllegalArgumentException

@Suppress("unused")
data class ChannelRegisterInfo internal constructor(internal val lo: LifecycleOwner? = null, val groupId: Long, val ownerId: Int, val targetUserid: Int?, val msgId: Long?, @MsgFetchType val type: Int? = null, internal var mChannel: FetchMsgChannel) : LifecycleObserver {

    internal constructor(lo: LifecycleOwner? = null, groupId: Long, ownerId: Int, targetUserid: Int?, channel: FetchMsgChannel) : this(lo, groupId, ownerId, targetUserid, 0, null, channel)

    internal constructor(lo: LifecycleOwner? = null, groupId: Long, ownerId: Int, targetUserid: Int?, channel: String) : this(lo, groupId, ownerId, targetUserid, 0, null, FetchMsgChannel.valueOf(channel.uppercase()))

    var key = ""

    val curChannelName: String; get() = mChannel.serializeName

    val classification: Int; get() = mChannel.classification

    init {
        lo?.lifecycle?.addObserver(this)
        key = createKey(mChannel.serializeName, groupId, ownerId.toLong(), targetUserid?.toLong())
    }

    private var lifecycleState: Lifecycle.Event = Lifecycle.Event.ON_RESUME

    fun setMessageReceiveObserver(): UIHelperCreator<MessageInfoEntity, MessageInfoEntity, *> {
        return CcIM.addReceiveObserver(MessageInfoEntity::class.java, key, lo)
    }

    internal fun checkValid(): Boolean {
        val errorMsg = "your call register with channel ${mChannel.serializeName} , but %s is invalid"
        return catching({
            if (mChannel.classification == 0 && groupId < 0) throw IllegalArgumentException(String.format(errorMsg, "groupId"))
            if (mChannel.classification == 1 && ownerId < 0) throw IllegalArgumentException(String.format(errorMsg, "ownerId"))
            if (mChannel.classification == 2 && (targetUserid == null || targetUserid < 0)) throw IllegalArgumentException(String.format(errorMsg, "targetUserId"))
            true
        }, { false }) ?: false
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_RESUME)
    private fun resume() {
        IMChannelManager.resumeRegisterInfo(this)
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_PAUSE)
    private fun pause() {
        IMChannelManager.pauseRegisterInfo(this)
    }

    internal fun onResumed() {
        lifecycleState = Lifecycle.Event.ON_RESUME
    }

    internal fun onPaused() {
        lifecycleState = Lifecycle.Event.ON_PAUSE
    }

    internal fun onDestroy() {
        lifecycleState = Lifecycle.Event.ON_DESTROY
    }

    companion object {

        internal fun createKey(serializeName: String, groupId: Long?, ownerId: Long?, targetUserid: Long?): String {
            return "${serializeName}|g:${groupId}|o:${ownerId}|u:${targetUserid}"
        }

        fun buildWithOwnerMessage(lo: LifecycleOwner, groupId: Long): ChannelRegisterInfo {
            return ChannelRegisterInfo(lo, groupId, -1, -1, FetchMsgChannel.OWNER_MESSAGE)
        }

        fun buildWithOwnerClapHouse(lo: LifecycleOwner, groupId: Long): ChannelRegisterInfo {
            return ChannelRegisterInfo(lo, groupId, -1, -1, FetchMsgChannel.OWNER_CLAP_HOUSE)
        }

        fun buildWithFansMessage(lo: LifecycleOwner, groupId: Long): ChannelRegisterInfo {
            return ChannelRegisterInfo(lo, groupId, -1, -1, FetchMsgChannel.FANS_MESSAGE)
        }

        fun buildWithFansClapHouse(lo: LifecycleOwner, groupId: Long): ChannelRegisterInfo {
            return ChannelRegisterInfo(lo, groupId, -1, -1, FetchMsgChannel.FANS_CLAP_HOUSE)
        }

        fun buildWithOwnerPrivateChat(lo: LifecycleOwner, groupId: Long, ownerId: Int): ChannelRegisterInfo {
            return ChannelRegisterInfo(lo, groupId, ownerId, -1, FetchMsgChannel.FANS_PRIVATE)
        }

        fun buildWithFansPrivateChat(lo: LifecycleOwner, groupId: Long, targetUserId: Int): ChannelRegisterInfo {
            return ChannelRegisterInfo(lo, groupId, -1, targetUserId, FetchMsgChannel.OWNER_PRIVATE)
        }
    }
}
