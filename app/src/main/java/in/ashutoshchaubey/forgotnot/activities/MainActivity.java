package in.ashutoshchaubey.forgotnot.activities;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import in.ashutoshchaubey.forgotnot.R;
import in.ashutoshchaubey.forgotnot.constants.Constants;
import in.ashutoshchaubey.forgotnot.helpers.DbHelper;
import in.ashutoshchaubey.forgotnot.databaseandadapters.NotifAdapter;
import in.ashutoshchaubey.forgotnot.databaseandadapters.NotifItem;
import in.ashutoshchaubey.forgotnot.services.FnNotificationListenerService;

import static in.ashutoshchaubey.forgotnot.services.FnNotificationListenerService.MY_PREFERENCES;

public class MainActivity extends AppCompatActivity implements NotifAdapter.ItemClickListener{

    private AlertDialog enableNotificationListenerAlertDialog;
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private SQLiteDatabase db;
    private DbHelper notifDbHelper;
    private ArrayList<NotifItem> notifList;
    private NotifAdapter notifAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isNotificationServiceEnabled()){
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
            enableNotificationListenerAlertDialog.show();
        }else{
            startService(new Intent(MainActivity.this, FnNotificationListenerService.class));
        }

        TextView textView = (TextView) findViewById(R.id.text);
        SharedPreferences preferences = preferences = getApplicationContext()
                .getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.notif_recycler_view);
        notifDbHelper = new DbHelper(this);
        db = notifDbHelper.getReadableDatabase();
        notifList = new ArrayList<>();
        notifList.add(new NotifItem("ECN 102 Lecture", Calendar.getInstance(), Constants.REMINDER_SET));
        notifList.add(new NotifItem("Document Verification @ MAC", Calendar.getInstance(), Constants.REMINDER_UNSET));
        notifList.add(new NotifItem("Fee Deposit", Calendar.getInstance(), Constants.REMINDER_UNNOTICED));
        fetchData();
        Log.e(FnNotificationListenerService.TAG, notifList.size()+"");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notifAdapter = new NotifAdapter(this,notifList);
        notifAdapter.setClickListener(this);
        recyclerView.setAdapter(notifAdapter);

    }

    /**
     * Created by Fábio Alves Martins Pereira (Chagall)
     * Is Notification Service Enabled.
     * Verifies if the notification listener service is enabled.
     * Got it from: https://github.com/kpbird/NotificationListenerService-Example/blob/master/NLSExample/src/main/java/com/kpbird/nlsexample/NLService.java
     * @return True if enabled, false otherwise.
     */
    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Created by Fábio Alves Martins Pereira (Chagall)
     * Build Notification Listener Alert Dialog.
     * Builds the alert dialog that pops up if the user has not turned
     * the Notification Listener Service on yet.
     * @return An alert dialog which leads to the notification enabling screen
     */
    private AlertDialog buildNotificationServiceAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Notification Listener Service");
        alertDialogBuilder.setMessage("For the the app. to work you need to enable the Notification Listener Service. Enable it now?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                    }
                });
        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // If you choose to not enable the notification listener
                        // the app. will not work as expected
                    }
                });
        return(alertDialogBuilder.create());
    }

    private void fetchData(){

        Cursor cursor = db.query(Constants.NotifEntry.TABLE_NAME,null, null,
                null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {

                    NotifItem notifItem = new NotifItem();
                    notifItem.setTitle(cursor.getString(1));
                    Calendar time = Calendar.getInstance();
                    time.set(Calendar.DATE, cursor.getInt(2));
                    time.set(Calendar.MONTH, cursor.getInt(3));
                    time.set(Calendar.YEAR, cursor.getInt(4));
                    time.set(Calendar.HOUR, cursor.getInt(5));
                    time.set(Calendar.MINUTE, cursor.getInt(6));
                    time.set(Calendar.AM_PM, cursor.getInt(7));
                    notifItem.setReminderTime(time);
                    notifItem.setSetOrNot(cursor.getString(8));
                    notifList.add(notifItem);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

    }

    @Override
    public void onItemClick(View view, int position) {
        Calendar tempCal = notifList.get(position).getReminderTime();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", tempCal.getTimeInMillis());
        intent.putExtra("allDay", false);
        intent.putExtra("rrule", "FREQ=DAILY");
        intent.putExtra("endTime", tempCal.getTimeInMillis()+60*60*1000);
        intent.putExtra("title", notifList.get(position).getTitle());
        startActivity(intent);
    }

}
