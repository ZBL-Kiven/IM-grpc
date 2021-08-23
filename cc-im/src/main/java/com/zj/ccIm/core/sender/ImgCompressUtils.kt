package com.zj.ccIm.core.sender

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.WorkerThread
import com.zj.ccIm.core.sender.BaseFileSender.Companion.numFormat
import com.zj.im.chat.poster.log
import java.io.ByteArrayOutputStream


object ImgCompressUtils {

    @WorkerThread
    fun compressBySampleSize(src: Bitmap?, quality: Int, sampleSize: Int, recycle: Boolean): Bitmap? {
        if (src == null || src.width == 0 || src.height == 0) return null
        val options = BitmapFactory.Options()
        options.inSampleSize = sampleSize
        val bos = ByteArrayOutputStream()
        src.compress(Bitmap.CompressFormat.JPEG, quality, bos)
        val bytes: ByteArray = bos.toByteArray()
        if (recycle && !src.isRecycled) src.recycle()
        log("Img content has been compressed to ${numFormat(bytes.size.toLong())}")
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
    }

}