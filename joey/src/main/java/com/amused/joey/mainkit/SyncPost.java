package com.amused.joey.mainkit;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Project: JoeyTools
 * Create : joey
 * Date   : 2019/01/30 10:32
 * Description:
 */
class SyncPost {
    private AtomicInteger lock;
    private Runnable runnable;
  
    SyncPost(Runnable r) {
        runnable = r;
        lock = new AtomicInteger();
    }

    void run() {
        if (lock.compareAndSet(0, 1)) {
            runnable.run();
            lock.set(0);
            synchronized (this) {
                this.notifyAll();
            }
        }
    }

    void waitRun(int time) throws InterruptedException {
        synchronized (this) {
            this.wait(time);
        }
    }
}
