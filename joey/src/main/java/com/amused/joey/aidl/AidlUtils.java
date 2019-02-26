package com.amused.joey.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Project: JoeyTools
 * Create : joey
 * Date   : 2019/01/30 11:22
 * Description:
 */
public class AidlUtils {
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private Builder builder;
    private IBinder iBinder;
    private ServiceConnection connection;

    private AidlUtils(Builder builder) { this.builder = builder; }

    @SuppressWarnings("unchecked")
    public Object getObject() throws InterruptedException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("Can\'t runing in the main thread !");
        }
        if (null == builder.clazz) {
            throw new IllegalArgumentException("Clazz can not be null !");
        }
        Intent intent = initIntent();
        connection = initConnection();
        builder.context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        countDownLatch.await((builder.timeout > 0)? builder.timeout: 3000, TimeUnit.MILLISECONDS);
        if (null == iBinder) {
            throw new RuntimeException("Get IBinder is null !");
        }
        Class<?> stub = Class.forName(builder.clazz.getName() + "$Stub");
        Method asInterface = stub.getMethod("asInterface", IBinder.class);
        return asInterface.invoke(stub, iBinder);
    }

    public void disconnected() {
        if (null != connection) {
            builder.context.unbindService(connection);
            connection = null;
        }
    }

    private Intent initIntent() {
        if (null == builder.action) {
            throw new IllegalArgumentException("Action can not be null !");
        }
        Intent intent = new Intent(builder.action);
        if (null != builder.toPackageName) {
            intent.setPackage(builder.toPackageName);
        }
        return intent;
    }

    private ServiceConnection initConnection() {
        return new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                builder.context.unbindService(this);
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                iBinder = service;
                countDownLatch.countDown();
            }
        };
    }

    public static class Builder {
        private Context context;
        private String action;
        private String toPackageName;
        private long timeout;
        private Class<?> clazz;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setAction(String action) {
            this.action = action;
            return this;
        }

        public Builder setToPackageName(String toPackageName) {
            this.toPackageName = toPackageName;
            return this;
        }

        public Builder setTimeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder setClazz(Class<?> clazz){
            this.clazz = clazz;
            return this;
        }

        public AidlUtils build() {
            return new AidlUtils(this);
        }
    }
}
