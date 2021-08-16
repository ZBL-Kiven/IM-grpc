package com.zj.im.chat.poster

interface DataHandler<DATA, R> {
    fun handle(data: DATA): R
}