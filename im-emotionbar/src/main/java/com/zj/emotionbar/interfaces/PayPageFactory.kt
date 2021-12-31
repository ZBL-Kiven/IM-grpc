package com.zj.emotionbar.interfaces

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.zj.emotionbar.data.Emoticon
import com.zj.emotionbar.data.EmoticonPack
import com.zj.emotionbar.widget.PayPageView

/**
 * @author: JayQiu
 * @date: 2021/12/9
 * @description:
 */
class PayPageFactory<T : EmoticonPack<O>, O : Emoticon> : PageFactory<T, O> {
    override fun create(context: Context, pack: T, clickListener: OnEmoticonClickListener<O>?, payClickListener: OnPayClickListener<T>?, retryClickListener: OnRetryClickListener<T>?): View {
        val pageView = PayPageView(context, null, 0) {
            payClickListener?.onPayClick(pack)
        }
        when (pack.status) {
            EmoticonPack.EmoticonStatus.LOADING -> {
                pageView.showLoading()
            }
            EmoticonPack.EmoticonStatus.ERROR -> {
                pageView.showError()
                pageView.setRetryOnClickListener { retryClickListener?.onRetryClick(pack) }
            }
            else -> {
                pageView.showData(pack.price)
            }
        }

        pageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        return pageView
    }
}