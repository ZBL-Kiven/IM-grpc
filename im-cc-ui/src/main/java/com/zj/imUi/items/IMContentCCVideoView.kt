package com.zj.imUi.items

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.zj.imUi.base.BaseBubble
import com.zj.imUi.base.BaseImItem
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imUi.utils.MessageSendTimeUtils
import com.zj.imUi.utils.TimeDiffUtils
import com.zj.views.ut.DPUtils

import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.zj.imUi.R


class IMContentCCVideoView @JvmOverloads constructor(context: Context,
    attrs: AttributeSet? = null,
    def: Int = 0) : BaseBubble(context, attrs, def), MessageSendTimeUtils.SendTImeListener {


    private var tvName: AppCompatTextView
    private var imgOwnerFlag: AppCompatImageView
    private var llTitle: LinearLayout
    private var imgCCVideoCover: AppCompatImageView
    private var imgJumpFlag: AppCompatImageView
    private var tvCCVideoTitle: AppCompatTextView
    private var tvCCVideoSendTime: AppCompatTextView
    private var ccVideoProgressBar: ProgressBar

    @SuppressLint("ObjectAnimatorBinding") //    private var anim: ObjectAnimator = ObjectAnimator.ofInt(this, "ImageLevel", 0, 10000)

    private var contentLayout: View =
        LayoutInflater.from(context).inflate(R.layout.im_msg_item_normal_cc_video, this, false)

    init {
        with(contentLayout) {
            tvName = findViewById(R.id.im_msg_item_normal_cc_video_tv_nickname)
            imgOwnerFlag = findViewById(R.id.im_msg_item_normal_cc_video_img_owner)
            llTitle = findViewById(R.id.im_msg_item_normal_cc_video_ll_title)
            imgCCVideoCover = findViewById(R.id.im_msg_item_normal_cc_video_img_cover)
            tvCCVideoTitle = findViewById(R.id.im_msg_item_normal_cc_video_tv_title)
            tvCCVideoSendTime = findViewById(R.id.im_msg_item_normal_cc_video_tv_time)
            imgJumpFlag = findViewById(R.id.im_msg_item_normal_cc_video_img_flag)
            ccVideoProgressBar = findViewById(R.id.im_msg_item_cc_video_img_progressbar)
        } //        anim.duration = 800
        //        anim.repeatCount = ObjectAnimator.INFINITE
        //        anim.start()
        //        imgCCVideoCover.scaleType = ImageView.ScaleType.CENTER_INSIDE
    }

    override fun init(data: ImMsgIn) {
        if (childCount == 0) {
            addView(contentLayout)
        }
        onSetData(data)

        imgCCVideoCover.setOnClickListener {
            data.jumpToVideoDetails() //跳转大V作品视频播放
        }
    }

    @SuppressLint("CheckResult")
    private fun onSetData(data: ImMsgIn?) {
        if (data == null) return

        imgJumpFlag.visibility = View.GONE
        performRegisterTimer()

        if (data.getSelfUserId() != data.getSenderId()) {
            llTitle.visibility = View.VISIBLE
            tvName.text = data.getSenderName()
        } else llTitle.visibility = View.GONE

        val corners = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            8f,
            context.resources.displayMetrics).toInt()
        val roundOptions = RequestOptions().transform(RoundedCorners(corners))
        roundOptions.transform(CenterCrop(), RoundedCorners(corners)) //处理CenterCrop的情况,保证圆角不失效

        data.getCCVideoContentImgPreviewRemoteStorageUrl()?.let {
            Glide.with(this).load(it).override(DPUtils.dp2px(256f), DPUtils.dp2px(160f))
                .apply(roundOptions).addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onResourceReady(resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean): Boolean {
                    imgJumpFlag.visibility = View.VISIBLE
                    ccVideoProgressBar.visibility = View.GONE
                    return false
                }
            }).into(imgCCVideoCover)
        }

        data.getCCVideoContentVideoTitle()?.let {
            tvCCVideoTitle.visibility = View.VISIBLE
            tvCCVideoTitle.text = data.getCCVideoContentVideoTitle()
            if (data.getSelfUserId() == data.getSenderId()) {
                tvCCVideoTitle.setTextColor(ContextCompat.getColor(context,
                    R.color.text_color_white))
            } else tvCCVideoTitle.setTextColor(ContextCompat.getColor(context,
                R.color.text_color_black))

        }
        val timeDiff: Long? = TimeDiffUtils.timeDifference(data.getSendTime())
        if (timeDiff != null) {
            if (timeDiff > 1 && timeDiff < 3600000 * 48) {
                tvCCVideoSendTime.visibility = View.VISIBLE
                tvCCVideoSendTime.text = (TimeDiffUtils.timeDifference(data.getSendTime()))?.let {
                    TimeDiffUtils.setTimeText(it,
                        context)
                }
            } else tvCCVideoSendTime.visibility = View.GONE
        }
        if(data.getSelfUserId() == data.getSenderId()){
            tvCCVideoSendTime.setTextColor(ContextCompat.getColor(context,R.color.text_color_self_reply_others))
        }else
            tvCCVideoSendTime.setTextColor(ContextCompat.getColor(context,R.color.message_cc_video_send_time))


    }

    override fun onSendTime(msgId: String, sendTime: Long) {
        if (msgId == this.curData?.invoke()?.getMsgId()) {
            this.curData?.invoke()
                ?.let { tvCCVideoSendTime.text = TimeDiffUtils.setTimeText(sendTime, context) }
        }
    }

    override fun onResume() {
        performRegisterTimer()
    }

    override fun notifyChange(pl: Any?) {
        super.notifyChange(pl)
        when (pl) {
            BaseImItem.NOTIFY_CHANGE_SENDING_STATE -> performRegisterTimer()
        }
    }

    private fun performRegisterTimer() {
        curData?.invoke()?.let {
            if (it.getSendState() == 0 || it.getSendState() == 3) {
                TimeDiffUtils.timeDifference(it.getSendTime())?.let { it1 ->
                    MessageSendTimeUtils.registerSendTimeObserver(it.getMsgId(), it1, this)
                }
            } else {
                MessageSendTimeUtils.unRegisterSendTImeObserver(it.getMsgId())
            }
        }
    }

    override fun onStop() {
        curData?.invoke()?.let { MessageSendTimeUtils.unRegisterSendTImeObserver(it.getMsgId()) }
    }

    override fun onDestroy() {
        removeAllViews()
    }

}