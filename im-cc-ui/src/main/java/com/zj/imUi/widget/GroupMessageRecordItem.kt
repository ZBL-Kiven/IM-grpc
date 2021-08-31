package com.zj.imUi.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.zj.imUi.Constance
import com.zj.imUi.R
import com.zj.imUi.interfaces.ImMsgIn


class GroupMessageRecordItem @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attributeSet, defStyle) {

    private val audioTime: AppCompatTextView
    private val audioPlayView: VoicePlayView

    init {
        LayoutInflater.from(context).inflate(R.layout.im_msg_item_widget_record, this, true)
        audioTime = findViewById(R.id.im_msg_item_audio_time)
        audioPlayView = findViewById(R.id.im_msg_item_audio_play_view)
    }

    @SuppressLint("SetTextI18n")
    fun setData(messageBean: ImMsgIn?) {
        if (messageBean == null) return
        audioTime.text = StringBuilder(messageBean.getAudioContentDuration().toString()).append("''")
        if (!(messageBean.getSenderId() != messageBean.getOwnerId() || messageBean.getReplyMsgId() != 0L)) { //发送者为大V 且不是回复消息
            audioTime.setTextColor(ContextCompat.getColor(context, R.color.text_color_white))
        } else if (messageBean.getReplyMsgType() == Constance.MSG_TYPE_QUESTION && messageBean.getPublished()) {
            audioTime.setTextColor(ContextCompat.getColor(context, R.color.text_color_origin_private))
        } else if (messageBean.getReplyMsgType() == Constance.MSG_TYPE_QUESTION && !messageBean.getPublished()) {
            audioTime.setTextColor(ContextCompat.getColor(context, R.color.bg_purple))
        }
        audioTime.text = "${messageBean.getAudioContentDuration() ?: 0}\""
        audioPlayView.isAnim = messageBean.isAudioPlaying() == true
        setOnClickListener {
            if (!audioPlayView.isAnim) {
                messageBean.getMsgListener()?.playAudio()
            }
        }
    }

    fun startAnim() {
        audioPlayView.play()
    }

    fun stopAnim() {
        audioPlayView.stop()
    }
}