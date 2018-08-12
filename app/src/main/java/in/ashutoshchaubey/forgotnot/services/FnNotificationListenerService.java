package in.ashutoshchaubey.forgotnot.services;

import android.app.Notification;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.Calendar;

import in.ashutoshchaubey.forgotnot.constants.Constants;
import in.ashutoshchaubey.forgotnot.helpers.DbHelper;
import in.ashutoshchaubey.forgotnot.utilities.DateTimeInfoUtil;
import in.ashutoshchaubey.forgotnot.constants.Constants.NotifEntry;

/**
 * Created by ashutoshchaubey on 30/07/18.
 */

public class FnNotificationListenerService extends NotificationListenerService {

    public static final String TAG = "SampleNLS";
    public static final String MY_PREFERENCES = "MY_PREFERENCES";
    public static final String MY_MAIL = "MY_MAIL";
    int testValue;


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {



        if(sbn.getPackageName().equals("com.google.android.gm")) {


            Notification notification = sbn.getNotification();
            Bundle extras = notification.extras;
            String mailSubject = "";
            try {
                mailSubject = extras.getCharSequence(Notification.EXTRA_TEXT).toString();

//            Log.e(TAG, mainContent);
            } catch (NullPointerException e) {
//            Log.e(TAG, e.getMessage());
            }
            try {
                String mainBody = extras.getCharSequence(Notification.EXTRA_BIG_TEXT).toString();
                if (DateTimeInfoUtil.findDate(mainBody) || DateTimeInfoUtil.findTime(mainBody)) {


                    DbHelper notifDbHelper = new DbHelper(this);
                    SQLiteDatabase sqLiteDatabase = notifDbHelper.getWritableDatabase();

                    Calendar extractedData = DateTimeInfoUtil.extractDateTime(mainBody);
                    Log.e(TAG, extractedData.get(Calendar.YEAR)+"");
                    ContentValues cv = new ContentValues();
                    cv.put(NotifEntry.COLUMN_EVENT_NAME, mailSubject);
                    cv.put(NotifEntry.COLUMN_DATE_DD, extractedData.get(Calendar.DAY_OF_MONTH));
                    cv.put(NotifEntry.COLUMN_DATE_MM, extractedData.get(Calendar.MONTH));
                    cv.put(NotifEntry.COLUMN_DATE_YYYY, extractedData.get(Calendar.YEAR));
                    cv.put(NotifEntry.COLUMN_TIME_HR, extractedData.get(Calendar.HOUR));
                    cv.put(NotifEntry.COLUMN_TIME_MIN, extractedData.get(Calendar.MINUTE));
                    cv.put(NotifEntry.COLUMN_TIME_AM_PM, extractedData.get(Calendar.AM_PM));
                    cv.put(NotifEntry.COLUMN_SET_UNSET, Constants.REMINDER_UNNOTICED);
                    cv.put(NotifEntry.COLUMN_SHOW, Constants.NOTIF_SHOW);
                    sqLiteDatabase.insert(NotifEntry.TABLE_NAME, null, cv);
                    cv.clear();
                    sqLiteDatabase.close();

                }
//            Log.e(TAG, mainBody);

            } catch (NullPointerException e) {
//            Log.e(TAG, e.getMessage());
            }
        }

    }

    @Override
    public boolean stopService(Intent name) {
        Log.e(TAG, "Service stopped :(");
        return super.stopService(name);
    }


}
