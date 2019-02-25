package com.amused.joey.sys;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.File;
import java.util.List;

public class Software {

    private Software() {}

    // 判断android SDK 版本是否大于等于5.0
    public static boolean isHighLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static PackageInfo getInfoByPackageName(Context context, String packageName) {
        for (PackageInfo info : context.getPackageManager().getInstalledPackages(0)) {
            if (info.packageName.equals(packageName)) {
                return info;
            }
        }
        return null;
    }

    public static PackageInfo getInfoByPathName(Context context, String pathName) {
        if (new File(pathName).exists()) {
            return context.getPackageManager().getPackageArchiveInfo(pathName, PackageManager.GET_ACTIVITIES);
        }
        return null;
    }

    public static boolean isInstalled(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isAppRunning(Context context, String packageName) {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        if (isHighLollipop()) {
            return serviceRunning(am, packageName);
        } else {
            return appRunning(am, packageName);
        }
    }

    private static boolean serviceRunning(ActivityManager am, String packageName) {
        if (null != am) {
            List<ActivityManager.RunningServiceInfo> list = am.getRunningServices(300);
            for (ActivityManager.RunningServiceInfo info : list) {
                if (info.service.getPackageName().equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean appRunning(ActivityManager am, String packageName) {
        if (null != am) {
            for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
                if (info.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
