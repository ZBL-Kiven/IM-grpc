package com.zj.ccIm.core.db

import com.zj.ccIm.CcIM
import com.zj.ccIm.core.bean.AssetsChanged

object AssetsChangedOperator {

    fun onAssetsChanged(callId: String?, diamondNum: Int?, sparkNum: Int?) {
        if (sparkNum != null || diamondNum != null) {
            CcIM.postToUiObservers(AssetsChanged(sparkNum, diamondNum), callId)
        }
    }
}