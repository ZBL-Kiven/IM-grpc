package com.zj.im.chat.poster

interface DataHandler<DATA> {

    fun handle(data: DATA): Any?

}