package com.amused.joey;

import android.content.Context;
import android.widget.Toast;

import com.amused.joey.mainkit.MainThreadKit;

/**
 * Project: JoeyTools
 * Create : joey
 * Date   : 2019/01/29 17:05
 * Description:
 */
public class UiToast {
    private static Toast toast;

    public static void showMessage(final Context context, final String message, final boolean lenLong) {
        synchronized (UiToast.class) {
            try {
                MainThreadKit.runOnAsync(() -> {
                    if (null != toast) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(context, message, lenLong? Toast.LENGTH_LONG: Toast.LENGTH_SHORT);
                    toast.show();
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
