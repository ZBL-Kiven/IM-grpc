package com.zj.rebuild.chat.widget.bottom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.zj.imUi.R
import com.zj.rebuild.chat.widget.GroupRewardMeItem
import java.lang.StringBuilder

/**
 * author: 李 祥
 * date:   2021/8/12 7:44 下午
 * description:
 */
class GroupMessageItemTime @JvmOverloads  constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attributeSet, defStyle) {

    private var mTimeTextView :TextView?=null
    private var mGroupRewardMeItem: GroupRewardMeItem
    init {
        LayoutInflater.from(context).inflate(R.layout.group_message_item_time,this,true)
        mGroupRewardMeItem = findViewById(R.id.group_reward_item)
        mTimeTextView = findViewById(R.id.tv_tIme)
    }

    fun setData(messageBean: Any){
        mGroupRewardMeItem.setBackGround(messageBean)
        // TODO: 2021/8/17 记得修改 时间换算
        val stringBuilder:StringBuilder = StringBuilder()
        mTimeTextView?.text = setTimeText(messageBean.sendTime)
    }

    private fun setTimeText(sendTime: Int): CharSequence? {
        if (sendTime in 60000..359999){
            return StringBuilder(timeParse(sendTime).toString()).append(" mins ago")
        }else if(sendTime > 360000){
            return StringBuilder(timeParseHour(sendTime).toString()).append(" hours ago")
        }else
            return null
    }


    private fun timeParseHour(duration: Int): String? {
        var time: String? = ""
        val hour = duration / 360000
        val minutes = duration % 360000
        val minute = Math.round(minutes.toFloat() / 6000)
        time = hour.toString()
        return time
    }

    fun timeParse(duration: Int): String? {
        var time: String? = ""
            val minute = duration / 60000
            val seconds = duration % 60000
            val second = Math.round(seconds.toFloat() / 1000).toLong()
//            if (minute < 10) {
//                time += "0"
//            }
//            time += "$minute:"
//            if (second < 10) {
//                time += "0"
//            }
//            time += second
        return minute.toString()
    }
}
