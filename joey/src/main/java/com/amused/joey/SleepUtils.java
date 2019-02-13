package com.amused.joey;

/**
 * Project: JoeyTools
 * Create : joey
 * Date   : 2019/01/30 16:33
 * Description:
 */
public class SleepUtils {
    public static void seconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void millis(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
