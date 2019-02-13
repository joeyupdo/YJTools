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

    private AidlUtils() {}

    public static class Builder<T> {
        private CountDownLatch countDownLatch = new CountDownLatch(1);

        private Context context;
        private String action;
        private String toPackageName;
        private long timeout;
        private Class<?> clazz;

        private IBinder iBinder;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder<T> setAction(String action) {
            this.action = action;
            return this;
        }

        public Builder<T> setToPackageName(String toPackageName) {
            this.toPackageName = toPackageName;
            return this;
        }

        public Builder<T> setTimeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder<T> setClazz(Class<?> clazz){
            this.clazz = clazz;
            return this;
        }

        @SuppressWarnings("unchecked")
        public T build() throws InterruptedException, ClassNotFoundException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException, IllegalAccessException {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                throw new RuntimeException("Can\'t runing in the main thread !");
            }
            if (null == clazz) {
                throw new IllegalArgumentException("Clazz can not be null !");
            }
            Intent intent = initIntent();
            ServiceConnection connection = initConnection();
            initBinder(intent, connection);
            if (null == iBinder) {
                throw new RuntimeException("Get IBinder is null !");
            }
            Class<?> stub = Class.forName(clazz.getName() + "$Stub");
            Method asInterface = stub.getMethod("asInterface", IBinder.class);
            return (T) asInterface.invoke(stub, iBinder);
        }

        private Intent initIntent() {
            if (null == action) {
                throw new IllegalArgumentException("Action can not be null !");
            }
            Intent intent = new Intent(action);
            if (null != toPackageName) {
                intent.setPackage(toPackageName);
            }
            return intent;
        }

        private ServiceConnection initConnection() {
            ServiceConnection conn = new ServiceConnection() {

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    context.unbindService(this);
                }

                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    iBinder = service;
                    countDownLatch.countDown();
                }
            };
            return conn;
        }

        public void initBinder(Intent intent, ServiceConnection connection) throws InterruptedException {
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
            try {
                countDownLatch.await((timeout > 0)? timeout: 3000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new InterruptedException(e.getMessage());
            }
        }
    }
}
