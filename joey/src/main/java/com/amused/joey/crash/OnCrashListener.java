package com.amused.joey.crash;

import java.io.File;
import java.util.List;

/**
 * Project: JoeyTools
 * Create : joey
 * Date   : 2019/01/30 13:36
 * Description:
 */
public interface OnCrashListener {
    /**
     * 当发生崩溃时触发，注意：crashLog可能为空
     * @param crashLog
     * @param crashData
     * @param crashInfo
     */
    public void onCrash(File crashLog, List<AppCrashInfo> crashData, StringBuilder crashInfo);
}
