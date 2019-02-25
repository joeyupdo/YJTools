package com.amused.joey.sys;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.text.DecimalFormat;

public class DiskManager {
    private final static int K1 = 1024;

    private DiskManager() { }

    public static String capacityToString(final float data) {
        float space = data;
        String unit = "B";
        for (int count = 0; count < 4; ++count) {
            if (space < K1) {
                break;
            }
            space /= K1;
            switch (count) {
                case 0:
                    unit = "KB";
                    break;
                case 1:
                    unit = "MB";
                    break;
                case 2:
                    unit = "GB";
                    break;
                case 3:
                    unit = "TB";
                    break;
                default:
                    break;
            }
        }
        return new DecimalFormat(".0").format(space) + unit;
    }

    public static long getDataFree() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public static String getDataFreeUnit() {
        return capacityToString(getDataFree());
    }

    public static long getDataSpace() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();      //每个block 占字节数
        long totalBlocks = stat.getBlockCount();   //block总数
        return totalBlocks * blockSize;
    }

    public static String getDataSpaceUnit() {
        return capacityToString(getDataSpace());
    }
}
