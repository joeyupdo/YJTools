package com.amused.joey;

import android.os.Handler;

import com.amused.joey.mainkit.MainThreadKit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Project: JoeyTools
 * Create : joey
 * Date   : 2019/01/30 17:06
 * Description: 自定义定时器
 */
public class DelayTimer {

    private final static ConcurrentHashMap<String, Runnable> tasks = new ConcurrentHashMap<>();
    private final static AtomicReference<Handler> handler = new AtomicReference<>();

    /**
     * delay时间后执行任务
     * @param task
     * @param delay
     */
    public static void timer(final Runnable task, final long delay) {
        checkHandler();
        if (null == tasks.putIfAbsent(getTaskKey(task), new Runnable() {
            @Override
            public void run() {
                task.run();
                tasks.remove(getTaskKey(task));
            }
        })) {
            handler.get().postDelayed(tasks.get(getTaskKey(task)), delay);
        }
    }

    /**
     * delay时间后开始执行任务，时间间隔为interval
     * @param task
     * @param delay
     * @param interval
     */
    public static void interval(final Runnable task, final long delay, final long interval) {
        checkHandler();
        if (null == tasks.putIfAbsent(getTaskKey(task), new Runnable() {
            @Override
            public void run() {task.run();
                if (interval > 0) {
                    handler.get().postDelayed(tasks.get(getTaskKey(task)), interval);
                }
            }
        })) {
            handler.get().postDelayed(tasks.get(getTaskKey(task)), delay);
        }
    }

    /**
     * 取消任务
     * @param task
     */
    public static void cancel(Runnable task) {
        synchronized (tasks) {
            Runnable runnable = tasks.get(getTaskKey(task));
            if (null != runnable) {
                handler.get().removeCallbacks(runnable);
                tasks.remove(getTaskKey(task));
            }
        }
    }

    /**
     * 获取任务列表
     * @return
     */
    public static List<String> getTasks() {
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, Runnable> entity : tasks.entrySet()) {
            list.add(entity.getKey());
        }
        return list;
    }

    private static String getTaskKey(Runnable runnable) {
        return runnable.getClass().getName()+"."+runnable.hashCode();
    }

    private static void checkHandler() {
        if (null == handler.get()) {
            try {
                MainThreadKit.runOnSync(new Runnable() {
                    @Override
                    public void run() {
                        handler.compareAndSet(null, new Handler());
                    }
                }, 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
