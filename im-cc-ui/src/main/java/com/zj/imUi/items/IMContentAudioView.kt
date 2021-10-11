package com.zj.imUi.items

import android.content.Context
import android.util.AttributeSet
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imUi.widget.GroupMessageRecordItem

class IMContentAudioView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0) : GroupMessageRecordItem(context, attrs, def), ImContentIn {

    private var isGroupChat:Boolean = true

    override fun onSetData(data: ImMsgIn?) {
        super.setData(data,isGroupChat)
    }

    override fun isGroupChat(isGroupChat: Boolean) {
        this.isGroupChat = isGroupChat
    }

    override fun onResume(data: ImMsgIn?) {
        if (data?.isAudioPlaying() == true) {
            startAnim()
        } else {
            stopAnim()
        }
    }

    override fun onStop(data: ImMsgIn?) {
        stopAnim()
    }

    override fun onDestroy(data: ImMsgIn?) {
        stopAnim()
    }
}