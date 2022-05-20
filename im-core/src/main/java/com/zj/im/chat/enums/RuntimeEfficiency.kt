package com.zj.im.chat.enums

/**
 * created by ZJJ
 *
 * @param interval  how long to handle once in queue
 *
 **/

enum class RuntimeEfficiency(val interval: Long) {
    SLEEP(256), LOW(64), MEDIUM(32), HIGH(16), OVERCLOCK(8)
}