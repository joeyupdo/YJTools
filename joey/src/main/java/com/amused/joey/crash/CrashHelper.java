package com.amused.joey.crash;

/**
 * Project: JoeyTools
 * Create : joey
 * Date   : 2019/01/30 13:10
 * Description:
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.amused.joey.SleepUtils;
import com.amused.joey.UiToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告.
 * 需要在Application中注册，为了要在程序启动器就监控整个程序。
 */
public class CrashHelper implements Thread.UncaughtExceptionHandler {
    private final static String TAG = "CrashHandler";
    @SuppressLint("StaticFieldLeak")
    private final static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);

    private Builder builder;
    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler defaultHandler;
    // 用来存储发生崩溃时的设备信息和异常信息
    private List<AppCrashInfo> crashInfos;

    private CrashHelper(Builder builder) {
        crashInfos = new ArrayList<>();
        this.builder = builder;
        //获取系统默认的UncaughtException处理器
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /* 当UncaughtException发生时会转入该函数来处理 */
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if ((null == throwable) && (null != defaultHandler)) {
            defaultHandler.uncaughtException(thread, throwable);
        } else {
            handleException(throwable);
            //退出进程
            SleepUtils.seconds(3);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    private String getAppName(PackageInfo packageInfo) {
        int labelRes = packageInfo.applicationInfo.labelRes;
        return builder.context.getResources().getString(labelRes);
    }

    private void handlerExceptionVersionInfo() throws PackageManager.NameNotFoundException {
        PackageManager packageManager = builder.context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageInfo(builder.context.getPackageName(), PackageManager.GET_ACTIVITIES);
        // APP版本信息
        if (null != packageInfo) {
            String appName = getAppName(packageInfo);
            crashInfos.add(new AppCrashInfo("ApplicationName", appName));
            crashInfos.add(new AppCrashInfo("PackageName", packageInfo.packageName));
            crashInfos.add(new AppCrashInfo("VersionName", (null != packageInfo.versionName)? packageInfo.versionName: "NULL"));
            crashInfos.add(new AppCrashInfo("VersionCode", String.valueOf(packageInfo.versionCode)));
            UiToast.showMessage(builder.context, appName + " has crashed, exit it !", false);
        } else {
            UiToast.showMessage(builder.context, builder.context.getPackageName() + " has crashed, exit it !", false);
        }
    }

    private void handleExceptionCustomInfo() {
        // 自定义崩溃环境信息
        if (null != builder.crashInfosAppend) {
            crashInfos.addAll(builder.crashInfosAppend);
        }
    }

    private void handleExceptionDeviceInfo() throws IllegalAccessException {
        // 设备信息
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            crashInfos.add(new AppCrashInfo(field.getName(), field.get(null).toString()));
        }
    }

    private void handleExceptionThrowable(Throwable throwable) {
        // 崩溃信息
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        Throwable cause = throwable.getCause();
        while (null != cause) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        crashInfos.add(new AppCrashInfo("CrashThrowable", writer.toString()));
    }

    private void handleException(Throwable throwable) {
        try {
            crashInfos.add(new AppCrashInfo("CrashTime", new Date().toString()));
            handlerExceptionVersionInfo();
            handleExceptionCustomInfo();
            handleExceptionDeviceInfo();
            handleExceptionThrowable(throwable);

            saveToFile();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* 如果可能，保存错误信息到文件中 */
    private void saveToFile() throws IOException {
        StringBuilder infoStr = new StringBuilder();
        for (int index = 0; index < crashInfos.size(); ++index) {
            AppCrashInfo info = crashInfos.get(index);
//            infoStr.append(entry.getKey().toUpperCase(Locale.US));
            infoStr.append(info.key);
            infoStr.append(": ");
            infoStr.append(info.value);
            infoStr.append("\n");
        }
        if (null != builder.logFilePrefix) {
            String time = formatter.format(new Date());
            String fileName = builder.logFilePrefix + "_" + time + ".log";
            File dir = builder.context.getFilesDir();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File log = new File(dir, fileName);
            FileOutputStream fos = new FileOutputStream(log.getAbsoluteFile());
            fos.write(infoStr.toString().getBytes());
            fos.close();
            if (null != builder.onCrashListener) {
                builder.onCrashListener.onCrash(crashInfos, infoStr);
                builder.onCrashListener.onCrash(log);
            } else {
                Log.e(TAG, infoStr.toString());
            }
        } else {
            if (null != builder.onCrashListener) {
                builder.onCrashListener.onCrash(crashInfos, infoStr);
            }
        }
    }

    public static class Builder {
        private Context context;
        private OnCrashListener onCrashListener;
        private String logFilePrefix;
        // 发生崩溃时记录用户自定义的信息
        private List<AppCrashInfo> crashInfosAppend;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setOnCrashListener(OnCrashListener onCrashListener) {
            this.onCrashListener = onCrashListener;
            return this;
        }

        public Builder setLogFilePrefix(String logFilePrefix) {
            this.logFilePrefix = logFilePrefix;
            return this;
        }

        public Builder addCrashInfosAppend(String key, String value) {
            if (null == crashInfosAppend) {
                crashInfosAppend = new ArrayList<>();
            }
            crashInfosAppend.add(new AppCrashInfo(key, value));
            return this;
        }

        public CrashHelper build() {
            return new CrashHelper(this);
        }
    }
}
