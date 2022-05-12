package com.zj.im.sender

/**
 * Created by ZJJ
 */
interface OnSendBefore<T> {

    /**
     * This method will be called during the waiting period of the message queue.
     * It will not block the normal passage of the message.
     * The message will only be sent after all the [OnSendBefore] attached to this message are executed,
     * so it is very suitable for message preprocessing ,
     * additional tasks in case of message file upload, session creation before sending, etc.
     *
     * @param onStatus It needs to be called manually, except for [OnStatus.onProgress],
     * it means the execution is completed, such as [OnStatus.call] [OnStatus.error]
     * */
    fun call(onStatus: OnStatus<T>)

}

