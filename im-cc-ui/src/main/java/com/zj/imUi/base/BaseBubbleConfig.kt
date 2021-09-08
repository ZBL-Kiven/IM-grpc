package com.zj.imUi.base

import android.widget.ImageView
import android.widget.RelativeLayout


interface BaseBubbleConfig<T> {

    fun getBubbleLayoutParams(d: T): RelativeLayout.LayoutParams

    fun getSendingLayoutParams(d: T): RelativeLayout.LayoutParams

    fun getAvatarLayoutParams(d: T): RelativeLayout.LayoutParams

    fun getTvNickNameLayoutParams(d: T): RelativeLayout.LayoutParams

    fun onLoadAvatar(iv: ImageView?, d: T)
}