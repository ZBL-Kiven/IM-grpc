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
    override fun create(context: Context, pack: T, clickListener: OnEmoticonClickListener<Emoticon>?, payClickListener: OnPayClickListener<EmoticonPack<Emoticon>>?): View {
        val pageView = PayPageView(context, null, 0) {
            payClickListener?.onPayClick(pack as EmoticonPack<Emoticon>)
        }
        pageView.showData(pack.price)
        pageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        return pageView
    }


}