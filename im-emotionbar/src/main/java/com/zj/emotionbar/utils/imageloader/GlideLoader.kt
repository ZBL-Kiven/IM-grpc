package com.zj.emotionbar.utils.imageloader

import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.zj.emotionbar.utils.UriType
import com.zj.emotionbar.utils.UriUtils
import java.io.File

/**
 * @author: JayQiu
 * @date: 2021/12/9
 * @description:
 */
object GlideLoader : IImageLoader {
    override fun displayImage(uri: String, imageView: ImageView) {
        loader.displayImage(uri, imageView)
    }

    private var loader: IImageLoader = GlideImageLoader()

}

class GlideImageLoader : IImageLoader {
    override fun displayImage(uri: String, imageView: ImageView) {
        when (UriUtils.getUriType(uri)) {
            UriType.DRAWABLE -> displayFromDrawable(imageView, uri)
            UriType.FILE -> displayFromFile(uri, imageView)
            UriType.ASSETS -> Glide.with(imageView).load(uri).into(imageView)
            UriType.OTHER -> Glide.with(imageView).load(uri).into(imageView)
        }

    }

    private fun displayFromDrawable(imageView: ImageView, uri: String) {
        val drawableIdString = UriUtils.getResourceID(imageView.context, uri)
        var resID = imageView.context.resources.getIdentifier(drawableIdString, "mipmap", imageView.context.packageName)
        if (resID <= 0) {
            resID = imageView.context.resources.getIdentifier(drawableIdString, "drawable", imageView.context.packageName)
        }
        if (resID > 0) {
            Glide.with(imageView).load(resID).into(imageView)
        }
    }

    private fun displayFromFile(uri: String, imageView: ImageView) {
        val filePath = UriUtils.getFilePath(uri) ?: return

        val file = File(filePath)
        if (!file.exists()) {
            return
        }
        Glide.with(imageView).load(file).into(imageView)
    }
}