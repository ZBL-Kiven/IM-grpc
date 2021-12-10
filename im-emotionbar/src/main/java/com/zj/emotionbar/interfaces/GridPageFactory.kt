package com.zj.emotionbar.interfaces

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zj.emotionbar.R
import com.zj.emotionbar.data.Emoticon
import com.zj.emotionbar.data.EmoticonPack
import com.zj.emotionbar.utils.imageloader.GlideLoader
import com.zj.emotionbar.utils.imageloader.ImageLoader

open class GridPageFactory<T : EmoticonPack<O>, O : Emoticon> : PageFactory<T, O> {

    override fun create(context: Context, pack: T, clickListener: OnEmoticonClickListener<Emoticon>?, payClickListener: OnPayClickListener<EmoticonPack<Emoticon>>?): View {
        val pageView = RecyclerView(context)
        val lm = GridLayoutManager(context, 5)
        pageView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        pageView.layoutManager = lm
        pageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val adapter = createAdapter(context, pack.emoticons, clickListener)
        pageView.adapter = adapter
        return pageView
    }

    open fun <T : Emoticon> createAdapter(context: Context, emoticons: List<T>, clickListener: OnEmoticonClickListener<Emoticon>?): RecyclerView.Adapter<*> {
        return ImageAdapter(context, emoticons, clickListener)
    }
}

class ImageAdapter<T : Emoticon>(context: Context, private val emoticons: List<T>, private val clickListener: OnEmoticonClickListener<Emoticon>?) : RecyclerView.Adapter<ImageAdapter.ImgViewHolder>() {
    private val imageHeight = (Resources.getSystem().displayMetrics.density * 24f + 0.5f).toInt()
    private val defaultItemSize = context.resources.getDimension(R.dimen.item_emoticon_size_default).toInt()
    private val padding = context.resources.getDimension(R.dimen.item_emoticon_padding).toInt()

    override fun getItemCount(): Int {
        return emoticons.size
    }

    private fun getItem(position: Int): Emoticon = emoticons[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImgViewHolder {
        val iv = ImageView(parent.context)
        val lp = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, defaultItemSize)
        iv.setPadding(0, padding, 0, padding)
        iv.layoutParams = lp
        return ImgViewHolder(iv)
    }

    override fun onBindViewHolder(holder: ImgViewHolder, position: Int) {
        val image = holder.itemView as ImageView
        val data = getItem(position)
        val uri = data.uri
        if (uri != null) GlideLoader.displayImage(uri, image)
        holder.itemView.setOnClickListener {
            clickListener?.onEmoticonClick(data, it)
        }
    }

    class ImgViewHolder(v: View) : RecyclerView.ViewHolder(v)
}