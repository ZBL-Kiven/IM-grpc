package com.zj.imUi.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.zj.imUi.R
import java.lang.StringBuilder
import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * author: 李 祥
 * date:   2021/9/8 3:47 下午
 * description:
 */
object TimeDiffUtils {

    @SuppressLint("SimpleDateFormat")
      fun timeDifference(lastTime: Long): Long? {
      //传过来的时间戳单位是毫秒
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val sendTime = formatter.format(lastTime)
        val currentTime = formatter.format(System.currentTimeMillis())
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val time1 = dateFormat.parse(sendTime)
        val time2 = dateFormat.parse(currentTime)
      //此时为微秒  一秒等于一百万微秒，等于一千毫秒，这儿换算成毫秒
//        Log.e("li_xiang","发送时间戳: " +lastTime+" 时间差（毫秒）"+((time2.time-time1.time)).toString()+"   sendTime  $sendTime"+"当前时间戳"+System.currentTimeMillis()+"  currentTime $currentTime"+"     time1 $time1"+"  time2  $time2")
        return (time2.time - time1.time)
    }


   fun setTimeText(sendTime: Long,context:Context): CharSequence? {
    return when (sendTime) {
        in 60000..3599999 -> {
          StringBuilder(timeParse(sendTime)).append(context.getString(R.string.im_ui_min_ago))
        }
        in 3600000 ..3600000 * 48 -> {
          StringBuilder(timeParseHour(sendTime)).append(context.getString(R.string.im_ui_hours_ago))
        }
        else -> null
    }
  }

   private fun timeParseHour(duration: Long): String {
    val time: String?
    val hour = duration / 3600000
    time = hour.toString()
    return time
  }

   private fun timeParse(duration: Long): String {
    val minute = duration / 60000
    return minute.toString()
  }


}
