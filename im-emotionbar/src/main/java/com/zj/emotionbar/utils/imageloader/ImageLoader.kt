package com.zj.emotionbar.utils.imageloader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import com.zj.emotionbar.utils.UriType
import com.zj.emotionbar.utils.UriUtils
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Executors

object ImageLoader: IImageLoader {
    override fun displayImage(uri: String, imageView: ImageView) {
        loader.displayImage(uri, imageView)
    }

    private var loader: IImageLoader = DefaultImageLoader()
}

class DefaultImageLoader: IImageLoader {

    private val tag = "DefaultImageLoader"
    private val pool = Executors.newFixedThreadPool(5)

    override fun displayImage(uri: String, imageView: ImageView) {
        when(UriUtils.getUriType(uri)) {
            UriType.DRAWABLE -> displayFromDrawable(imageView, uri)
            UriType.FILE -> displayFromFile(uri, imageView)
            UriType.ASSETS -> displayFromAssets(uri, imageView)
            UriType.OTHER -> displayFromWeb(uri, imageView)
        }
    }

    private fun displayFromDrawable(imageView: ImageView, uri: String) {
        val drawableIdString = UriUtils.getResourceID(imageView.context, uri)

        var resID = imageView.context.resources.getIdentifier(drawableIdString, "mipmap", imageView.context.packageName)
        if (resID <= 0) {
            resID = imageView.context.resources.getIdentifier(drawableIdString, "drawable", imageView.context.packageName)
        }
        if (resID > 0) {
            imageView.setImageResource(resID)
        }
    }

    private fun displayFromFile(uri: String, imageView: ImageView) {
        val filePath = UriUtils.getFilePath(uri) ?: return

        val file = File(filePath)
        if (!file.exists()) {
            return
        }

        try {
            imageView.setImageBitmap(BitmapFactory.decodeFile(filePath))
        } catch (e: Exception) {
            Log.e(tag, e.message, e)
            return
        }
    }

    private fun displayFromAssets(uri: String, imageView: ImageView) {
        val filePath = UriUtils.getAssetsPath(uri) ?: return

        try {
            val bitmap = BitmapFactory.decodeStream(imageView.context.assets.open(filePath))
            imageView.setImageBitmap(bitmap)
        } catch (e: IOException) {
            Log.e(tag, e.message, e)
            return
        }
    }

    private fun displayFromWeb(uri: String, imageView: ImageView) {
        pool.submit {
            val bitmap = getBitMap(uri) ?: return@submit
            imageView.post {
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    private fun getBitMap(url: String): Bitmap? {
        var myFileUrl: URL? = null
        var bitmap: Bitmap? = null
        try {
            myFileUrl = URL(url)
        } catch (e: MalformedURLException) {
            Log.e(tag, e.message, e)
        }

        try {
            val conn = myFileUrl!!
                    .openConnection() as HttpURLConnection
            conn.doInput = true
            conn.connect()
            val inputStream = conn.inputStream
            bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

        } catch (e: IOException) {
            Log.e(tag, e.message, e)
        }

        return bitmap
    }
}