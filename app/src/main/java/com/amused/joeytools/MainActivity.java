package com.amused.joeytools;

import android.app.Activity;
import android.os.Bundle;

import com.amused.joey.DelayTimer;
import com.amused.joey.mainkit.MainThreadKit;
import com.amused.joey.sys.SystemInfo;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DelayTimer.timer(() -> System.out.println("Cpu name: " + SystemInfo.getCpuName()), 2000);
//        DelayTimer.timer(() -> {
//            new Thread(() -> {
//                AidlUtils aidlUtils = new AidlUtils.Builder(this)
//                    .setAction("com.xixun.joey.aidlset.SettingsService")
//                    .setToPackageName("com.xixun.joey.cardsystem")
//                    .setClazz(CardService.class)
//                    .build();
//                try {
//                    CardService aidl = (CardService) aidlUtils.getObject();
//                    System.out.println("WIDTH X HEIGHT ==> " + aidl.getScreenWidth() + " X " + aidl.getScreenHeight());
//                    aidlUtils.disconnected();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                } catch (NoSuchMethodException e) {
//                    e.printStackTrace();
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            }).start();
//        }, 3000);

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
