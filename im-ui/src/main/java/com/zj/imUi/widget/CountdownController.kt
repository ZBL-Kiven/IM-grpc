package com.zj.rebuild.chat.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.common.util.SharedPreferencesUtils
import com.luck.picture.lib.entity.EventEntity
import com.sanhe.baselibrary.utils.LoginUtils
import com.sanhe.baselibrary.utils.Preference
import com.zj.provider.log.LogUtils
import com.zj.provider.privilege.timmer.CCTimerManager
import com.zj.rebuild.R
import com.zj.rebuild.chat.bean.MessageBean
import java.lang.Exception
import java.lang.Math.min
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat

/**
 * author: 李 祥
 * date:   2021/8/25 1:45 下午
 * description:
 */


/**
 * The time callback controller for time schedule.
 * 时间计划的时间回调控制器。
 */
abstract class CountdownController {

    /**
     * The key register into time manager map to time observer.
     **将密钥注册到时间管理器，映射到时间观察器。
     */
    protected abstract val functionKey: String

    /**
     * Whether observer time forever regardless of the lifecycle.
     * 无论生命周期是什么，观察者都将永远存在。
     */
    protected abstract val observerForever: Boolean

    protected var isObserving = false

    /**
     * 添加时间观察者
     */
    protected fun addTimerObserver(
        interval: Long,
        observer: Observer<Long>,
        key: String = functionKey
    ) {
        if (isObserving && functionKey == key) return
        //俩种模式
            CCTimerManager.addObserverForever(interval, key, observer)
        isObserving = true
    }

    //移除
    protected fun removeTimeObserver(lw: LifecycleOwner) {
        if (!isObserving) return
        CCTimerManager.removeObserver(lw, functionKey)
        isObserving = false
    }

    /**
     * Pause the time callback.
     */
    @CallSuper
    open fun pause() {
        CCTimerManager.pause(functionKey)
    }

    /**
     * Resume the time callback.
     */
    @CallSuper
    open fun resume() {
        CCTimerManager.resume(functionKey)
    }


}

 class CountDownTimer(val view: View?,val messageBean: MessageBean): CountdownController(),Observer<Long>{

     override val functionKey: String
         get() = "REWARD_COUNTDOWN"
     override val observerForever: Boolean
         get() = true

     private var mTimeTxt: TextView? = null

     init {
         mTimeTxt = view?.findViewById(R.id.tv_countdown)
     }
    enum class State(private val colorString: String) {
        Stop("#5CC9f9"), Running("#FFA30B"), PreStop("#FFA30B"), Completed("#FFA30B");

        fun getColor(): String {
            return colorString
        }
    }
    /**
     * The init time for progress controller, for restore cache time line.
     */
      val initTime: Long?=0
    /**
     * This progress current time.
     * Unit: millisecond
     */
     var curTimeMills: MutableLiveData<Long> = MutableLiveData(0L)

    /**
     * Current state for the controller.
     */
    private var curState: MutableLiveData<State> = MutableLiveData(State.Stop)

    private var weakLifecycle: WeakReference<*>? = null


     fun startTimer(lw: Context, interval: Long): CountDownTimer {
        weakLifecycle ?: run {
            weakLifecycle = WeakReference(lw)
        }
         curTimeMills.value = messageBean.questionContent?.expireTime
        if (!isObserving) addTimerObserver(interval, this)
        return this
    }

    fun addStateObserver(lw: LifecycleOwner, stateObserver: Observer<State>) {
        curState.observe(lw, stateObserver)
    }

    fun release(lw: LifecycleOwner) {
        removeTimeObserver(lw)
        weakLifecycle = null
    }



     override fun pause() {
        super.pause()
    }

    /**
     * The logic to dispose the change progress.
     */
    override fun onChanged(it: Long) {
        //赋值
        curTimeMills.value = curTimeMills.value?.plus(it) ?: it
        if(curTimeMills.value!! <=0) {
            mTimeTxt?.text = "--"
            pause()
            return
        }
        mTimeTxt?.text = timeParseHour(curTimeMills.value!!)
        //动态再把值给item赋值，这样就算滑开后再回来z这个Item的计时数据也不会错误
        messageBean.questionContent?.expireTime = curTimeMills.value!!
    }

    fun getCountDownTime(): Long? {
        return curTimeMills.value?.toLong()
    }


//     private fun timeParse(duration: Long): String? {
//         var time: String? = ""
//         if (duration > 1000) {
//             time = timeParseMinute(duration)
//         } else {
//             val minute = duration / 60000
//             val seconds = duration % 60000
//             val second = Math.round(seconds.toFloat() / 1000).toLong()
//             if (minute < 10) {
//                 time += "0"
//             }
//             time += "$minute:"
//             if (second < 10) {
//                 time += "0"
//             }
//             time += second
//         }
//         return time
//     }

     private fun timeParseHour(duration: Long): String? {
         var time: String? = ""
         val hour = duration / 360000
         val minutes = duration % 360000
         val minute = Math.round(minutes.toFloat() / 6000)
         if (hour < 1) {
             time += 0
         }
         time += "$hour:"
         if (minute < 10) {
             time += "0"
         }
         time += minute
         return time
     }

//     @SuppressLint("SimpleDateFormat")
//     private val msFormat = SimpleDateFormat("mm:ss")
//
//     fun timeParseMinute(duration: Long): String? {
//         return try {
//              msFormat.format(duration)
//         } catch (e: Exception) {
//             e.printStackTrace()
//             "0:00"
//         }
//     }

}



