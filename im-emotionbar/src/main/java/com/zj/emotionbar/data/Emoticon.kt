@file:Suppress("unused")

package com.zj.emotionbar.data

open class Emoticon() {
    /**
     * @param icon 显示
     * @param uri  发送数据地址
     */
    constructor(id: Int?, icon: String?, url: String?, pack: EmoticonPack<*>) : this() {
        this.id = id
        this.icon = icon
        this.url = url
        this.pack = pack
    }

    var pack: EmoticonPack<*>? = null
    var id: Int? = null
    var icon: String? = null
    var url: String? = null
}