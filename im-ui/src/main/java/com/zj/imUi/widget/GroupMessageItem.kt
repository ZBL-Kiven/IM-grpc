package com.zj.rebuild.chat.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginLeft
import com.sanhe.baselibrary.utils.GlideUtils
import com.sanhe.baselibrary.utils.ToastUtils
import com.zj.rebuild.BuildConfig
import com.zj.rebuild.R
import com.zj.rebuild.chat.MessageTypeConstant
import com.zj.rebuild.chat.MessageType
import com.zj.rebuild.chat.bean.MessageBean
import kotlin.math.min
import com.zj.rebuild.chat.widget.bottom.GroupMessageItemTime
import com.zj.rebuild.chat.widget.top.GroupMessageItemTitle
import java.lang.StringBuilder


/**
 * author: 李 祥
 * date:   2021/8/9 8:13 下午
 * description:  chat消息容器
 */
@SuppressLint("ResourceAsColor")
class GroupMessageItem @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attributeSet, defStyle) {

    private var linearLayoutContent: LinearLayout

    private var paint: Paint? = null
    private var itembgColor = ContextCompat.getColor(context, R.color.item_bg_white)
    private var mBgColorOrigin = resources.getColor(R.color.bg_origin)
    private var mBgColorWhite = resources.getColor(R.color.item_bg_white)

    private var messagestatus: Int = -1
    private var isOthersMessage = false
    private var isOwnner = false
    private var isOwnerReplyQuestionIsPublic = true

    init {
        setWillNotDraw(false);
        LayoutInflater.from(context).inflate(R.layout.group_message_item, this, true)
        linearLayoutContent = findViewById(R.id.ll_message_item)
        linearLayoutContent.gravity = LinearLayout.VERTICAL
    }


    //设置气泡最大宽度为屏幕80%
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = MeasureSpec.getSize(widthMeasureSpec)
        val maxWidth = (resources.displayMetrics.widthPixels * 0.8).toInt()
        val min = min(size, maxWidth)
        val measureSpec = MeasureSpec.makeMeasureSpec(min, MeasureSpec.AT_MOST)
        super.onMeasure(measureSpec, heightMeasureSpec)
    }


    override fun onDraw(canvas: Canvas?) {
        drawBackGround(canvas)
    }

    //圆弧背景
    private fun drawBackGround(canvas: Canvas?) {
        if (paint == null) paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val path = Path()
        paint?.strokeWidth = 1f
        paint?.style = Paint.Style.FILL
        paint?.color = mBgColorWhite
        if (!isOthersMessage) {
            //发送者为自己
            paint?.color = mBgColorOrigin

            when (messagestatus) {
                MessageTypeConstant.MESSAGE_REWARD_TYPE_REPLIED ->
                    paint?.color = resources.getColor(R.color.message_item_timeout_add_replied)
                MessageTypeConstant.MESSAGE_REWARD_TYPE_REPLY_PUBLIC ->
                    paint?.color = resources.getColor(R.color.message_item_need_reply)
                MessageTypeConstant.MESSAGE_REWARD_TYPE_REPLY_PRIVATE ->
                    paint?.color = resources.getColor(R.color.message_item_private)
            }

            val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
            val radii = floatArrayOf(
                dpToPx(context, 8f),
                dpToPx(context, 8f),
                dpToPx(context, 8f),
                dpToPx(context, 8f),
                dpToPx(context, 0f),
                dpToPx(context, 0f),
                dpToPx(context, 8f),
                dpToPx(context, 8f)
            )
            path.addRoundRect(rectF, radii, Path.Direction.CW)
            canvas?.drawPath(path, paint!!)
        } else {
            //发送者为其他人
            paint?.color = mBgColorWhite
            paint?.style = Paint.Style.FILL
            when (messagestatus) {
                MessageTypeConstant.MESSAGE_REWARD_TYPE_REPLIED ->
                    paint?.color = resources.getColor(R.color.message_item_timeout_add_replied)
                MessageTypeConstant.MESSAGE_RECEIVE_REWARD_TYPE_REPLY_PUBLIC ->
                    paint?.color = mBgColorWhite
                MessageTypeConstant.MESSAGE_REWARD_TYPE_REPLY_PRIVATE ->
                    paint?.color = resources.getColor(R.color.message_item_private)
            }
            val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
            val radii = floatArrayOf(
                dpToPx(context, 0f),
                dpToPx(context, 0f),
                dpToPx(context, 12f),
                dpToPx(context, 12f),
                dpToPx(context, 8f),
                dpToPx(context, 8f),
                dpToPx(context, 8f),
                dpToPx(context, 8f)
            )
            path.addRoundRect(rectF, radii, Path.Direction.CW)
            canvas?.drawPath(path, paint!!)

            //发送者为大V
            if (isOwnner && isOwnerReplyQuestionIsPublic) {
                paint!!.color = mBgColorOrigin
                paint!!.style = Paint.Style.STROKE
                //画金色边框
                canvas?.drawPath(drawRect(paint!!), paint!!)
            }
        }
    }

    private fun drawRect(paint: Paint): Path {
        val path = Path()
        val rectF = RectF(1f, 1f, width.toFloat() - 1, height.toFloat() - 1)
        val radii = floatArrayOf(
            dpToPx(context, 0f),
            dpToPx(context, 0f),
            dpToPx(context, 12f),
            dpToPx(context, 12f),
            dpToPx(context, 8f),
            dpToPx(context, 8f),
            dpToPx(context, 8f),
            dpToPx(context, 8f)
        )
        path.addRoundRect(rectF, radii, Path.Direction.CW)
        return path
    }

    fun dpToPx(context: Context, dipValue: Float): Float {
        val scale = context.applicationContext.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toFloat()
    }

    //动态添加布局
    fun setData(userId: Int, type: MessageType, messageBean: MessageBean) {
        isOthersMessage = messageBean.sender.senderId != userId
        isOwnner = messageBean.sender.senderId == messageBean.ownerId
        //先移除所有控件,不然会重复添加
        linearLayoutContent.removeAllViews()
        when (type) {
            MessageType.SEND_NORMAL_TEXT -> {
                val lp = LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
                )
                val inflater3 = LayoutInflater.from(context)
                val view: View = inflater3.inflate(R.layout.group_message_type_normal_text, null)
                lp.setMargins(12, 12, 12, 12)
                view.layoutParams = lp
                val textView: AppCompatTextView = view.findViewById(R.id.tv_message_normal_text)
                textView.text = messageBean.textContent?.text
                textView.setTextColor(resources.getColor(R.color.white))
                linearLayoutContent.addView(view)
            }
            MessageType.SEND_NORMAL_IMG -> {
                val lp = LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
                )
                val inflater3 = LayoutInflater.from(context)
                val view: View = inflater3.inflate(R.layout.group_message_type_normal_img, null)
                view.layoutParams = lp
                val imgVIew: AppCompatImageView = view.findViewById(R.id.tv_message_normal_img)
                messageBean.imgContent?.url?.let {
                    GlideUtils.loadRoundCornerUrlImage2(
                        context,
                        it,
                        imgVIew,
                        8,
                        setImgWidth(messageBean.imgContent!!.width),
                        setImgHeight(messageBean.imgContent!!.height)
                    )
                }
                linearLayoutContent.addView(view)
            }
            MessageType.SEND_NORMAL_RECORD -> {

            }

            MessageType.SEND_REWARD_MESSAGE -> {
                //内容
                val groupRewardQuestionContent = GroupRewardQuestionContent(context, null, 0)
                groupRewardQuestionContent.setData(userId, messageBean)
                //底部内容
                val groupMessageItemTime = GroupMessageItemTime(context, null, 0)
                groupMessageItemTime.setData(messageBean)
                //添加布局
                linearLayoutContent.addView(groupRewardQuestionContent)
                linearLayoutContent.addView(groupMessageItemTime)
                if (messageBean.questionContent?.questionStatus == 0) {
                    if (messageBean.questionContent?.isPublic == true) {
                        messagestatus = MessageTypeConstant.MESSAGE_REWARD_TYPE_REPLY_PUBLIC
                    } else
                        messagestatus = MessageTypeConstant.MESSAGE_REWARD_TYPE_REPLY_PRIVATE
                } else
                    messagestatus = MessageTypeConstant.MESSAGE_REWARD_TYPE_REPLIED
            }

            MessageType.SEND_REPLY_MESSAGE -> {
                //回复其他人的text消息 我是群主 回复普通消息 消息发送者为大V自己
                if (messageBean.ownerId == messageBean.sender.senderId && messageBean.ownerId == userId) {
                    if (messageBean.replyMsg?.msgType == "text")
                    //群主视角 自己回复其他人的text类型消息
                        when (messageBean.msgType) {
                            "text" -> {
                                //回复text
                                val lp = LayoutParams(
                                    LayoutParams.MATCH_PARENT,
                                    LayoutParams.WRAP_CONTENT
                                )
                                val inflater3 = LayoutInflater.from(context)
                                val view: View = inflater3.inflate(
                                    R.layout.group_message_type_normal_me_replygroupmember_text,
                                    null
                                )
                                view.layoutParams = lp
                                val textReply: AppCompatTextView =
                                    view.findViewById(R.id.tv_message_normal_replygroupmember_text)
                                val textQuestion: AppCompatTextView =
                                    view.findViewById(R.id.tv_message_normal_needreply_text)
                                val textNickname: AppCompatTextView =
                                    view.findViewById(R.id.tv_nickname)
                                textNickname.text = messageBean.replyMsg?.sender?.senderName
                                textReply.text = messageBean.textContent?.text
                                textQuestion.text = messageBean.replyMsg?.textContent?.text
                                linearLayoutContent.addView(view)
                            }
                            "img" -> {
                                //回复图片
                                val lp = LayoutParams(
                                    LayoutParams.MATCH_PARENT,
                                    LayoutParams.WRAP_CONTENT
                                )
                                val inflater3 = LayoutInflater.from(context)
                                val view: View = inflater3.inflate(
                                    R.layout.group_message_type_normal_groupowner_replyme_img,
                                    null
                                )
                                view.layoutParams = lp
                                val imgReply: AppCompatImageView =
                                    view.findViewById(R.id.tv_message_normal_replygroupmember_img)
                                val textQuestion: AppCompatTextView =
                                    view.findViewById(R.id.tv_message_normal_needreply_text)
                                // TODO: 2021/8/17 先给个圆弧图片  且未设置宽高
                                messageBean.imgContent?.url?.let {
                                    GlideUtils.loadRoundCornerUrlImage2(
                                        context,
                                        it,
                                        imgReply,
                                        8,
                                        setImgWidth(messageBean.imgContent!!.width),
                                        setImgHeight(messageBean.imgContent!!.height)
                                    )
                                }
                                textQuestion.text = messageBean.replyMsg!!.textContent?.text
                                linearLayoutContent.addView(view)
                            }
                            "audio" -> {
                                //回复语音
                                val lp = LayoutParams(
                                    LayoutParams.MATCH_PARENT,
                                    LayoutParams.WRAP_CONTENT
                                )
                                val inflater3 = LayoutInflater.from(context)
                                val view: View = inflater3.inflate(
                                    R.layout.group_message_type_normal_groupowner_replyme_record,
                                    null
                                )
                                view.layoutParams = lp
                                val audioReply: GroupMessageRecordItem =
                                    view.findViewById(R.id.tv_message_normal_replygroupmember_record)
                                val textQuestion: AppCompatTextView =
                                    view.findViewById(R.id.tv_message_normal_needreply_text)
                                // TODO: 2021/8/17 录音还未设置
                                textQuestion.text = messageBean.replyMsg?.textContent?.text
                                linearLayoutContent.addView(view)
                            }
                            "question" -> {
                                //回复悬赏消息
                                if (messageBean.replyMsg?.questionContent?.isPublic == true) {
                                    messagestatus =
                                        MessageTypeConstant.MESSAGE_REWARD_TYPE_REPLY_PUBLIC
                                } else
                                    messagestatus =
                                        MessageTypeConstant.MESSAGE_REWARD_TYPE_REPLY_PRIVATE
                                //回复打赏消息
                                when (messageBean.replyMsg?.questionContent?.contentType) {
                                    "text" -> {
                                        val lp = LayoutParams(
                                            LayoutParams.MATCH_PARENT,
                                            LayoutParams.WRAP_CONTENT
                                        )
                                        val inflater = LayoutInflater.from(context)
                                        val view: View = inflater.inflate(
                                            R.layout.group_message_type_reward_me_already_reply_text,
                                            null
                                        )
                                        view.layoutParams = lp
                                        val textReply: AppCompatTextView =
                                            view.findViewById(R.id.tv_text)
                                        val textQuestion: AppCompatTextView =
                                            view.findViewById(R.id.tv_question)
                                        val textNickname: AppCompatTextView =
                                            view.findViewById(R.id.tv_nickname)
                                        //群主（我）的回复消息
                                        textReply.text = messageBean.textContent?.text
                                        textNickname.text = messageBean.replyMsg?.sender?.senderName
                                        //拼接打赏的提问消息
                                        var stringBuilder: StringBuilder = StringBuilder()
                                        textQuestion.text = stringBuilder.append(": ")
                                            .append(messageBean.replyMsg?.questionContent?.textContent?.text)
                                        linearLayoutContent.addView(view)
                                        //添加底部内容
                                        linearLayoutContent.addView(addBottomView(messageBean))
                                    }
                                    "img" -> {
                                        val lp = LayoutParams(
                                            LayoutParams.MATCH_PARENT,
                                            LayoutParams.WRAP_CONTENT
                                        )
                                        val inflater = LayoutInflater.from(context)
                                        val view: View = inflater.inflate(
                                            R.layout.group_message_type_reward_me_already_reply_img,
                                            null
                                        )
                                        view.layoutParams = lp
                                        val imgReply: AppCompatImageView =
                                            view.findViewById(R.id.img_reply_pic)
                                        val textQuestion: AppCompatTextView =
                                            view.findViewById(R.id.tv_question)
                                        val textNickname: AppCompatTextView =
                                            view.findViewById(R.id.tv_nickname)
                                        textNickname.text = messageBean.replyMsg?.sender?.senderName
                                        //拼接打赏的提问消息
                                        var stringBuilder: StringBuilder = StringBuilder()
                                        textQuestion.text = stringBuilder.append(": ")
                                            .append(messageBean.replyMsg?.questionContent?.textContent?.text)
                                        //群主（我）的回复消息
                                        messageBean.imgContent?.url?.let {
                                            GlideUtils.loadRoundCornerUrlImage2(
                                                context,
                                                it,
                                                imgReply,
                                                8,
                                                setImgWidth(messageBean.imgContent!!.width),
                                                setImgHeight(messageBean.imgContent!!.height)
                                            )
                                        }
                                        linearLayoutContent.addView(view)
                                        //添加底部内容
                                        linearLayoutContent.addView(addBottomView(messageBean))
                                    }
                                    "audio" -> {

                                        val lp = LayoutParams(
                                            LayoutParams.MATCH_PARENT,
                                            LayoutParams.WRAP_CONTENT
                                        )
                                        val inflater = LayoutInflater.from(context)
                                        val view: View = inflater.inflate(
                                            R.layout.group_message_type_reward_me_already_reply_record,
                                            null
                                        )
                                        view.layoutParams = lp
                                        val recordReply: GroupMessageRecordItem =
                                            view.findViewById(R.id.record)
                                        val textQuestion: AppCompatTextView =
                                            view.findViewById(R.id.tv_question)
                                        val textNickname: AppCompatTextView =
                                            view.findViewById(R.id.tv_nickname)
                                        textNickname.text = messageBean.replyMsg?.sender?.senderName
                                        //拼接打赏的提问消息
                                        var stringBuilder: StringBuilder = StringBuilder()
                                        textQuestion.text = stringBuilder.append(": ")
                                            .append(messageBean.replyMsg?.questionContent?.textContent?.text)

                                        //群主（我）的回复消息
                                        // TODO: 2021/8/24 录音 
                                        linearLayoutContent.addView(view)
                                        //添加底部内容
                                        linearLayoutContent.addView(addBottomView(messageBean))
                                    }
                                    else -> {
                                        val string: StringBuilder =
                                            StringBuilder("回复的reward类型不是指定的img，audio和text").append(
                                                messageBean.replyMsg?.questionContent?.contentType
                                            )
                                        ToastUtils.showToast(context, string.toString(), 0)
                                    }
                                }
                            }
                            else -> {
                                val string: StringBuilder =
                                    StringBuilder("回复的消息类型不是指定的img，audio和text，question").append(
                                        messageBean.replyMsg?.questionContent?.contentType
                                    )
                                ToastUtils.showToast(context, string.toString(), 0)
                            }
                        }
                } else if (messageBean.sender.senderId == userId && messageBean.ownerId != userId) {
                    //群员（我）回复其他人 只能用text回复 text，img和record消息 消息发送者为群员自己
                    if (messageBean.replyMsg?.msgType == "text") {
                        when (messageBean.msgType) {
                            "text" -> {
                                //回复text
                                val lp = LayoutParams(
                                    LayoutParams.MATCH_PARENT,
                                    LayoutParams.WRAP_CONTENT
                                )
                                val inflater3 = LayoutInflater.from(context)
                                val view: View = inflater3.inflate(
                                    R.layout.group_message_type_normal_me_replygroupmember_text,
                                    null
                                )
                                view.layoutParams = lp
                                val textReply: AppCompatTextView =
                                    view.findViewById(R.id.tv_message_normal_replygroupmember_text)
                                val textQuestion: AppCompatTextView =
                                    view.findViewById(R.id.tv_message_normal_needreply_text)
                                textReply.text = messageBean.textContent?.text
                                textReply.setTextColor(resources.getColor(R.color.text_color_white))
                                val textNickname: AppCompatTextView =
                                    view.findViewById(R.id.tv_nickname)
                                textNickname.text = messageBean.replyMsg?.sender?.senderName
                                textQuestion.text = messageBean.replyMsg?.textContent?.text
                                linearLayoutContent.addView(view)
                            }
                            else -> {
                                val string: StringBuilder =
                                    StringBuilder("普通群成员只能发送text内容").append(messageBean.replyMsg?.questionContent?.contentType)
                                ToastUtils.showToast(context, string.toString(), 0)
                            }
                        }
                    } else if (messageBean.replyMsg?.msgType == "img") {
                        //这儿是用户回复大V的图片
                        //回复text
                        val lp = LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT
                        )
                        val inflater3 = LayoutInflater.from(context)
                        val view: View = inflater3.inflate(
                            R.layout.group_message_type_normal_groupmember_reply_img,
                            null
                        )
                        view.layoutParams = lp
                        val textNickname: AppCompatTextView =
                            view.findViewById(R.id.tv_nickname)
                        val textReply: AppCompatTextView =
                            view.findViewById(R.id.tv_message_normal_reply_text)
                        val imgOwner: AppCompatImageView =
                            view.findViewById(R.id.tv_message_normal_reply_img)
                        //大V的昵称
                        textNickname.text = messageBean.replyMsg?.sender?.senderName
                        textReply.text = messageBean.textContent?.text
                        textReply.setTextColor(resources.getColor(R.color.text_color_white))
                        //图片
                        messageBean.replyMsg?.imgContent?.url?.let {
                            GlideUtils.loadRoundCornerUrlImage2(
                                context,
                                it,
                                imgOwner,
                                8,
                                setImgWidth(messageBean.replyMsg!!.imgContent!!.width),
                                setImgHeight(messageBean.replyMsg!!.imgContent!!.height)
                            )
                        }
                        linearLayoutContent.addView(view)
                    } else if (messageBean.replyMsg?.msgType == "audio") {
                        //这儿是用户回复大V的音频
                        //回复text
                        val lp = LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.MATCH_PARENT
                        )
                        val inflater3 = LayoutInflater.from(context)
                        val view: View = inflater3.inflate(
                            R.layout.group_message_type_normal_groupmember_reply_record,
                            null
                        )
                        view.layoutParams = lp
                        val textReply: AppCompatTextView =
                            view.findViewById(R.id.tv_message_normal_reply_text)
                        val textNickname: AppCompatTextView =
                            view.findViewById(R.id.tv_nickname)
                        val audioOwner: GroupMessageRecordItem =
                            view.findViewById(R.id.tv_message_normal_reply_record)
                        //大V的昵称
                        textNickname.text = messageBean.replyMsg?.sender?.senderName
                        //自己回复的text内容
                        textReply.text = messageBean.textContent?.text
                        textReply.setTextColor(resources.getColor(R.color.text_color_white))
                        // TODO: 2021/8/18 设置录音
                        linearLayoutContent.addView(view)
                    }
                } else {
                    ToastUtils.showToast(context, "此消息是发送消息吗？", 0)
                }
            }

            MessageType.RECEIVE_NORMAL_TEXT -> {
                val lp = LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
                )
                val inflater3 = LayoutInflater.from(context)
                val view: View = inflater3.inflate(R.layout.group_message_type_normal_text, null)
                lp.setMargins(12, 5, 12, 12)
                view.layoutParams = lp
                val textView: AppCompatTextView = view.findViewById(R.id.tv_message_normal_text)
                textView.text = messageBean.textContent?.text
                textView.setTextColor(resources.getColor(R.color.text_color_black))
                //添加顶部Title
                linearLayoutContent.addView(addTitleView(userId, messageBean))
                linearLayoutContent.addView(view)
            }
            MessageType.RECEIVE_NORMAL_IMG -> {
                val lp = LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
                )
                val inflater3 = LayoutInflater.from(context)
                val view: View = inflater3.inflate(R.layout.group_message_type_normal_img, null)
                view.layoutParams = lp
                val imgView: AppCompatImageView = view.findViewById(R.id.tv_message_normal_img)

                messageBean.imgContent?.url?.let {
                    GlideUtils.loadRoundCornerUrlImage2(
                        context,
                        it,
                        imgView,
                        8,
                        setImgWidth(messageBean.imgContent!!.width),
                        setImgHeight(messageBean.imgContent!!.height)
                    )
                }
                //添加顶部Title
                linearLayoutContent.addView(addTitleView(userId, messageBean))
                //记得设置为金边
                linearLayoutContent.addView(view)
            }

            MessageType.RECEIVE_NORMAL_AUDIO -> {
                val lp = LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
                )
                val inflater3 = LayoutInflater.from(context)
                val view: View = inflater3.inflate(R.layout.group_message_type_normal_record, null)
                view.layoutParams = lp
                // TODO: 2021/8/18  录音
                val recordView: GroupMessageRecordItem =
                    view.findViewById(R.id.tv_message_normal_record)
                //添加顶部Title
                linearLayoutContent.addView(addTitleView(userId, messageBean))
                linearLayoutContent.addView(view)
            }
            MessageType.RECEIVE_REWARD_QUESTION -> {
                if (userId != messageBean.ownerId && messageBean.questionContent?.isPublic == true) {
                    //群员视角 看别人发的打赏消息，  如打赏消息为隐私，则不展示
                    val groupRewardQuestionContent = GroupRewardQuestionContent(context, null, 0)
                    groupRewardQuestionContent.setData(userId, messageBean)

                    //添加顶部Title
                    linearLayoutContent.addView(addTitleView(userId, messageBean))
                    //添加布局
                    linearLayoutContent.addView(groupRewardQuestionContent)

                    messagestatus = if (messageBean.questionContent?.questionStatus == 0) {
                        if (messageBean.questionContent?.isPublic == true) {
                            MessageTypeConstant.MESSAGE_RECEIVE_REWARD_TYPE_REPLY_PUBLIC
                        } else
                            MessageTypeConstant.MESSAGE_REWARD_TYPE_REPLY_PRIVATE
                    } else
                        MessageTypeConstant.MESSAGE_REWARD_TYPE_REPLIED
                } else if (userId == messageBean.ownerId) {
                    //群主视角
                    //群主视角 看别人发的打赏消息
                    val groupRewardQuestionContent = GroupRewardQuestionContent(context, null, 0)
                    groupRewardQuestionContent.setData(userId, messageBean)
                    //添加顶部Title
                    linearLayoutContent.addView(addTitleView(userId, messageBean))
                    //添加布局
                    linearLayoutContent.addView(groupRewardQuestionContent)

                    messagestatus = if (messageBean.questionContent?.questionStatus == 0) {
                        if (messageBean.questionContent?.isPublic == true) {
                            MessageTypeConstant.MESSAGE_REWARD_TYPE_REPLY_PUBLIC
                        } else
                            MessageTypeConstant.MESSAGE_REWARD_TYPE_REPLY_PRIVATE
                    } else
                        MessageTypeConstant.MESSAGE_REWARD_TYPE_REPLIED
                } else {
                    if (BuildConfig.DEBUG) {
                        ToastUtils.showToast(context, "此消息是接收消息吗？", 0)
                    }
                }
            }

            //接收到回复类型消息
            MessageType.RECEIVE_REPLY_MESSAGE -> {
                //回复的内容为text
                if (messageBean.msgType == "text") {
                    //添加title
                    linearLayoutContent.addView(addTitleView(userId, messageBean))
                    //被回复的内容为text
                    if (messageBean.replyMsg?.msgType == "text") {
                        val lp = LayoutParams(
                            LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
                        )
                        val inflater: LayoutInflater = LayoutInflater.from(context)
                        val view: View = inflater.inflate(
                            R.layout.group_message_type_normal_groupowner_replyme_text,
                            null
                        )
                        view.layoutParams = lp
                        val textSender: AppCompatTextView =
                            view.findViewById(R.id.tv_message_normal_ownerreplyme_text)
                        val textReplyMsg: AppCompatTextView =
                            view.findViewById(R.id.tv_message_normal_replyme_word)
                        val textNickname: AppCompatTextView = view.findViewById(R.id.tv_nickname)
                        val textFlag: AppCompatTextView = view.findViewById(R.id.tv_flag)
                        textNickname.text = messageBean.replyMsg?.sender?.senderName
                        textSender.text = messageBean.textContent?.text
                        textReplyMsg.text = messageBean.replyMsg?.textContent?.text
                        if (messageBean.replyMsg?.sender?.senderId == userId) {
                            //如果是回复自己，则自己的内容字体为金色
                            textReplyMsg.setTextColor(resources.getColor(R.color.message_textColor_me))
                            textNickname.setTextColor(resources.getColor(R.color.message_textColor_me))
                            textFlag.setTextColor(resources.getColor(R.color.message_textColor_me))
                        } else {
                            textReplyMsg.setTextColor(resources.getColor(R.color.frame_textview_private))
                            textNickname.setTextColor(resources.getColor(R.color.frame_textview_private))
                            textFlag.setTextColor(resources.getColor(R.color.frame_textview_private))
                        }
                        linearLayoutContent.addView(view)
                    } else if (messageBean.replyMsg?.msgType == "img") {
                        //大V图片消息被群员回复
                        val lp = LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT
                        )
                        val inflater3 = LayoutInflater.from(context)
                        val view: View = inflater3.inflate(
                            R.layout.group_message_type_normal_groupmember_reply_img,
                            null
                        )
                        view.layoutParams = lp
                        val textNickname: AppCompatTextView =
                            view.findViewById(R.id.tv_nickname)
                        val textReply: AppCompatTextView =
                            view.findViewById(R.id.tv_message_normal_reply_text)
                        val imgOwner: AppCompatImageView =
                            view.findViewById(R.id.tv_message_normal_reply_img)
                        val textFlag: AppCompatTextView = view.findViewById(R.id.tv_flag)
                        textNickname.text = messageBean.replyMsg?.sender?.senderName
                        textReply.text = messageBean.textContent?.text
                        //图片
                        messageBean.replyMsg?.imgContent?.url?.let {
                            GlideUtils.loadRoundCornerUrlImage2(
                                context,
                                it,
                                imgOwner,
                                8,
                                setImgWidth(messageBean.replyMsg!!.imgContent!!.width),
                                setImgHeight(messageBean.replyMsg!!.imgContent!!.height)
                            )
                        }
                        if (messageBean.replyMsg?.sender?.senderId == userId) {
                            //如果是回复自己，则自己的内容字体为金色
                            textNickname.setTextColor(resources.getColor(R.color.message_textColor_me))
                            textFlag.setTextColor(resources.getColor(R.color.message_textColor_me))
                        } else {
                            textNickname.setTextColor(resources.getColor(R.color.frame_textview_private))
                            textFlag.setTextColor(resources.getColor(R.color.frame_textview_private))
                        }
                        linearLayoutContent.addView(view)

                    } else if (messageBean.replyMsg?.msgType == "audio") {
                        //回复text
                        val lp = LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT
                        )
                        val inflater3 = LayoutInflater.from(context)
                        val view: View = inflater3.inflate(
                            R.layout.group_message_type_normal_groupmember_reply_record,
                            null
                        )
                        view.layoutParams = lp
                        val textReply: AppCompatTextView =
                            view.findViewById(R.id.tv_message_normal_reply_text)
                        val textNickname: AppCompatTextView =
                            view.findViewById(R.id.tv_nickname)
                        val audioOwner: GroupMessageRecordItem =
                            view.findViewById(R.id.tv_message_normal_reply_record)
                        val textFlag: AppCompatTextView = view.findViewById(R.id.tv_flag)

                        //大V的昵称
                        textNickname.text = messageBean.replyMsg?.sender?.senderName
                        //自己回复的text内容
                        textReply.text = messageBean.textContent?.text
                        // TODO: 2021/8/18 设置录音

                        if (messageBean.replyMsg?.sender?.senderId == userId) {
                            //如果是回复自己，则自己的内容字体为金色
                            textNickname.setTextColor(resources.getColor(R.color.message_textColor_me))
                            textFlag.setTextColor(resources.getColor(R.color.message_textColor_me))
                        } else {
                            textNickname.setTextColor(resources.getColor(R.color.frame_textview_private))
                            textFlag.setTextColor(resources.getColor(R.color.frame_textview_private))
                        }
                        linearLayoutContent.addView(view)
                    } else if (messageBean.replyMsg?.msgType == "question") {
                        //回复悬赏消息
                        if (messageBean.replyMsg?.questionContent?.isPublic == true) {
                            messagestatus =
                                MessageTypeConstant.MESSAGE_REWARD_TYPE_REPLY_PUBLIC
                        } else {
                            messagestatus =
                                MessageTypeConstant.MESSAGE_REWARD_TYPE_REPLY_PRIVATE
                            isOwnerReplyQuestionIsPublic = false
                        }
                        //回复打赏消息
                        when (messageBean.replyMsg?.questionContent?.contentType) {
                            "text" -> {
                                val lp = LayoutParams(
                                    LayoutParams.MATCH_PARENT,
                                    LayoutParams.WRAP_CONTENT
                                )
                                val inflater = LayoutInflater.from(context)
                                val view: View = inflater.inflate(
                                    R.layout.group_message_type_reward_me_already_reply_text,
                                    null
                                )
                                view.layoutParams = lp
                                val textReply: AppCompatTextView =
                                    view.findViewById(R.id.tv_text)
                                val textQuestion: AppCompatTextView =
                                    view.findViewById(R.id.tv_question)
                                val imgReplyFlag: ImageView = view.findViewById(R.id.img_reply)

                                val textNickname: AppCompatTextView =
                                    view.findViewById(R.id.tv_nickname)
                                textNickname.text = messageBean.replyMsg?.sender?.senderName
                                //设置内容属性
                                imgReplyFlag.setImageResource(R.drawable.icon_huida_normal)
                                if (messageBean.replyMsg?.sender?.senderId == userId) {
                                    textQuestion.setTextColor(context.resources.getColor(R.color.bg_origin))
                                    textNickname.setTextColor(context.resources.getColor(R.color.bg_origin))
                                } else {
                                    textQuestion.setTextColor(context.resources.getColor(R.color.text_color_gray)) //回复其他人为灰色
                                    textNickname.setTextColor(context.resources.getColor(R.color.text_color_gray)) //回复其他人为灰色
                                }

                                if (messageBean.replyMsg?.questionContent?.isPublic == false) {
                                    textQuestion.setTextColor(context.resources.getColor(R.color.bg_purple))
                                    textNickname.setTextColor(context.resources.getColor(R.color.bg_purple))
                                }
                                textReply.setTextColor(context.resources.getColor(R.color.text_color_black))
                                //群主的悬赏回复消息
                                textReply.text = messageBean.textContent?.text
                                //拼接打赏的提问消息

                                var stringBuilder: StringBuilder = StringBuilder()
                                textQuestion.text = stringBuilder.append(": ")
                                    .append(messageBean.replyMsg?.questionContent?.textContent?.text)

                                linearLayoutContent.addView(view)
                            }
                            "img" -> {
                                val lp = LayoutParams(
                                    LayoutParams.MATCH_PARENT,
                                    LayoutParams.WRAP_CONTENT
                                )
                                val inflater = LayoutInflater.from(context)
                                val view: View = inflater.inflate(
                                    R.layout.group_message_type_reward_me_already_reply_img,
                                    null
                                )
                                view.layoutParams = lp
                                val imgReply: AppCompatImageView =
                                    view.findViewById(R.id.img_reply_pic)
                                val textQuestion: AppCompatTextView =
                                    view.findViewById(R.id.tv_question)
                                val imgReplyFlag: ImageView = view.findViewById(R.id.img_reply)
                                imgReplyFlag.setImageResource(R.drawable.icon_huida_normal)
                                val textNickname: AppCompatTextView =
                                    view.findViewById(R.id.tv_nickname)

                                //群主（我）的回复消息
                                messageBean.imgContent?.url?.let {
                                    GlideUtils.loadRoundCornerUrlImage2(
                                        context,
                                        it,
                                        imgReply,
                                        8,
                                        setImgWidth(messageBean.imgContent!!.width),
                                        setImgHeight(messageBean.imgContent!!.height)
                                    )
                                }
                                if (messageBean.replyMsg?.sender?.senderId == userId) {
                                    textQuestion.setTextColor(context.resources.getColor(R.color.bg_origin))
                                    textNickname.setTextColor(context.resources.getColor(R.color.bg_origin))
                                } else {
                                    textQuestion.setTextColor(context.resources.getColor(R.color.text_color_gray)) //回复其他人为灰色
                                    textNickname.setTextColor(context.resources.getColor(R.color.text_color_gray)) //回复其他人为灰色
                                }
                                if (messageBean.replyMsg?.questionContent?.isPublic == false) {
                                    textQuestion.setTextColor(context.resources.getColor(R.color.bg_purple))
                                    textNickname.setTextColor(context.resources.getColor(R.color.bg_purple))
                                }

                                textNickname.text = messageBean.replyMsg?.sender?.senderName
                                var stringBuilder: StringBuilder = StringBuilder()
                                textQuestion.text = stringBuilder.append(": ")
                                    .append(messageBean.replyMsg?.questionContent?.textContent?.text)
                                linearLayoutContent.addView(view)

                            }
                            "audio" -> {

                                val lp = LayoutParams(
                                    LayoutParams.MATCH_PARENT,
                                    LayoutParams.WRAP_CONTENT
                                )
                                val inflater = LayoutInflater.from(context)
                                val view: View = inflater.inflate(
                                    R.layout.group_message_type_reward_me_already_reply_record,
                                    null
                                )
                                view.layoutParams = lp
                                val recordReply: GroupMessageRecordItem =
                                    view.findViewById(R.id.record)
                                val textQuestion: AppCompatTextView =
                                    view.findViewById(R.id.tv_question)
                                val imgReplyFlag: ImageView = view.findViewById(R.id.img_reply)
                                imgReplyFlag.setImageResource(R.drawable.icon_huida_normal)
                                val textNickname: AppCompatTextView =
                                    view.findViewById(R.id.tv_nickname)
                                //群主（我）的回复消息
                                // TODO: 2021/8/17  录音
                                //拼接打赏的提问消息
                                if (messageBean.replyMsg?.sender?.senderId == userId) {
                                    textQuestion.setTextColor(context.resources.getColor(R.color.bg_origin))
                                    textNickname.setTextColor(context.resources.getColor(R.color.bg_origin))
                                } else {
                                    textQuestion.setTextColor(context.resources.getColor(R.color.text_color_gray)) //回复其他人为灰色
                                    textNickname.setTextColor(context.resources.getColor(R.color.text_color_gray)) //回复其他人为灰色
                                }
                                if (messageBean.replyMsg?.questionContent?.isPublic == false) {
                                    textQuestion.setTextColor(context.resources.getColor(R.color.bg_purple))
                                    textNickname.setTextColor(context.resources.getColor(R.color.bg_purple))
                                }

                                textNickname.text = messageBean.replyMsg?.sender?.senderName
                                var stringBuilder: StringBuilder = StringBuilder()
                                textQuestion.text = stringBuilder.append(": ")
                                    .append(messageBean.replyMsg?.questionContent?.textContent?.text)
                                linearLayoutContent.addView(view)
                            }
                            else -> {
                                val string: StringBuilder =
                                    StringBuilder("回复的reward类型不是指定的img，audio和text").append(
                                        messageBean.replyMsg?.questionContent?.contentType
                                    )
                                ToastUtils.showToast(context, string.toString(), 0)
                            }
                        }
                    } else {
                        if (BuildConfig.DEBUG)
                            ToastUtils.showToast(
                                context,
                                "其他类型2" + messageBean.replyMsg?.msgType,
                                0
                            )
                    }

                } else if (messageBean.replyMsg?.msgType == "img") {
                    //回复别人为图片 这种情形为大V用图片回复其他人的text
                    if (messageBean.replyMsg?.msgType == "text") {
                        val lp = LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT
                        )
                        val inflater3 = LayoutInflater.from(context)
                        val view: View = inflater3.inflate(
                            R.layout.group_message_type_normal_me_replygroupmember_img,
                            null
                        )
                        view.layoutParams = lp
                        val imgReply: AppCompatImageView =
                            view.findViewById(R.id.tv_message_normal_replygroupmember_img)
                        val textQuestion: AppCompatTextView =
                            view.findViewById(R.id.tv_message_normal_needreply_text)
                        val textNickname: AppCompatTextView =
                            view.findViewById(R.id.tv_nickname)
                        messageBean.imgContent?.url?.let {
                            GlideUtils.loadRoundCornerUrlImage2(
                                context,
                                it,
                                imgReply,
                                8,
                                setImgWidth(messageBean.imgContent!!.width),
                                setImgHeight(messageBean.imgContent!!.height)
                            )
                        }
                        textNickname.text = messageBean.replyMsg?.sender?.senderName
                        textQuestion.text = messageBean.replyMsg?.textContent?.text
                        if (messageBean.replyMsg?.sender?.senderId == userId) {
                            //如果是回复自己，则自己的内容字体为金色
                            textQuestion.setTextColor(resources.getColor(R.color.message_textColor_me))
                            textNickname.setTextColor(resources.getColor(R.color.message_textColor_me))
                        } else {
                            textQuestion.setTextColor(resources.getColor(R.color.frame_textview_private))
                            textNickname.setTextColor(resources.getColor(R.color.frame_textview_private))
                        }
                        linearLayoutContent.addView(view)
                    } else {
                        if (BuildConfig.DEBUG)
                            ToastUtils.showToast(context, "图片只能回复文本！！", 0)
                    }

                } else if (messageBean.replyMsg?.msgType == "audio") {
                    if (messageBean.replyMsg?.msgType == "text") {
                        val lp = LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT
                        )
                        val inflater3 = LayoutInflater.from(context)
                        val view: View = inflater3.inflate(
                            R.layout.group_message_type_normal_me_replygroupmember_record,
                            null
                        )
                        view.layoutParams = lp
                        val audioReply: GroupMessageRecordItem =
                            view.findViewById(R.id.tv_message_normal_replygroupmember_record)
                        val textQuestion: AppCompatTextView =
                            view.findViewById(R.id.tv_message_normal_needreply_text)
                        val textNickname: AppCompatTextView = view.findViewById(R.id.tv_nickname)
                        // TODO: 2021/8/19 录音
                        textQuestion.text = messageBean.replyMsg?.textContent?.text
                        if (messageBean.replyMsg?.sender?.senderId == userId) {
                            //如果是回复自己，则自己的内容字体为金色
                            textQuestion.setTextColor(resources.getColor(R.color.message_textColor_me))
                            textNickname.setTextColor(resources.getColor(R.color.message_textColor_me))
                        } else {
                            textQuestion.setTextColor(resources.getColor(R.color.frame_textview_private))
                            textNickname.setTextColor(resources.getColor(R.color.frame_textview_private))
                        }
                        linearLayoutContent.addView(view)
                    } else {
                        if (BuildConfig.DEBUG)
                            ToastUtils.showToast(
                                context, "语音只能回复文本！！" + messageBean.replyMsg?.msgType, 0
                            )
                    }
                } else {
                    if (BuildConfig.DEBUG)
                        ToastUtils.showToast(context, "其他类型！！$messageBean.msgType", 0)
                }
            }
            MessageType.ERROR_TYPE -> {
                if (BuildConfig.DEBUG)
                    ToastUtils.showToast(context, "错误类型！！$messageBean", 0)
            }
        }
        invalidate()
    }

    private fun setImgWidth2(width: Int?, a: Int): Int {
        val minWidth: Int? =
            width?.let { min(it, resources.displayMetrics.widthPixels * 0.8.toInt()) - a }
        return minWidth!!
    }


    private fun setImgHeight(height: Int?): Int {
        val minHeight: Int? =
            height?.let { min(it, resources.displayMetrics.widthPixels * 0.8.toInt()) }
        return minHeight!!
    }

    private fun setImgHeight2(height: Int?, b: Int): Int {
        val minHeight: Int? =
            height?.let { min(it, resources.displayMetrics.widthPixels * 0.8.toInt()) - b }
        return minHeight!!
    }


    private fun setImgWidth(width: Int?): Int {
        val minWidth: Int? =
            width?.let { min(it, resources.displayMetrics.widthPixels * 0.8.toInt()) }
        return minWidth!!
    }


    private fun addTitleView(userId: Int, messageBean: MessageBean): GroupMessageItemTitle {
        val groupMessageItemTitle = GroupMessageItemTitle(context, null, 0)
        groupMessageItemTitle.setData(userId, messageBean)
        return groupMessageItemTitle
    }

    private fun addBottomView(messageBean: MessageBean): GroupRewardOwnerMeItem {
        val groupRewardOwnerMeItem = GroupRewardOwnerMeItem(context, null, 0)
        groupRewardOwnerMeItem.setData(messageBean)
        return groupRewardOwnerMeItem
    }


}