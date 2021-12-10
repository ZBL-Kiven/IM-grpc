package com.zj.emotionbar.adpater

import androidx.viewpager.widget.PagerAdapter
import android.view.View
import android.view.ViewGroup
import com.zj.emotionbar.R
import com.zj.emotionbar.data.Emoticon
import com.zj.emotionbar.data.EmoticonPack
import com.zj.emotionbar.interfaces.GridPageFactory
import com.zj.emotionbar.interfaces.OnEmoticonClickListener
import com.zj.emotionbar.interfaces.OnPayClickListener
import com.zj.emotionbar.interfaces.PayPageFactory

/**
 * viewPager Adapter
 */
open class EmoticonPacksAdapter(val packList: List<EmoticonPack<out Emoticon>>) : PagerAdapter() {

    var adapterListener: EmoticonPacksAdapterListener? = null
    private var clickListener: OnEmoticonClickListener<Emoticon>? = null
    private var payListener: OnPayClickListener<EmoticonPack<Emoticon>>? = null
    private var isUpdateAll = false

    fun setClickListener(l: OnEmoticonClickListener<Emoticon>?) {
        this.clickListener = l
    }

    fun setPayClickListener(payListener: OnPayClickListener<EmoticonPack<Emoticon>>?) {
        this.payListener = payListener
    }


    fun getEmoticonPackPosition(pack: EmoticonPack<out Emoticon>): Int {
        return packList.indexOf(pack)
    }

    override fun getCount(): Int {
        return packList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val pack = packList[position] as EmoticonPack<Emoticon>
        val view = pack.getView(container.context, pack, clickListener, payListener)
        view.tag = position
        container.addView(view)
        pack.isDataChanged = false
        isUpdateAll = false
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun getItemPosition(obj: Any): Int {
        return POSITION_UNCHANGED
    }

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
        adapterListener?.onDataSetChanged()
    }

    interface EmoticonPacksAdapterListener {
        fun onDataSetChanged()
    }
}