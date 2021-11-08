package com.zj.im.main;

import com.zj.im.main.impl.RunningObserver;

import java.util.concurrent.TimeUnit;

/**
 * Created by ZJJ
 *
 * @link the msg looper , it always running with SDK active.
 * <p>
 * improve it efficiency with a frequency controller.
 */
class MsgLooper extends Thread {

    interface AbandonInterruptedListener {
        void looperInterrupted();
    }

    void setFrequency(Long time) {
        if (checkRunning(false)) frequencyConversion(time);
    }

    boolean checkRunning(boolean ignoreQuit) {
        boolean isRunning = isAlive() && (ignoreQuit || !mQuit) && !isInterrupted();
        if (!isRunning && !ignoreQuit) {
            abandonInterruptedListener.looperInterrupted();
        }
        return isRunning;
    }

    private Long sleepTime;
    private final String runningKey;
    private final RunningObserver observer;
    private final AbandonInterruptedListener abandonInterruptedListener;

    /**
     * the Loop started by construct
     */
    MsgLooper(String runningKey, Long sleepTime, RunningObserver observer, AbandonInterruptedListener l) {
        super("msg_handler");
        this.abandonInterruptedListener = l;
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
                observer.runningInBlock(runningKey);
                TimeUnit.NANOSECONDS.sleep(sleepTime);
            } catch (Exception ignored) {
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
