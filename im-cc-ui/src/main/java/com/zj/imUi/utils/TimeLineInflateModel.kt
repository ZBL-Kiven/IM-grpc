package com.zj.imUi.utils

import android.annotation.SuppressLint
import android.content.Context
import com.zj.imUi.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

object TimeLineInflateModel {

    private const val maximumDiffDisplayTime: Long = 2 * 60 * 1000

    fun inflateTimeLine(ctx: Context, dataTime: Long, lastTime: Long, maxDiffTimeStamp: Long = maximumDiffDisplayTime): String? {
        return if (abs(lastTime - dataTime) > maxDiffTimeStamp) {
            getTimeString(ctx, dataTime)
        } else null
    }

    @SuppressLint("StringFormatMatches")
    private fun getTimeString(ctx: Context, timestamp: Long): String {
        val result: String
        val weekNames = arrayOf(ctx.getString(R.string.im_ui_sunday), ctx.getString(R.string.im_ui_monday), ctx.getString(R.string.im_ui_tuesday), ctx.getString(R.string.im_ui_wednesday), ctx.getString(R.string.im_ui_thursday), ctx.getString(R.string.im_ui_friday), ctx.getString(R.string.im_ui_saturday))
        val hourTimeFormat = ctx.getString(R.string.im_ui_hour_time_format)
        val monthTimeFormat = ctx.getString(R.string.im_ui_month_time_format)
        val yearTimeFormat = ctx.getString(R.string.im_ui_year_time_format)
        try {
            val todayCalendar = Calendar.getInstance()
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp
            if (todayCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
                result = if (todayCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
                    when (todayCalendar.get(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH)) {

                        // 0 -> getTime(timestamp, hourTimeFormat)

                        0 -> TimeDiffUtils.timeDifference(timestamp)?.let {
                            TimeDiffUtils.setTimeText(it, ctx).toString()
                        }.toString()

                        //                        1 -> "${ctx.getString(R.string.im_ui_yesterday)} ${
                        //                            getTime(timestamp, hourTimeFormat)
                        //                        }"
                        //                        2, 3, 4, 5, 6 -> {
                        //                            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                        //                            "${weekNames[dayOfWeek - 1]} ${getTime(timestamp, hourTimeFormat)}"
                        //                        }

                        else -> getTime(timestamp, monthTimeFormat)
                    }
                } else {
                    getTime(timestamp, monthTimeFormat)
                }
            } else {
                result = getTime(timestamp, yearTimeFormat)
            }
            return result
        } catch (e: Exception) {
            return ""
        }

    }

    private fun getTime(time: Long, pattern: String): String {
        val date = Date(time)
        return dateFormat(date, pattern)
    }

    private fun dateFormat(date: Date, pattern: String): String {
        val format = SimpleDateFormat(pattern, Locale.getDefault())
        return format.format(date)
    }

}