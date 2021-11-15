package com.zj.imUi.items

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.zj.imUi.base.BaseBubble
import com.zj.imUi.interfaces.ImMsgIn
import com.zj.views.ut.DPUtils

import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.zj.imUi.R


class IMContentCCLiveView @JvmOverloads constructor(context: Context,
    attrs: AttributeSet? = null,
    def: Int = 0) : BaseBubble(context, attrs, def) {


    private var tvName: AppCompatTextView
    private var imgOwnerFlag: AppCompatImageView
    private var llTitle: LinearLayout
    private var imgCCLiveCover: AppCompatImageView
    private var tvCCLiveTitle: AppCompatTextView
    private var frame :FrameLayout
    private var baseMargin = DPUtils.dp2px(12f)


    private var contentLayout: View =
        LayoutInflater.from(context).inflate(R.layout.im_msg_item_normal_cc_live, this, false)

    @SuppressLint("ObjectAnimatorBinding")
    private var anim: ObjectAnimator = ObjectAnimator.ofInt(this, "ImageLevel", 0, 10000)

    init {
        with(contentLayout) {
            tvName = findViewById(R.id.im_msg_item_normal_cc_live_tv_nickname)
            imgOwnerFlag = findViewById(R.id.im_msg_item_normal_cc_live_img_owner)
            llTitle = findViewById(R.id.im_msg_item_normal_cc_live_ll_title)
            imgCCLiveCover = findViewById(R.id.im_msg_item_normal_cc_live_img_cover)
            tvCCLiveTitle = findViewById(R.id.im_msg_item_normal_cc_live_tv_title)
            frame = findViewById(R.id.im_msg_item_normal_cc_live_fl)

        }

        anim.duration = 800
        anim.repeatCount = ObjectAnimator.INFINITE
        anim.start()
    }

    override fun init(data: ImMsgIn, chatType: Any) {
        if (childCount == 0) {
            addView(contentLayout)
        }
        onSetData(data)

        imgCCLiveCover.setOnClickListener {
            data.jumpToLiveRoom() //跳转大V直播
        }

    }

    @SuppressLint("CheckResult")
    private fun onSetData(data: ImMsgIn?) {
        if (data == null) return
        if (data.getSelfUserId() != data.getSenderId()) {
            if(data.getSenderId() == data.getOwnerId()){
                imgOwnerFlag.visibility = View.VISIBLE
            }else
                imgOwnerFlag.visibility = View.GONE

            llTitle.visibility = View.VISIBLE
            tvName.text = data.getSenderName()
            val lp = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
            lp.setMargins(baseMargin,0,baseMargin,baseMargin)
            frame.layoutParams = lp
        } else {
            llTitle.visibility = View.GONE
            val lp = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
            lp.setMargins(0,0,0,0)
            frame.layoutParams = lp
        }
        val corners = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            8f,
            context.resources.displayMetrics).toInt()
        val roundOptions = RequestOptions().transform(RoundedCorners(corners))
        roundOptions.transform(CenterCrop(), RoundedCorners(corners)) //处理CenterCrop的情况,保证圆角不失效

        data.getLiveMsgCover()?.let {
            Glide.with(this).load(it).override(DPUtils.dp2px(256f), DPUtils.dp2px(226f))
                .apply(roundOptions)
                .placeholder(R.drawable.im_msg_item_img_loading)
                .error(R.drawable.im_msg_item_img_loading)
                .addListener(object : RequestListener<Drawable> {
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
                        anim.cancel()
                        return false
                    }
                }).into(imgCCLiveCover)
        }

        data.getLiveMsgIntroduce()?.let {
            tvCCLiveTitle.visibility = View.VISIBLE
            tvCCLiveTitle.text = data.getLiveMsgIntroduce()
        }

    }


    override fun onResume() {
    }
    
    override fun onStop() {
    }

    override fun onDestroy() {
        removeAllViews()
    }

}