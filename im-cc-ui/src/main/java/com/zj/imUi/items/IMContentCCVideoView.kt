package com.zj.imUi.items

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.zj.imUi.R
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.imUi.utils.AutomationImageCalculateUtils
import com.zj.views.ut.DPUtils
import java.lang.StringBuilder

class IMContentCCVideoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    def: Int = 0
) : LinearLayout(context, attrs, def) {


    private var tvName: AppCompatTextView
    private var imgOwnerFlag: AppCompatImageView
    private var llTitle: LinearLayout
    private var imgCCVideoCover: AppCompatImageView
    private var tvCCVideoTitle: AppCompatTextView
    private var tvCCVideoSendTime: AppCompatTextView


    init {
        LayoutInflater.from(context).inflate(R.layout.im_msg_item_normal_cc_video, this, true)
        tvName = findViewById(R.id.im_msg_item_normal_cc_video_tv_nickname)
        imgOwnerFlag = findViewById(R.id.im_msg_item_normal_cc_video_img_owner)
        llTitle = findViewById(R.id.im_msg_item_normal_cc_video_ll_title)
        imgCCVideoCover = findViewById(R.id.im_msg_item_normal_cc_video_img_cover)
        tvCCVideoTitle = findViewById(R.id.im_msg_item_normal_cc_video_tv_title)
        tvCCVideoSendTime = findViewById(R.id.im_msg_item_normal_cc_video_tv_time)
    }

    fun onSetData(data: ImMsgIn?) {
        if (data == null) return

        if (data.getSelfUserId() != data.getSenderId()) {
            llTitle.visibility = View.VISIBLE
            tvName.text = data.getSenderName()
        } else llTitle.visibility = View.GONE

        val corners = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            8f,
            context.resources.displayMetrics
        ).toInt()
        data.getCCVideoContentImgPreviewRemoteStorageUrl()?.let {
            Glide.with(this).load(it).centerInside()
                .override(DPUtils.dp2px(256f), DPUtils.dp2px(160f))
                .apply(RequestOptions.bitmapTransform(RoundedCorners(corners)))
                .into(imgCCVideoCover)
        }

        data.getCCVideoContentVideoTitle()?.let {
            tvCCVideoTitle.visibility = View.VISIBLE
            tvCCVideoTitle.text = data.getCCVideoContentVideoTitle()
        }
        tvCCVideoSendTime.text = setTimeText(data.getSendTime())

    }


    private fun setTimeText(sendTime: Long): CharSequence? {
        return if (sendTime in 60000..3599999) {
            StringBuilder(timeParse(sendTime)).append(" ")
                .append(context.getString(R.string.im_ui_min_ago))
        } else if (sendTime > 3600000 && sendTime < 3600000 * 48) {
            StringBuilder(timeParseHour(sendTime)).append(" ")
                .append(context.getString(R.string.im_ui_hours_ago))
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

}