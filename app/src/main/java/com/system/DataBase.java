package com.system;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.ContactsContract;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shahnawaz on 5/3/2016.
 */
public class DataBase extends SQLiteOpenHelper {
    private static final String NAME = "smsdb";
    private static final int VERSION = 1;

    private static final String KEY_SMS = "sms";
    private static final String KEY_NUMBER = "number";
    private static final String KEY_INBOUND = "inbound";
    private static final String KEY_CONTACT = "contact";

    private static final String TABLE_NAME = "table_messages";

    private Context mContext;

    public DataBase(Context context) {
        super(context, NAME, null, VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String create_table = "CREATE TABLE `table_messages` ( " +
                    " `_id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, " +
                    " `sms` TEXT, " +
                    " `number` TEXT, " +
                    " `contact` TEXT, " +
                    " `inbound` INTEGER DEFAULT 0 " +
                    ")";

            db.rawQuery(create_table, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int getCount() {
        try {
            int ret = 0;
            String countQuery = "SELECT COUNT(*) FROM " + TABLE_NAME;
            Cursor cursor = this.getReadableDatabase().rawQuery(countQuery, null);
            if (cursor != null) {
                ret = cursor.getInt(0);
                cursor.close();
            }
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<Data> getMessages() {
        ArrayList<Data> data = new ArrayList<>();
        try {
            String query = "SELECT * FROM " + TABLE_NAME;
            Cursor cursor = this.getReadableDatabase().rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                data.add(new Data(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public void deleteMessages() {
        try {
            this.getWritableDatabase().delete(TABLE_NAME, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertMessage(String message, String number, boolean inbound) {
        try {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            Cursor cursor = mContext.getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
            String contactname = "";
            if (cursor != null && cursor.moveToFirst()) {
                contactname = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                cursor.close();
            }

            ContentValues values = new ContentValues();
            values.put(KEY_NUMBER, number);
            values.put(KEY_SMS, message);
            values.put(KEY_INBOUND, inbound ? 1 : 0);
            values.put(KEY_CONTACT, contactname);

            this.getWritableDatabase().insert(TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Data {
        @SerializedName("sms")
        String sms;

        @SerializedName("number")
        String number;

        @SerializedName("contact")
        String contact;

        @SerializedName("inbound")
        Integer inbound;

        public Data(String sms, String number, String contact, Integer inbound) {
            this.sms = sms;
            this.number = number;
            this.contact = contact;
            this.inbound = inbound;
        }

    }
}
