package com.amused.joeytools;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amused.joey.DelayTimer;
import com.amused.joey.SleepUtils;
import com.amused.joey.UiToast;
import com.amused.joey.crash.AppCrashInfo;
import com.amused.joey.crash.CrashHelper;
import com.amused.joey.crash.OnCrashListener;
import com.amused.joey.email.YJEmail;
import com.amused.joey.mainkit.MainThreadKit;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new CrashHelper.Builder(this)
            .setLogFilePrefix("Test")
            .setOnCrashListener(new OnCrashListener() {
                @Override
                public void onCrash(File crashLog, List<AppCrashInfo> crashData, StringBuilder crashInfo) {
                    new YJEmail.Builder()
                        .setHost("smtp.163.com")
                        .setPort(465)
                        .setFromUser("joeylogs@163.com")
                        .setFromPassword("logsautosend163")
                        .setFromAlias("TestTools-client")
                        .setToUser("joey@xixunled.com")
                        .setToAlias("TestTools-master")
                        .build()
                        .sendText("TestCrash", crashInfo.toString(), null);
                    crashLog.delete();
                }
            })
            .build();

        autoExit();
    }

    private void autoExit() {
        DelayTimer.timer(new Runnable() {
            @Override
            public void run() {
                int i = 1/0;
                try {
                    MainThreadKit.runOnAsync(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 5000);
    }
}
