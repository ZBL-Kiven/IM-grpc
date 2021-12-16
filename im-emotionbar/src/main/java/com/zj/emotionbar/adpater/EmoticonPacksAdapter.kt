package com.zj.emotionbar.adpater

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.zj.emotionbar.data.Emoticon
import com.zj.emotionbar.data.EmoticonPack
import com.zj.emotionbar.interfaces.OnEmoticonClickListener
import com.zj.emotionbar.interfaces.OnPayClickListener

/**
 * viewPager Adapter
 */
open class EmoticonPacksAdapter<E : Emoticon>(val packList: MutableList<EmoticonPack<E>>) : PagerAdapter() {

    var adapterListener: EmoticonPacksAdapterListener? = null
    private var clickListener: OnEmoticonClickListener<E>? = null
    private var payListener: OnPayClickListener<EmoticonPack<E>>? = null
    private var isUpdateAll = false

    fun setClickListener(l: OnEmoticonClickListener<E>?) {
        this.clickListener = l
    }

    fun setPayClickListener(payListener: OnPayClickListener<EmoticonPack<E>>?) {
        this.payListener = payListener
    }


    fun getEmoticonPackPosition(pack: EmoticonPack<E>): Int {
        return packList.indexOf(pack)
    }

    override fun getCount(): Int {
        return packList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val pack = packList[position]
        val view = pack.getView(container.context, pack, clickListener, payListener)
        view.tag = position
        container.addView(view)
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
        obj as View
        return if (obj.tag as Int == updatePosition) POSITION_NONE
        else POSITION_UNCHANGED
    }

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
    }

    var updatePosition = -1
    fun notifyDataIndexChanged(position: Int) {
        updatePosition = position
        notifyDataSetChanged()
    }

    interface EmoticonPacksAdapterListener {
        fun onDataSetChanged()
    }
}