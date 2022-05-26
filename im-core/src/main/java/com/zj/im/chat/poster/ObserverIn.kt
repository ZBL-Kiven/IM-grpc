package com.zj.im.chat.poster

interface ObserverIn {

    fun onObserverRegistered(creator: UIHelperCreator<*, *, *>)

    fun onObserverUnRegistered(creator: UIHelperCreator<*, *, *>)
}