package com.zj.ccIm.core.db

import com.zj.ccIm.core.IMHelper
import com.zj.ccIm.core.bean.AssetsChanged

object AssetsChangedOperator {

    fun onAssetsChanged(callId: String?, diamondNum: Int?, sparkNum: Int?) {
        if (sparkNum != null || diamondNum != null) {
            IMHelper.postToUiObservers(AssetsChanged(sparkNum, diamondNum), callId)
        }
    }
}