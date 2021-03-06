package com.zj.imUi.items

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.zj.imUi.R
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.views.ut.DPUtils

/**
 * author: 李 祥
 * date:   2021/9/23 5:57 下午
 * description:
 */
class IMContentCCVideoWidgetVIew @JvmOverloads constructor(context: Context,
    attrs: AttributeSet? = null,
    def: Int = 0) : RelativeLayout(context, attrs, def),ImContentIn  {



    private var imgCCVideoCover: AppCompatImageView
    private var imgJumpFlag: AppCompatImageView
    private var ccVideoProgressBar: ProgressBar
        init {
            LayoutInflater.from(context).inflate(R.layout.im_msg_item_normal_cc_video_widget,this,true)
            imgCCVideoCover = findViewById(R.id.im_msg_item_normal_cc_video_widget_img_cover)
            imgJumpFlag = findViewById(R.id.im_msg_item_normal_cc_video_widget_img_flag)
            ccVideoProgressBar = findViewById(R.id.im_msg_item_cc_video_widget_img_progressbar)
        }

    @SuppressLint("CheckResult")
    override fun onSetData(data: ImMsgIn?) {

        imgCCVideoCover.setOnClickListener {
            data?.jumpToVideoDetails() //跳转大V作品视频播放
        }

        if (data == null) return

        imgJumpFlag.visibility = View.GONE
        data.getReplyMsgCCVideoCoverContent()?.let {
            Glide.with(this).load(it).override(DPUtils.dp2px(44f), DPUtils.dp2px(44f)).centerCrop()
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean { return false }
                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        imgJumpFlag.visibility = View.VISIBLE
                        ccVideoProgressBar.visibility = View.GONE
                        return false }
                }).into(imgCCVideoCover)
        }
    }

    override fun chatType(chatType:Any) {
    }

    override fun onResume(data: ImMsgIn?) {
    }

    override fun onStop(data: ImMsgIn?) {
    }

    override fun onDestroy(data: ImMsgIn?) {
    }

}