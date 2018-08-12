package in.ashutoshchaubey.forgotnot.activities;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import in.ashutoshchaubey.forgotnot.R;
import in.ashutoshchaubey.forgotnot.constants.Constants;
import in.ashutoshchaubey.forgotnot.helpers.DbHelper;
import in.ashutoshchaubey.forgotnot.databaseandadapters.NotifAdapter;
import in.ashutoshchaubey.forgotnot.databaseandadapters.NotifItem;
import in.ashutoshchaubey.forgotnot.helpers.RecyclerItemTouchHelper;
import in.ashutoshchaubey.forgotnot.helpers.RecyclerItemTouchHelperListener;
import in.ashutoshchaubey.forgotnot.services.FnNotificationListenerService;

import static in.ashutoshchaubey.forgotnot.services.FnNotificationListenerService.MY_PREFERENCES;

public class MainActivity extends AppCompatActivity implements NotifAdapter.ItemClickListener, RecyclerItemTouchHelperListener {

    private AlertDialog enableNotificationListenerAlertDialog;
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";
    private SQLiteDatabase db;
    private DbHelper notifDbHelper;
    private ArrayList<NotifItem> notifList;
    private NotifAdapter notifAdapter;
    private LinearLayout rootLayout;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootLayout = (LinearLayout) findViewById(R.id.root_layout);

        if(!isNotificationServiceEnabled()){
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
            enableNotificationListenerAlertDialog.show();
        }else{
            startService(new Intent(MainActivity.this, FnNotificationListenerService.class));
        }

        TextView textView = (TextView) findViewById(R.id.text);
        preferences = getApplicationContext()
                .getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);
        editor = preferences.edit();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.notif_recycler_view);
        notifDbHelper = new DbHelper(this);
        db = notifDbHelper.getWritableDatabase();
        notifList = new ArrayList<>();
//        notifList.add(new NotifItem("ECN 102 Lecture", Calendar.getInstance(), Constants.REMINDER_SET));
//        notifList.add(new NotifItem("Document Verification @ MAC", Calendar.getInstance(), Constants.REMINDER_UNSET));
//        notifList.add(new NotifItem("Fee Deposit", Calendar.getInstance(), Constants.REMINDER_UNNOTICED));
        fetchData();
        Log.e(FnNotificationListenerService.TAG, notifList.size()+"");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notifAdapter = new NotifAdapter(this,notifList);
        notifAdapter.setClickListener(this);
        recyclerView.setAdapter(notifAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


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
                    Log.e(FnNotificationListenerService.TAG, cursor.getInt(9)+"");
                    if (cursor.getInt(8) == Constants.NOTIF_SHOW) {
                        NotifItem notifItem = new NotifItem();
                        Log.e("Row ID",cursor.getInt(0)+"");
                        notifItem.setTitle(cursor.getString(1));
                        Calendar time = Calendar.getInstance();
                        time.set(Calendar.DATE, cursor.getInt(2));
                        time.set(Calendar.MONTH, cursor.getInt(3));
                        time.set(Calendar.YEAR, cursor.getInt(4));
                        time.set(Calendar.HOUR, cursor.getInt(5));
                        time.set(Calendar.MINUTE, cursor.getInt(6));
                        time.set(Calendar.AM_PM, cursor.getInt(7));
                        notifItem.setReminderTime(time);
                        notifItem.setSetOrNot(cursor.getString(9));
                        notifList.add(notifItem);
                    }

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

    }

    @Override
    public void onItemClick(View view, final int position) {
        final Calendar tempCal = notifList.get(position).getReminderTime();
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Set Reminder?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        NotifItem item = notifList.get(position);
                        ContentValues cv = new ContentValues();
                        cv.put(Constants.NotifEntry.COLUMN_SET_UNSET, Constants.REMINDER_SET);
                        db.update(Constants.NotifEntry.TABLE_NAME, cv, Constants.NotifEntry.COLUMN_EVENT_NAME+" = \""+item.getTitle()+"\"",null);
                        cv.clear();
                        Intent intent = new Intent(Intent.ACTION_EDIT);
                        intent.setType("vnd.android.cursor.item/event");
                        intent.putExtra("beginTime", tempCal.getTimeInMillis());
                        intent.putExtra("allDay", false);
                        intent.putExtra("rrule", "FREQ=DAILY");
                        intent.putExtra("endTime", tempCal.getTimeInMillis()+60*60*1000);
                        intent.putExtra("title", notifList.get(position).getTitle());
                        startActivity(intent);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        NotifItem item = notifList.get(position);
                        ContentValues cv = new ContentValues();
                        cv.put(Constants.NotifEntry.COLUMN_SET_UNSET, Constants.REMINDER_UNSET);
                        db.update(Constants.NotifEntry.TABLE_NAME, cv, Constants.NotifEntry.COLUMN_EVENT_NAME+" = \""+item.getTitle()+"\"",null);
                        cv.clear();

                    }
                }).create();
        dialog.show();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder holder, int direction, int position) {

        if (holder instanceof NotifAdapter.NotifViewHolder){

//            int curr = preferences.getInt(Constants.KEY_TOTAL_NO_OF_DELETED_NOTIFITEMS,0);
//            Log.e("Total del items", curr+"");
//            editor.putInt(Constants.KEY_TOTAL_NO_OF_DELETED_NOTIFITEMS, curr+1);
//            editor.apply();

            final int deleteIndex = holder.getAdapterPosition();
            final NotifItem deletedItem = notifList.get(deleteIndex);
            final String name = deletedItem.getTitle();
            notifAdapter.removeItem(deleteIndex);

            final ContentValues cv = new ContentValues();
            cv.put(Constants.NotifEntry.COLUMN_SHOW, Constants.NOTIF_HIDE);
            db.update(Constants.NotifEntry.TABLE_NAME, cv, Constants.NotifEntry.COLUMN_EVENT_NAME+" = \""+name+"\"",null);
            cv.clear();

            Snackbar snackbar = Snackbar.make(rootLayout, "Item removed!!", Snackbar.LENGTH_SHORT);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    int curr = preferences.getInt(Constants.KEY_TOTAL_NO_OF_DELETED_NOTIFITEMS,0);
//                    editor.putInt(Constants.KEY_TOTAL_NO_OF_DELETED_NOTIFITEMS, curr-1);
//                    editor.apply();
                    notifAdapter.restoreItem(deleteIndex,deletedItem);
                    cv.put(Constants.NotifEntry.COLUMN_SHOW, Constants.NOTIF_SHOW);
                    db.update(Constants.NotifEntry.TABLE_NAME, cv, Constants.NotifEntry.COLUMN_EVENT_NAME+" = \""+name+"\"",null);
                    cv.clear();
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        }

    }
}
