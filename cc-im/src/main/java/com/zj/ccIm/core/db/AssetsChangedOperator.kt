package com.zj.ccIm.core.db

import com.google.gson.Gson
import com.zj.ccIm.CcIM
import com.zj.ccIm.core.bean.AssetsChanged
import com.zj.ccIm.core.bean.ChangeBalanceRespEn
import com.zj.ccIm.core.impl.ClientHubImpl

object AssetsChangedOperator {

    fun changeAssetsWithTopic(d: String?) {
        val data = Gson().fromJson(d, ChangeBalanceRespEn::class.java) ?: return
        CcIM.postToUiObservers(data.getNum(), ClientHubImpl.PAYLOAD_CHANGED)
    }

    fun onAssetsChanged(callId: String?, diamondNum: Int?, sparkNum: Int?) {
        if (sparkNum != null || diamondNum != null) {
            CcIM.postToUiObservers(AssetsChanged(sparkNum, diamondNum), callId)
        }
    }
}