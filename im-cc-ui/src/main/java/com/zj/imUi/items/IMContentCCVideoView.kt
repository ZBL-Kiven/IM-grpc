package com.zj.imUi.items

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.zj.imUi.R
import com.zj.imUi.base.BaseBubble
import com.zj.imUi.base.BaseImItem
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imUi.utils.MessageSendTimeUtils
import com.zj.imUi.utils.RewardTimeCountdownUtils
import com.zj.imUi.utils.TimeDiffUtils
import com.zj.views.ut.DPUtils
import java.lang.StringBuilder

class IMContentCCVideoView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0) : BaseBubble(context, attrs, def) ,MessageSendTimeUtils.SendTImeListener{


    private var tvName: AppCompatTextView
    private var imgOwnerFlag: AppCompatImageView
    private var llTitle: LinearLayout
    private var imgCCVideoCover: AppCompatImageView
    private var tvCCVideoTitle: AppCompatTextView
    private var tvCCVideoSendTime: AppCompatTextView

    private var contentLayout: View = LayoutInflater.from(context).inflate(R.layout.im_msg_item_normal_cc_video, this, false)

    init {
        with(contentLayout) {
            tvName = findViewById(R.id.im_msg_item_normal_cc_video_tv_nickname)
            imgOwnerFlag = findViewById(R.id.im_msg_item_normal_cc_video_img_owner)
            llTitle = findViewById(R.id.im_msg_item_normal_cc_video_ll_title)
            imgCCVideoCover = findViewById(R.id.im_msg_item_normal_cc_video_img_cover)
            tvCCVideoTitle = findViewById(R.id.im_msg_item_normal_cc_video_tv_title)
            tvCCVideoSendTime = findViewById(R.id.im_msg_item_normal_cc_video_tv_time)
        }
    }

    override fun init(data: ImMsgIn) {
        if (childCount == 0) {
            addView(contentLayout)
        }
        onSetData(data)

        imgCCVideoCover.setOnClickListener {
            data.jumpToOwnerHomePage() //跳转大V作品详情页
        }
    }

    private fun onSetData(data: ImMsgIn?) {
        if (data == null) return

        performRegisterTimer()

        if (data.getSelfUserId() != data.getSenderId()) {
            llTitle.visibility = View.VISIBLE
            tvName.text = data.getSenderName()
        } else llTitle.visibility = View.GONE

        val corners = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, context.resources.displayMetrics).toInt()
        data.getCCVideoContentImgPreviewRemoteStorageUrl()?.let {
            Glide.with(this).load(it).centerInside().override(DPUtils.dp2px(256f), DPUtils.dp2px(160f)).apply(RequestOptions.bitmapTransform(RoundedCorners(corners))).into(imgCCVideoCover)
        }

        data.getCCVideoContentVideoTitle()?.let {
            tvCCVideoTitle.visibility = View.VISIBLE
            tvCCVideoTitle.text = data.getCCVideoContentVideoTitle()
            if (data.getSelfUserId() == data.getSenderId()) {
                tvCCVideoTitle.setTextColor(ContextCompat.getColor(context, R.color.text_color_white))
            } else tvCCVideoTitle.setTextColor(ContextCompat.getColor(context, R.color.text_color_black))

        }
        if (data.getSendTime() in 1..3600000 * 48) {
            tvCCVideoSendTime.visibility = View.VISIBLE
            tvCCVideoSendTime.text = (TimeDiffUtils.timeDifference(data.getSendTime()))?.let { setTimeText(it) }
        } else tvCCVideoSendTime.visibility = View.GONE

    }

    private fun setTimeText(sendTime: Long): CharSequence? {
        return if (sendTime in 60000..3599999) {
            StringBuilder(timeParse(sendTime)).append(" ").append(context.getString(R.string.im_ui_min_ago))
        } else if (sendTime > 3600000 && sendTime < 3600000 * 48) {
            StringBuilder(timeParseHour(sendTime)).append(" ").append(context.getString(R.string.im_ui_hours_ago))
        } else null
    }

    private fun timeParseHour(duration: Long): String {
        val time: String?
        val hour = duration / 3600000
        time = hour.toString()
        return time
    }

    private fun timeParse(duration: Long): String {
        val minute = duration / 60000
        return minute.toString()
    }


    override fun onSendTime(msgId: String, sendTime: Long) {
        if (msgId == this.curData?.invoke()?.getMsgId()) { this.curData?.invoke()?.let { tvCCVideoSendTime.text = setTimeText(sendTime) } }
    }

    override fun onResume() {
        performRegisterTimer()
    }

    override fun notifyChange(pl: Any?) {
        super.notifyChange(pl)
        when (pl) {
            BaseImItem.NOTIFY_CHANGE_SENDING_STATE->performRegisterTimer()
        }
    }

    private fun performRegisterTimer() {
        curData?.invoke()?.let {
            if (it.getSendState() == 0 || it.getSendState() == 3) {
                TimeDiffUtils.timeDifference(it.getSendTime())?.let { it1 ->
                    MessageSendTimeUtils.registerSendTimeObserver(
                        it.getMsgId(),
                        it1, this
                    )
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