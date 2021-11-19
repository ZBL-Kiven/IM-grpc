package com.zj.imtest.ui.data.bean

data class RiskMsg(val riskId: Int, val multilingual: Map<String, String>) {

    fun getSensitiveMsg(countryCode: String): String? {
        return multilingual[countryCode.uppercase()]
    }
}
