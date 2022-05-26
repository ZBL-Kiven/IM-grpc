package com.zj.ccIm

import android.os.Handler
import android.os.Looper
import com.zj.ccIm.core.Constance
import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.ImConfigIn
import com.zj.ccIm.core.bean.MessageTotalDots
import com.zj.ccIm.core.impl.ClientHubImpl
import com.zj.ccIm.core.impl.ServerHubImpl
import com.zj.ccIm.error.AuthenticationError
import com.zj.ccIm.error.ConnectionError
import com.zj.ccIm.live.impl.LiveClientHubImpl
import com.zj.ccIm.live.impl.LiveServerHubImpl
import com.zj.database.entity.PrivateOwnerEntity
import com.zj.database.entity.SessionInfoEntity
import com.zj.im.chat.core.BaseOption
import com.zj.im.chat.exceptions.IMException
import com.zj.im.chat.hub.ClientHub
import com.zj.im.chat.hub.ServerHub
import com.zj.im.chat.interfaces.MessageInterface
import com.zj.im.chat.poster.UIHelperCreator
import com.zj.im.main.impl.IMInterface


@Suppress("MemberVisibilityCanBePrivate", "unused")
internal object CcIM : IMInterface<Any?>() {

    fun init(config: ImConfigIn, option: BaseOption) {
        this.imConfig = config
        super.initChat(option)
    }

    var imConfig: ImConfigIn? = null
        get() {
            return if (field == null) {
                recordLogs("CcIM", "get IM Config must not be null!", true);null
            } else field
        }

    override fun getClient(): ClientHub<Any?> {
        return if (imConfig?.useLive() == true) LiveClientHubImpl() else ClientHubImpl()
    }

    override fun getServer(): ServerHub<Any?> {
        return if (imConfig?.useLive() == true) LiveServerHubImpl() else ServerHubImpl()
    }

    override fun onError(e: IMException) {
        if (e !is ConnectionError) {
            imConfig?.onAlertError(e)
        }
    }

    override fun onSdkDeadlyError(e: IMException) {
        if (e is AuthenticationError) {
            imConfig?.onAuthenticationError()
        } else imConfig?.onSdkDeadlyError(e)
    }

    override fun onNewListenerRegistered(cls: UIHelperCreator<*, *, *>, withData: Any?) {
        when (cls) {
            SessionInfoEntity::class.java -> {
                routeToClient(null, Constance.CALL_ID_START_LISTEN_SESSION)
            }
            MessageTotalDots::class.java -> {
                routeToClient(null, Constance.CALL_ID_START_LISTEN_TOTAL_DOTS)
            }
            PrivateOwnerEntity::class.java -> {
                routeToClient(null, Constance.CALL_ID_START_LISTEN_PRIVATE_OWNER_CHAT)
            }
        }
    }

    fun getImInterface(): MessageInterface<*> {
        return this
    }

    fun <T : Any> postToUiObservers(data: T, payload: String? = null, onFinish: (() -> Unit)? = null) {
        val cls = if (data is Collection<*>) {
            throw NullPointerException("To send an array, you need to specify the Class type, and call [postToUiObservers(cls: Class<>?,...)] to handle this problem.")
        } else {
            data::class.java
        }
        postToUiObservers(cls, data, payload, onFinish)
    }

    fun <T : Any> postToUiObservers(cls: Class<*>?, data: T?, payload: String? = null, onFinish: (() -> Unit)? = null) {
        super.postToUi(cls, data, payload, onFinish ?: {})
    }

    override fun shutdown(case: String) {
        IMHelper.close()
        super.shutdown(case)
    }
}

internal val MainLooper = Handler(Looper.getMainLooper())