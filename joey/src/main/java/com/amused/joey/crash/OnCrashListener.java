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
    public void onCrash(File crashLog, List<AppCrashInfo> crashData, StringBuilder crashInfo);
}
