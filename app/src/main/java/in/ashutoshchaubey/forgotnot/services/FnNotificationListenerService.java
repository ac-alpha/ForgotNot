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

    //TAG for debugging purposes
    String TAG = "NLS";

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

        Log.i(TAG, "onNotificationPosted");

        //Only extract information from the gmail notifications
        if(sbn.getPackageName().equals("com.google.android.gm")) {

            //getting the posted Notification
            Notification notification = sbn.getNotification();

            //extracting the Bundle extras from the notification
            Bundle extras = notification.extras;
            String mailSubject = "";
            try {
                //Getting mail Subject
                mailSubject = extras.getCharSequence(Notification.EXTRA_TEXT).toString();
            } catch (NullPointerException e) {
            }
            try {
                String mainBody = extras.getCharSequence(Notification.EXTRA_BIG_TEXT).toString();
                if (DateTimeInfoUtil.findDate(mainBody) || DateTimeInfoUtil.findTime(mainBody)) {

                    //Creating DbHelper object to help insert data into the database
                    DbHelper notifDbHelper = new DbHelper(this);

                    //Database in which data is to be inserted
                    SQLiteDatabase sqLiteDatabase = notifDbHelper.getWritableDatabase();

                    //Extracted date and time
                    Calendar extractedData = DateTimeInfoUtil.extractDateTime(mainBody);

                    //ContentValues object to format data in order to insert into database
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

            } catch (NullPointerException e) {
            }
        }

    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }


}
