package com.zj.im.main;


import com.zj.im.chat.enums.RuntimeEfficiency;
import com.zj.im.main.impl.RunningObserver;
import com.zj.im.main.looper.MsgHandlerQueue;
import com.zj.im.utils.log.logger.LogUtilsKt;

import java.util.concurrent.TimeUnit;

/**
 * Created by ZJJ
 *
 * @link the msg looper , it always running with SDK active.
 * <p>
 * improve it efficiency with a frequency controller.
 */
class MsgLooper extends Thread {

    void setFrequency(Long time) {
        if (checkRunning(false)) frequencyConversion(time);
    }

    boolean checkRunning(boolean ignoreQuit) {
        boolean isRunning = isAlive() && (ignoreQuit || !mQuit) && !isInterrupted();
        if (!isRunning && !ignoreQuit) {
            observer.looperInterrupted();
        }
        return isRunning;
    }

    private Long sleepTime;
    private final String runningKey;
    private final RunningObserver observer;
    private MsgHandlerQueue msgQueue;

    /**
     * the Loop started by construct
     */
    MsgLooper(String runningKey, Long sleepTime, RunningObserver observer) {
        super("msg_handler");
        this.runningKey = runningKey;
        this.observer = observer;
        this.sleepTime = sleepTime;
        start();
    }

    private void frequencyConversion(Long sleepTime) {
        synchronized (this) {
            this.sleepTime = sleepTime;
        }
    }

    private boolean mQuit;

    @Override
    public void run() {
        while (!mQuit) {
            try {
                if (isInterrupted()) return;
                boolean isEmptyQueue = observer.runningInBlock(runningKey);
                if (msgQueue == null) {
                    this.msgQueue = new MsgHandlerQueue();
                    observer.onLooperPrepared(msgQueue);
                }
                if (isEmptyQueue && !msgQueue.hasData()) {
                    frequencyConversion(RuntimeEfficiency.SLEEP.getInterval());
                } else {
                    msgQueue.loopOnce(sleepTime);
                }
                TimeUnit.MILLISECONDS.sleep(sleepTime);
            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    observer.looperInterrupted();
                } else {
                    LogUtilsKt.printErrorInFile("MsgLooper", e.getMessage(), true);
                }
            }
        }
    }

    public void shutdown() {
        interrupt();
        synchronized (this) {
            mQuit = true;
            notify();
        }
    }
}
