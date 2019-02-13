package com.amused.joey.mainkit;

import android.os.Looper;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Project: JoeyTools
 * Create : joey
 * Date   : 2019/01/30 08:51
 * Description:
 */
public class MainThreadKit {
    private static AtomicReference<HandlerPoster> mainPoster = new AtomicReference<>(null);

    private static HandlerPoster getMainPoster() {
        if (null == mainPoster.get()) {
            mainPoster.compareAndSet(null, new HandlerPoster(Looper.getMainLooper()));
        }
        return mainPoster.get();
    }

    public static boolean isOnMain() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
  
    /** 
     * Asynchronously 
     * The child thread asynchronous run relative to the main thread, 
     * not blocking the child thread 
     * 
     * @param runnable Runnable Interface 
     */  
    public static void runOnAsync(Runnable runnable) throws InterruptedException {
            if (isOnMain()) {
                runnable.run();
        } else {
            getMainPoster().async(runnable);
        }
    }  
  
    /** 
     * Synchronously 
     * The child thread relative thread synchronization operation, 
     * blocking the child thread, 
     * thread for the main thread to complete 
     * 
     * @param runnable Runnable Interface
     * @param waitTime millisecond
     */
    public static void runOnSync(Runnable runnable, int waitTime) throws InterruptedException {
        if (isOnMain()) {
            runnable.run();
        } else {
            SyncPost poster = new SyncPost(runnable);
            getMainPoster().sync(poster);
            poster.waitRun(waitTime);
        }
    }
  
    public static void dispose() {
        HandlerPoster handlerPoster = mainPoster.get();
        if (null != handlerPoster) {
            handlerPoster.dispose();
            mainPoster.set(null);
        }  
    }  
}
