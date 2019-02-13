package com.amused.joey.mainkit;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Project: JoeyTools
 * Create : joey
 * Date   : 2019/01/30 09:09
 * Description:
 */
class HandlerPoster extends Handler {
	private final LinkedBlockingQueue<Runnable> asyncPool = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<SyncPost> syncPool = new LinkedBlockingQueue<>();
    private final AtomicBoolean asyncActive;
    private final AtomicBoolean syncActive;
  
    HandlerPoster(Looper looper) {
        super(looper);  
        asyncActive = new AtomicBoolean(false);
        syncActive = new AtomicBoolean(false);
    }  
  
    void dispose() {  
        this.removeCallbacksAndMessages(null);  
        this.asyncPool.clear();  
        this.syncPool.clear();  
    }  
  
    void async(Runnable runnable) throws InterruptedException {
        asyncPool.put(runnable);
        if (asyncActive.compareAndSet(false, true)) {
            if (!sendMessage(obtainMessage(0, PostMethod.ASYNC))) {
                asyncActive.set(false);
                throw new RuntimeException("Could not send handler message");
            }
        }
    }  
  
    void sync(SyncPost post) throws InterruptedException {
        syncPool.put(post);
        if (syncActive.compareAndSet(false, true)) {
            syncActive.set(false);
            if (!sendMessage(obtainMessage(0, PostMethod.SYNC))) {
                throw new RuntimeException("Could not send handler message");
            }
        }
    }  
  
    @Override
    public void handleMessage(Message msg) {
        try {
            switch ((PostMethod) msg.obj) {
                case ASYNC:
                    while (!asyncPool.isEmpty()) {
                        Runnable runnable = asyncPool.take();
                        runnable.run();
                    }
                    break;
                case SYNC:
                    while (!syncPool.isEmpty()) {
                        SyncPost syncPost = syncPool.take();
                        syncPost.run();
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
