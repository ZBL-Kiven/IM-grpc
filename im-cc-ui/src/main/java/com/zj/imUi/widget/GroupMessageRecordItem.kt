package com.zj.imUi.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.zj.imUi.R
import com.zj.imUi.UiMsgType
import com.zj.imUi.interfaces.ImMsgIn


open class GroupMessageRecordItem @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attributeSet, defStyle) {

    private lateinit var audioTime: AppCompatTextView
    private lateinit var audioPlayView: VoicePlayView
    private lateinit var audioLinearLayout: LinearLayout

    init {
        onInit()
    }

    private fun onInit() {
        LayoutInflater.from(context).inflate(R.layout.im_msg_item_widget_record, this, true)
        audioTime = findViewById(R.id.im_msg_item_audio_time)
        audioPlayView = findViewById(R.id.im_msg_item_audio_play_view)
        audioLinearLayout = findViewById(R.id.im_msg_item_widget_record_ll)
    }


    @SuppressLint("SetTextI18n")
    fun setData(data: ImMsgIn?,chatType:Any): GroupMessageRecordItem {
        if (data == null) return this
        if (data.getAnswerContentAudioContentUrl() != null){
            // TODO: 2021/10/8 私聊状态下为白色
            audioTime.text = StringBuilder(data.getAnswerContentAudioContentDuration().toString()).append("''")
            if (data.getSelfUserId() == data.getOwnerId()){
                if(chatType == 1) {
                    audioLinearLayout.setBackgroundResource(R.drawable.im_msg_item_audio_white_cornors_bg)
                    audioTime.setTextColor(ContextCompat.getColor(context, R.color.im_msg_bg_origin))
                    audioPlayView.setPaintColor(ContextCompat.getColor(context, R.color.im_msg_bg_origin))
                }else{
                    audioLinearLayout.setBackgroundResource(R.drawable.im_msg_item_audio_self_origin_cornors_bg)
                    audioTime.setTextColor(ContextCompat.getColor(context, R.color.im_msg_text_color_white))
                    audioPlayView.setPaintColor(ContextCompat.getColor(context,R.color.im_msg_text_color_white))
                }
            } else {
                audioLinearLayout.setBackgroundResource(R.drawable.im_msg_item_audio_origin_cornors_bg)
                audioTime.setTextColor(ContextCompat.getColor(context, R.color.im_msg_text_color_white))
                audioPlayView.setPaintColor(ContextCompat.getColor(context,R.color.im_msg_text_color_white))            }
        }else {
            audioTime.text = StringBuilder(data.getAudioContentDuration().toString()).append("''")
            if ( data.getReplyMsgClientMsgId() == null) { //发送者为大V 且不是回复消息
                if (data.getSenderId() == data.getSelfUserId()) audioLinearLayout.setBackgroundResource(R.drawable.im_msg_item_audio_self_origin_cornors_bg)
                else audioLinearLayout.setBackgroundResource(R.drawable.im_msg_item_audio_origin_cornors_bg)
                audioTime.setTextColor(ContextCompat.getColor(context, R.color.im_msg_text_color_white))
                audioPlayView.setPaintColor(ContextCompat.getColor(context,R.color.im_msg_text_color_white))
            }else if (data.getReplyMsgType() == UiMsgType.MSG_TYPE_QUESTION ) {
                if (data.getSenderId() == data.getSelfUserId()){//大v自己发送打赏录音消息
                    if (data.getReplyMsgQuestionIsPublished() == false&&chatType == 1){
                        audioLinearLayout.setBackgroundResource(R.drawable.im_msg_item_audio_white_cornors_bg)
                        audioTime.setTextColor(ContextCompat.getColor(context, R.color.im_msg_bg_purple))
                        audioPlayView.setPaintColor(ContextCompat.getColor(context, R.color.im_msg_bg_purple))
                    }else{
                        audioLinearLayout.setBackgroundResource(R.drawable.im_msg_item_audio_white_cornors_bg)
                        audioTime.setTextColor(ContextCompat.getColor(context, R.color.im_msg_bg_origin))
                        audioPlayView.setPaintColor(ContextCompat.getColor(context, R.color.im_msg_bg_origin))
                    }
                }else{
                    if (data.getReplyMsgQuestionIsPublished() == false&&chatType == 1)
                        audioLinearLayout.setBackgroundResource(R.drawable.im_msg_item_audio_purple_cornors_bg)
                    else audioLinearLayout.setBackgroundResource(R.drawable.im_msg_item_audio_origin_cornors_bg)
                    audioTime.setTextColor(ContextCompat.getColor(context, R.color.im_msg_text_color_white))
                    audioPlayView.setPaintColor(ContextCompat.getColor(context, R.color.im_msg_text_color_white))
                }
            }else if (data.getReplyMsgType()==UiMsgType.MSG_TYPE_TEXT){
                audioLinearLayout.setBackgroundResource(R.drawable.im_msg_item_audio_white_cornors_bg)
                audioTime.setTextColor(ContextCompat.getColor(context, R.color.im_msg_bg_origin))
                audioPlayView.setPaintColor(ContextCompat.getColor(context, R.color.im_msg_bg_origin))
            }
        }
//        audioTime.text = "${data.getAudioContentDuration() ?: 0}\""
        audioPlayView.isAnim = data.isAudioPlaying() == true
        setOnClickListener {
            if (!audioPlayView.isAnim) {
                data.playAudio()
            } else {
                data.stopAudio()
            }
        }

        setOnLongClickListener {
            Log.d("LiXiang", "audio长按点击响应")
            val isNotSelf = data.getSelfUserId() != data.getSenderId()
            if (data.getType() == UiMsgType.MSG_TYPE_TEXT || isNotSelf) {
                MsgPop(context, data).show(it)
            }
            true
        }
        return this
    }

    fun startAnim() {
        audioPlayView.play()
    }

    fun stopAnim() {
        audioPlayView.stop()
    }
}