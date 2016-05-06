package com.system;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Shahnawaz on 5/6/2016.
 */
public class OutService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getApplicationContext().getContentResolver().registerContentObserver();
        return START_STICKY;
    }

    public static class MyObserver extends ContentObserver {

        public MyObserver(Handler handler) {
            super(handler);
        }
    }
}
