package com.zj.ccIm.core.bean

import java.lang.IllegalStateException

data class ChangeBalanceRespEn(val type: String? = null, val method: String? = null, val num: Int = 0) {

    fun getNum(): AssetsChanged {
        val value = if (method == "add") num else if (method == "sub") -num else throw IllegalStateException("ChangeBalanceRespEn must typed a method with 'add' or 'sub' only!")
        var diamond = 0
        var sparks = 0
        when (type) {
            "diamond" -> diamond = value
            "spark" -> sparks = value
            else -> throw IllegalStateException("ChangeBalanceRespEn must be one on the type with 'diamond' and 'spark' yet")
        }
        return AssetsChanged(sparks, diamond)
    }
}