package com.zj.im.chat.core

import android.app.Application
import android.app.Notification
import com.zj.im.chat.enums.RuntimeEfficiency

/**
 * Created by ZJJ
 *
 * the IM SDK options ,used for #ChatBase
 *
 * @see OptionProxy
 *
 * @param notification start a foreground service to keeping active for sdk when the app was running in background
 *
 * @param sessionId the foreground service session id
 *
 * @param debugEnable is allow logging at runtime?
 *
 * @param runtimeEfficiency the sdk efficiency level for sdk , {@link RuntimeEfficiency#interval }
 *
 * @param logsCollectionAble set is need collection runtime logs , {@link logsFileName} and saved in somewhere
 */

class BaseOption internal constructor(val context: Application, val notification: Notification? = null, val sessionId: Int?, val runtimeEfficiency: RuntimeEfficiency, val logsCollectionAble: () -> Boolean, val logsFileName: String, val logsMaxRetain: Long, val debugEnable: Boolean) {

    companion object {
        @Suppress("unused")
        fun create(context: Application): OptionProxy {
            return OptionProxy(context)
        }
    }
}
