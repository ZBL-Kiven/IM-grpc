package com.zj.imUi.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.zj.imUi.R
import com.zj.imUi.UiMsgType
import com.zj.imUi.interfaces.ImMsgIn


class GroupMessageRecordItem @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attributeSet, defStyle) {

    private val audioTime: AppCompatTextView
    private val audioPlayView: VoicePlayView
    private val audioLinearLayout: LinearLayout

    init {
        LayoutInflater.from(context).inflate(R.layout.im_msg_item_widget_record, this, true)
        audioTime = findViewById(R.id.im_msg_item_audio_time)
        audioPlayView = findViewById(R.id.im_msg_item_audio_play_view)
        audioLinearLayout = findViewById(R.id.im_msg_item_widget_record_ll)
    }


    @SuppressLint("SetTextI18n")
    fun setData(messageBean: ImMsgIn?) {
        if (messageBean == null) return
        audioTime.text = StringBuilder(messageBean.getAudioContentDuration().toString()).append("''")

        if ( messageBean.getReplyMsgClientMsgId() == null) { //发送者为大V 且不是回复消息
            if (messageBean.getSenderId() == messageBean.getSenderId()) audioLinearLayout.setBackgroundResource(R.drawable.im_msg_item_audio_self_origin_cornors_bg)
            else audioLinearLayout.setBackgroundResource(R.drawable.im_msg_item_audio_origin_cornors_bg)
            audioTime.setTextColor(ContextCompat.getColor(context, R.color.text_color_white))
            audioPlayView.setPaintColor(ContextCompat.getColor(context,R.color.text_color_white))
        }
        if (messageBean.getReplyMsgType() == UiMsgType.MSG_TYPE_QUESTION ) {
            if (messageBean.getSenderId() == messageBean.getSelfUserId()){//大v自己发送打赏录音消息
                if (messageBean.getReplyMsgQuestionIsPublished() == false){
                    audioLinearLayout.setBackgroundResource(R.drawable.im_msg_item_audio_white_cornors_bg)
                    audioTime.setTextColor(ContextCompat.getColor(context, R.color.bg_purple))
                    audioPlayView.setPaintColor(ContextCompat.getColor(context, R.color.bg_purple))
                }else{
                    audioLinearLayout.setBackgroundResource(R.drawable.im_msg_item_audio_white_cornors_bg)
                    audioTime.setTextColor(ContextCompat.getColor(context, R.color.bg_origin))
                    audioPlayView.setPaintColor(ContextCompat.getColor(context, R.color.bg_origin))
                }
            }else{
                if (messageBean.getReplyMsgQuestionIsPublished() == false) audioLinearLayout.setBackgroundResource(R.drawable.im_msg_item_audio_purple_cornors_bg)
                else audioLinearLayout.setBackgroundResource(R.drawable.im_msg_item_audio_origin_cornors_bg)
                audioTime.setTextColor(ContextCompat.getColor(context, R.color.text_color_white))
                audioPlayView.setPaintColor(ContextCompat.getColor(context, R.color.text_color_white))
            }
        }

        audioTime.text = "${messageBean.getAudioContentDuration() ?: 0}\""
        audioPlayView.isAnim = messageBean.isAudioPlaying() == true
        setOnClickListener {
            if (!audioPlayView.isAnim) {
                messageBean.playAudio()
            } else {
                messageBean.stopAudio()
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