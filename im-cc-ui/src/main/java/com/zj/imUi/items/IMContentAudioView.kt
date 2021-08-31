package com.zj.imUi.items

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.zj.imUi.R
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imUi.widget.GroupMessageRecordItem

class IMContentAudioView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0) : GroupMessageRecordItem(context, attrs, def), ImContentIn {

    override fun onSetData(data: ImMsgIn?) {
        super.setData(data)
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