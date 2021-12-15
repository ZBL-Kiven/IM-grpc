package com.zj.emotionbar.widget

import android.content.Context
import android.content.res.Resources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.zj.emotionbar.R
import com.zj.emotionbar.data.Emoticon
import com.zj.emotionbar.data.EmoticonPack
import com.zj.emotionbar.interfaces.EmoticonsToolBar
import com.zj.emotionbar.interfaces.OnToolBarItemClickListener
import com.zj.emotionbar.utils.imageloader.ImageLoader

@Suppress("unused")
open class EmotionsTabBar<E : Emoticon> @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def: Int = 0) : LinearLayout(context, attrs, def), EmoticonsToolBar<E> {

    private var recyclerView = RecyclerView(context)
    private var layoutManager: SmoothScrollLayoutManager? = null
    private var emotionPacks: List<EmoticonPack<E>>? = null
    private var leftView = FrameLayout(context)
    private var rightView = FrameLayout(context)
    private var adapterFactory: EmotionsTabAdapterFactory<E, out RecyclerView.ViewHolder>? = null
    private val tabIconMargin = (Resources.getSystem().displayMetrics.density * 13f + 0.5f).toInt()

    init {
        orientation = HORIZONTAL
        addFixView(leftView)
        addRecyclerView(context)
        addFixView(rightView)
    }

    private fun addFixView(view: View) {
        val params = LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 0f)
        params.gravity = Gravity.CENTER
        view.layoutParams = params
        addView(view)
    }

    private fun addRecyclerView(context: Context) {
        recyclerView.layoutParams = LayoutParams(0, WRAP_CONTENT, 1f)
        recyclerView.setPadding(tabIconMargin, 0, tabIconMargin, 0)
        layoutManager = SmoothScrollLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        addView(recyclerView)
        adapterFactory = DefaultAdapterFactory()
    }

    override fun setToolBarItemClickListener(listener: OnToolBarItemClickListener<E>?) {
        adapterFactory?.itemClickListeners = listener
    }

    override fun selectEmotionPack(pack: EmoticonPack<E>) {
        val position = emotionPacks?.indexOf(pack)

        val manager = layoutManager

        if (position != null && position >= 0 && manager != null) {
            val firstPosition = manager.findFirstVisibleItemPosition()
            val lastPosition = manager.findLastVisibleItemPosition()

            if (position < firstPosition) {
                manager.isMoveToTop = true
                recyclerView.smoothScrollToPosition(position)
            } else if (position > lastPosition) {
                manager.isMoveToTop = false
                recyclerView.smoothScrollToPosition(position)
            }
        }

        if (position != null) {
            adapterFactory?.onEmotionPackSelect(position)
            recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    override fun setPackList(packs: List<EmoticonPack<E>>) {
        emotionPacks = packs
        recyclerView.adapter = adapterFactory?.createAdapter(packs)
    }

    override fun addFixedToolItemView(view: View?, isRight: Boolean) {
        if (view != null) {
            val container = if (isRight) rightView else leftView

            container.addView(view)
        }
    }

    override fun notifyDataChanged() {
        recyclerView.adapter?.let {
            it.notifyItemRangeChanged(0, it.itemCount)
        }
    }

    open class DefaultAdapterFactory<E : Emoticon> : EmotionsTabAdapterFactory<E, EmotionPackTabAdapter<E>.ViewHolder> {

        override var itemClickListeners: OnToolBarItemClickListener<E>? = null
        private lateinit var packList: List<EmoticonPack<E>>

        override fun createAdapter(packs: List<EmoticonPack<E>>): RecyclerView.Adapter<out EmotionPackTabAdapter<E>.ViewHolder> {
            packList = packs
            val adapter = getAdapter(packList)
            adapter.itemClickListeners = itemClickListeners
            return adapter
        }

        protected open fun getAdapter(packs: List<EmoticonPack<E>>): EmotionPackTabAdapter<E> {
            return EmotionPackTabAdapter(packs)
        }

        override fun onEmotionPackSelect(position: Int) {
            packList.forEachIndexed { index, pair ->
                pair.tag = index == position
            }
        }
    }
}


interface EmotionsTabAdapterFactory<E : Emoticon, T : RecyclerView.ViewHolder> {
    var itemClickListeners: OnToolBarItemClickListener<E>?
    fun createAdapter(packs: List<EmoticonPack<E>>): RecyclerView.Adapter<out T>

    fun onEmotionPackSelect(position: Int)
}


/**
 * packs: MutablePair first is selected state
 */
open class EmotionPackTabAdapter<E : Emoticon>(private val packs: List<EmoticonPack<E>>) : RecyclerView.Adapter<EmotionPackTabAdapter<E>.ViewHolder>() {

    var itemClickListeners: OnToolBarItemClickListener<E>? = null

    init {
        packs.forEach {
            it.tag = false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.view_table_item, parent, false)
        return ViewHolder(itemView)
    }


    override fun getItemCount() = packs.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fus = packs[position].image ?: ""
        ImageLoader.displayImage(fus, holder.imageView)
        if (packs[position].tag == null) {
            packs[position].tag = false
        }
        val bid = if (packs[position].tag as Boolean) R.drawable.ui_bg_emo_tab_selected else R.drawable.ui_bg_emo_tab_normal
        holder.itemView.setBackgroundResource(bid)
        holder.itemView.isClickable = true
        holder.itemView.setOnClickListener {
            itemClickListeners?.onToolBarItemClick(packs[position])
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.emo_tab_item_iv_image)
    }
}