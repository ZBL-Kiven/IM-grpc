package com.zj.emotionbar.interfaces

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zj.emotionbar.data.Emoticon
import com.zj.emotionbar.data.EmoticonPack
import com.zj.emotionbar.utils.EmoticonsKeyboardUtils.dip2px
import com.zj.emotionbar.utils.imageloader.GlideLoader
import com.zj.emotionbar.widget.GridPageView
import com.zj.emotionbar.widget.GridSpacingItemDecoration

open class GridPageFactory<T : EmoticonPack<E>, E : Emoticon> : PageFactory<T, E> {

    override fun create(context: Context, pack: T, clickListener: OnEmoticonClickListener<E>?, payClickListener: OnPayClickListener<T>?, retryClickListener: OnRetryClickListener<T>?): View {
        val gridPageView = GridPageView(context)
        when (pack.status) {
            EmoticonPack.EmoticonStatus.LOADING -> {
                gridPageView.showLoading()
            }
            EmoticonPack.EmoticonStatus.ERROR -> {
                gridPageView.showError()
            }
            else -> {
                if (EmoticonPack.EmoticonType.PAY.type == pack.type) {
                    gridPageView.showPrice(pack.price)

                }
                gridPageView.showData()
                val pageView = gridPageView.getRecyclerView()
                val lm = GridLayoutManager(context, 5)
                pageView?.addItemDecoration(GridSpacingItemDecoration(5, dip2px(context, 12f), false))
                pageView?.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                pageView?.layoutManager = lm
                val adapter = createAdapter(pack.emoticons ?: mutableListOf(), clickListener)
                pageView?.adapter = adapter
            }

        }
        gridPageView?.onGridPageClickListener = object : GridPageClickListener {
            override fun onPayClickListener() {
                payClickListener?.onPayClick(pack)
            }

            override fun onRetryClickListener() {
                retryClickListener?.onRetryClick(pack)
            }
        }
        return gridPageView
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