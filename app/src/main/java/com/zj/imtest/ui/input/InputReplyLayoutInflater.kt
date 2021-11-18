package com.zj.imtest.ui.input

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.zj.ccIm.core.MsgType
import com.zj.database.entity.MessageInfoEntity
import com.zj.imtest.R

class InputReplyLayoutInflater(v: View, private val closeIn: () -> Unit) {

    private var close: View = v.findViewById(R.id.im_conversation_input_reply_iv_close)
    private var replyUsername: TextView = v.findViewById(R.id.im_conversation_input_reply_name)
    private var replyContent: TextView = v.findViewById(R.id.im_conversation_input_reply_content)
    private var replyQuestionType: TextView = v.findViewById(R.id.im_conversation_input_reply_question_type)
    private var replyQuestionRewardView: View = v.findViewById(R.id.im_conversation_input_reply_question_reward_layout)
    private var replyQuestionReward: TextView = v.findViewById(R.id.im_conversation_input_reply_question_reward)

    companion object {

        fun inflate(container: FrameLayout?, inflater: LayoutInflater?, d: MessageInfoEntity?, closeIn: () -> Unit) {
            val v = inflater?.inflate(R.layout.im_conversation_reply, container) ?: return
            val il = InputReplyLayoutInflater(v, closeIn)
            il.initData(v.context, d ?: return)
        }
    }

    private fun initData(context: Context, d: MessageInfoEntity) {
        close.setOnClickListener { closeIn() }
        replyUsername.text = d.sender?.senderName
        replyContent.setCompoundDrawables(if (d.questionContent == null) null else ContextCompat.getDrawable(context, R.mipmap.im_v_session_question), null, null, null)
        replyContent.text = d.transDataType(context)
        if (d.questionContent == null) {
            replyQuestionType.visibility = View.GONE
            replyQuestionRewardView.visibility = View.GONE
        } else {
            val isPublic = d.questionContent?.published == true
            val spark = d.questionContent?.spark ?: 0
            replyQuestionRewardView.visibility = View.VISIBLE
            replyQuestionType.text = if (isPublic) context.getString(R.string._public) else context.getString(R.string._private)
            val questingRewardText = "+$spark"
            replyQuestionReward.text = questingRewardText
        }
    }

    private fun MessageInfoEntity.transDataType(context: Context): String? {
        if (questionContent != null) {
            return questionContent?.textContent?.text
        }
        return when (msgType) {
            MsgType.TEXT.type -> textContent?.text
            MsgType.IMG.type -> "[${context.getString(R.string.image)}]"
            MsgType.AUDIO.type -> "[${context.getString(R.string.voice)}]"
            MsgType.CC_VIDEO.type -> "[${context.getString(R.string.im_msg_type_video)}]"
            else -> null
        }
    }

}