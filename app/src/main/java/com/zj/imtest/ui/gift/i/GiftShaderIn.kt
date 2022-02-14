package com.zj.imtest.ui.gift.i

interface GiftShaderIn : GiftInfoIn {

    fun getUniqueId(): Int

    fun getSource(): String?
}