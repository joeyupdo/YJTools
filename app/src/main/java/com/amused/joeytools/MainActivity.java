package com.amused.joeytools;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amused.joey.SleepUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autoExit();
    }

    private void autoExit() {
        SleepUtils.seconds(3);
        finish();
    }
}
