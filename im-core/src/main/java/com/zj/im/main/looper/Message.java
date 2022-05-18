package com.zj.im.main.looper;

class Message {

    long delay;
    final Object obj;

    Message(long delay, Object obj) {
        this.delay = delay;
        this.obj = obj;
    }

}
