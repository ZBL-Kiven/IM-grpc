package com.zj.im.utils

import com.zj.im.chat.enums.RuntimeEfficiency
import com.zj.im.main.StatusHub.isRunningInBackground

/**
 * created by ZJJ
 *
 * the efficiency checker ,
 *
 * the SDK may adjust frequency to adapt the current state,
 *
 * for saving the power.
 * */

internal object EfficiencyUtils {

    private const val levelSleep = 3

    private const val levelLow = 5

    private const val levelMedium = 8

    private const val levelHigh = 15

    fun getEfficiency(total: Int): RuntimeEfficiency {
        return if (isRunningInBackground) {
            when (total) {
                in 0..levelSleep -> {
                    RuntimeEfficiency.SLEEP
                }
                in levelSleep..levelLow -> {
                    RuntimeEfficiency.LOW
                }
                else -> RuntimeEfficiency.OVERCLOCK
            }
        } else {
            when (total) {
                in 0..levelMedium -> {
                    RuntimeEfficiency.MEDIUM
                }
                in levelMedium..levelHigh -> {
                    RuntimeEfficiency.HIGH
                }
                else -> {
                    RuntimeEfficiency.OVERCLOCK
                }
            }
        }
    }
}