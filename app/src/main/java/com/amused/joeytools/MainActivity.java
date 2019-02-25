package com.amused.joeytools;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amused.joey.DelayTimer;
import com.amused.joey.YJID;
import com.amused.joey.mainkit.MainThreadKit;
import com.amused.joey.sys.SystemInfo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DelayTimer.timer(() -> {
            System.out.println("Cpu name: " + SystemInfo.getCpuName());
        }, 2000);

        autoExit(3000);
    }

    private void autoExit(final long exitTime) {
        DelayTimer.timer(() -> {
            try {
                MainThreadKit.runOnAsync(this::finish);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, exitTime);
    }
}
