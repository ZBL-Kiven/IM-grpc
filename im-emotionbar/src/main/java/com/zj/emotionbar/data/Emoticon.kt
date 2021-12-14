@file:Suppress("unused")

package com.zj.emotionbar.data

open class Emoticon() {
    /**
     * @param icon 显示
     * @param uri  发送数据地址
     */
    constructor(code: String?, icon: String?, uri: String?) : this() {
        this.code = code
        this.icon = uri
        this.uri = uri
    }

    var code: String? = null
    var icon: String? = null
    var uri: String? = null
}