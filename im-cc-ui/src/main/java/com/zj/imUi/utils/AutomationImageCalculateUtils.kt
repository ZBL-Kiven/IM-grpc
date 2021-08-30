package com.zj.imUi.utils

import kotlin.math.min


/**
 * Calculate the size of the image ,
 * Limited to the minimum + maximum rules,
 * support unit testing，@see #UT
 * */
object AutomationImageCalculateUtils {

    fun proportionalWH(originWidth: Int, originHeight: Int, maxWidth: Int, maxHeight: Int, minScale: Float): Array<Int> {
        val minWidth = (maxWidth * minScale).toInt()
        val minHeight = (maxHeight * minScale).toInt()
        var width: Int = originWidth
        var height: Int = originHeight

        if (width > maxWidth) {
            val maxWOffset = width * 1.0f / maxWidth
            width = maxWidth
            height = (height / maxWOffset).toInt()
        }
        if (height > maxHeight) {
            val maxHOffset = height * 1.0f / maxHeight
            height = maxHeight
            width = (width / maxHOffset).toInt()
        }
        if (width < minWidth) {
            val minWOffset = width * 1.0f / minWidth
            width = minWidth
            height = (height / minWOffset).toInt()
        }
        if (height < minHeight) {
            val minHOffset = height * 1.0f / minHeight
            height = minHeight
            width = (width / minHOffset).toInt()
        }
        return arrayOf(min(maxWidth, width), min(maxHeight, height))
    }
}