package com.zj.emotionbar.interfaces

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zj.emotionbar.data.Emoticon
import com.zj.emotionbar.data.EmoticonPack
import com.zj.emotionbar.utils.EmoticonsKeyboardUtils.dip2px
import com.zj.emotionbar.utils.imageloader.GlideLoader
import com.zj.emotionbar.widget.GridSpacingItemDecoration

open class GridPageFactory<T : EmoticonPack<E>, E : Emoticon> : PageFactory<T, E> {

    override fun create(context: Context, pack: T, clickListener: OnEmoticonClickListener<E>?, payClickListener: OnPayClickListener<T>?): View {
        val pageView = RecyclerView(context)
        val lm = GridLayoutManager(context, 5)
        pageView.addItemDecoration(GridSpacingItemDecoration(5, dip2px(context, 12f), false))
        pageView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        pageView.layoutManager = lm
        pageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        pageView.setPadding(dip2px(context, 12f), 0, dip2px(context, 12f), 0)
        val adapter = createAdapter(pack.emoticons?: mutableListOf(), clickListener)
        pageView.adapter = adapter
        return pageView
    }

    open fun createAdapter(emoticons: MutableList<E>, clickListener: OnEmoticonClickListener<E>?): RecyclerView.Adapter<*> {
        return ImageAdapter(emoticons, clickListener)
    }
}

class ImageAdapter<E : Emoticon>(private val emoticons: List<E>, private val clickListener: OnEmoticonClickListener<E>?) : RecyclerView.Adapter<ImageAdapter.ImgViewHolder>() {

    override fun getItemCount(): Int {
        return emoticons.size
    }

    private fun getItem(position: Int): E {
        return emoticons[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImgViewHolder {
        val iv = ImageView(parent.context)
        val lp = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        iv.layoutParams = lp
        return ImgViewHolder(iv)
    }

    override fun onBindViewHolder(holder: ImgViewHolder, position: Int) {
        val image = holder.itemView as ImageView
        val data = getItem(position)
        val uri = data.icon
        if (uri != null) GlideLoader.displayImage(uri, image)
        holder.itemView.setOnClickListener {
            clickListener?.onEmoticonClick(data, it)
        }
    }

    class ImgViewHolder(v: View) : RecyclerView.ViewHolder(v)
}