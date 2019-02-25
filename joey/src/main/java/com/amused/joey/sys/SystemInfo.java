package com.amused.joey.sys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class SystemInfo {
    private SystemInfo() { }

    public static String getCpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String name = text.split(":\\s+", 2)[1];
            br.close();
            fr.close();
            return name;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDeviceID(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        PackageManager pm = context.getPackageManager();
        if ((null != tm) && pm.checkPermission(Manifest.permission.READ_PHONE_STATE, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
            @SuppressLint("HardwareIds") String deviceId = tm.getDeviceId();
            return (null == deviceId)? null: DeviceUtil.formatImei(deviceId);
        } else {
            return null;
        }
    }


    public static int getPhoneType(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return (null != tm)? tm.getPhoneType(): TelephonyManager.PHONE_TYPE_NONE;
    }

}
