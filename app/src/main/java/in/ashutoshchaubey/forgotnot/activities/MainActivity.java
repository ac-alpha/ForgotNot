package in.ashutoshchaubey.forgotnot.activities;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
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
import java.util.Collections;
import in.ashutoshchaubey.forgotnot.R;
import in.ashutoshchaubey.forgotnot.constants.Constants;
import in.ashutoshchaubey.forgotnot.helpers.DbHelper;
import in.ashutoshchaubey.forgotnot.databaseandadapters.NotifAdapter;
import in.ashutoshchaubey.forgotnot.databaseandadapters.NotifItem;
import in.ashutoshchaubey.forgotnot.helpers.RecyclerItemTouchHelper;
import in.ashutoshchaubey.forgotnot.helpers.RecyclerItemTouchHelperListener;
import in.ashutoshchaubey.forgotnot.services.FnNotificationListenerService;


public class MainActivity extends AppCompatActivity implements NotifAdapter.ItemClickListener, RecyclerItemTouchHelperListener {

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    /*
     * SQLite Database in which data about all incoming notifications are stored.
     */
    private SQLiteDatabase db;

    /**
     * ArrayList storing all the data selected from the database in form of NotifItem
     * to populate in the RecyclerView.Adapter
     */
    private ArrayList<NotifItem> notifList;

    /**
     * Adapter for the RecyclerView
     */
    private NotifAdapter notifAdapter;

    /**
     * Root Layout of the main activity (from the bottom of which the SnackBar should reveal)
     */
    private LinearLayout rootLayout;

    /**
     * TAG for Logging
     */
    String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Fetching the TypeFace from assets
        Typeface kaushan = Typeface.createFromAsset(getApplication().getAssets(), "fonts/kaushan.otf");

        //Setting the Typeface of the toolbar text as kaushan
        TextView toolbarText = (TextView) findViewById(R.id.toolbar);
        toolbarText.setTypeface(kaushan);

        //Fetching the root layout from the xml
        rootLayout = (LinearLayout) findViewById(R.id.root_layout);

        //Checking if the notification service is enabled.
        //If enabled, then starting the service immediately (ignored if already started)
        //If not, then asking the user to enable the service
        if (!isNotificationServiceEnabled()) {
            AlertDialog enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
            enableNotificationListenerAlertDialog.show();
        } else {
            startService(new Intent(MainActivity.this, FnNotificationListenerService.class));
        }

        //Fetching the RecyclerView from xml
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.notif_recycler_view);

        //Getting the database associated with the app with the help of DbHelper class instance
        DbHelper notifDbHelper = new DbHelper(this);
        db = notifDbHelper.getWritableDatabase();

        //populating the notifList from database
        fetchData();

        //setting the LayoutManager to LinearLayout manager for populating the
        //items in form of a linear list
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //setting the animation of addition/deletion of items in the RecyclerView
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //setting the divider between two childs of the RecyclerView
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        //Adapter for the RecyclerView
        notifAdapter = new NotifAdapter(this, notifList);

        //setting ClickListener for the Adapter
        notifAdapter.setClickListener(this);

        //setting Adapter to the RecyclerView
        recyclerView.setAdapter(notifAdapter);

        //setting callback to give response swipe to delete functionality
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        //attaching the ItemTouchHelper to the RecyclerView
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        //setting ClickListener to the refresh button on Toolbar
        findViewById(R.id.refresh_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //repopulating the notigList to add any changes
                fetchData();

                //removing the adapter the re-setting it with new data
                recyclerView.setAdapter(null);
                notifAdapter = new NotifAdapter(MainActivity.this, notifList);
                notifAdapter.setClickListener(MainActivity.this);
                recyclerView.setAdapter(notifAdapter);
                notifAdapter.notifyDataSetChanged();
            }
        });

    }

    /**
     * Created by Fábio Alves Martins Pereira (Chagall)
     * Is Notification Service Enabled.
     * Verifies if the notification listener service is enabled.
     * Got it from: https://github.com/kpbird/NotificationListenerService-Example/blob/master/NLSExample/src/main/java/com/kpbird/nlsexample/NLService.java
     *
     * @return True if enabled, false otherwise.
     */
    private boolean isNotificationServiceEnabled() {
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
     *
     * @return An alert dialog which leads to the notification enabling screen
     */
    private AlertDialog buildNotificationServiceAlertDialog() {
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
        return (alertDialogBuilder.create());
    }

    /**
     * Fetches the data of all the notifications inserted in the
     * database and populates them into the notifList
     */
    private void fetchData() {

        notifList = new ArrayList<>();
//
//        notifList.add(new NotifItem("ECN102 Lecture", Calendar.getInstance(), Constants.REMINDER_SET));
//        notifList.add(new NotifItem("Subject Addition/Deletion Form Submission", Calendar.getInstance(), Constants.REMINDER_UNSET));
//        notifList.add(new NotifItem("NSS Meeting", Calendar.getInstance(), Constants.REMINDER_SET));
//        notifList.add(new NotifItem("ML/AI Workshop", Calendar.getInstance(), Constants.REMINDER_UNNOTICED));

        //Creating cursor to make query to database
        Cursor cursor = db.query(Constants.NotifEntry.TABLE_NAME, null, null,
                null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    //If the showOrNot Column is SHOW then only add it to the notifList
                    if (cursor.getInt(8) == Constants.NOTIF_SHOW) {
                        NotifItem notifItem = new NotifItem();
                        Log.e("Row ID", cursor.getInt(0) + "");
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

        //reversing the notifList so that the latest data remains at the top
        Collections.reverse(notifList);

    }

    @Override
    public void onItemClick(View view, final int position) {

        //setting the tempCal to the calendar time of the currently clicked item
        final Calendar tempCal = notifList.get(position).getReminderTime();

        //Building a new AlertDialog to ask user whether he really wants to set a reminder
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Set Reminder?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //If user presses Yes button

                        //Getting the NotifItem associated with the clicked child
                        NotifItem item = notifList.get(position);

                        //Making a ContentValues object to help update the table row
                        ContentValues cv = new ContentValues();

                        //Updating the COLUMN_SET_UNSET field to REMINDER_SET
                        cv.put(Constants.NotifEntry.COLUMN_SET_UNSET, Constants.REMINDER_SET);
                        db.update(Constants.NotifEntry.TABLE_NAME, cv, Constants.NotifEntry.COLUMN_EVENT_NAME + " = \"" + item.getTitle() + "\"", null);

                        //clearing the ContentValues object after use
                        cv.clear();

                        //Sending intent to the Calendar to add event
                        Intent intent = new Intent(Intent.ACTION_EDIT);
                        intent.setType("vnd.android.cursor.item/event");
                        intent.putExtra("beginTime", tempCal.getTimeInMillis());
                        intent.putExtra("allDay", false);
                        intent.putExtra("rrule", "FREQ=DAILY");
                        intent.putExtra("endTime", tempCal.getTimeInMillis() + 60 * 60 * 1000);
                        intent.putExtra("title", notifList.get(position).getTitle());
                        startActivity(intent);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //If user presses No button

                        //Getting the NotifItem associated with the clicked child
                        NotifItem item = notifList.get(position);

                        //Making a ContentValues object to help update the table row
                        ContentValues cv = new ContentValues();

                        //Updating the COLUMN_SET_UNSET field to REMINDER_UNSET
                        cv.put(Constants.NotifEntry.COLUMN_SET_UNSET, Constants.REMINDER_UNSET);
                        db.update(Constants.NotifEntry.TABLE_NAME, cv, Constants.NotifEntry.COLUMN_EVENT_NAME + " = \"" + item.getTitle() + "\"", null);

                        //clearing the ContentValues object after use
                        cv.clear();

                        //Recreating the MainActivity to apply changes
                        recreate();

                    }
                }).create();
        dialog.show();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder holder, int direction, int position) {

        //If a child is swiped

        //If the child is instance of the ViewHolder of the RecyclerView
        if (holder instanceof NotifAdapter.NotifViewHolder) {

            //Index of the swiped child
            final int deleteIndex = holder.getAdapterPosition();

            //NotifItem associated with the swiped child
            final NotifItem deletedItem = notifList.get(deleteIndex);

            //Title of the swiped NotifItem
            final String name = deletedItem.getTitle();

            //Removing the swiped index from adapter
            notifAdapter.removeItem(deleteIndex);

            //Making ContentValues object to help update database row
            final ContentValues cv = new ContentValues();

            //setting the value of COLUMN_SHOW field to be NOTIF_HIDE
            cv.put(Constants.NotifEntry.COLUMN_SHOW, Constants.NOTIF_HIDE);

            //Updating the database
            db.update(Constants.NotifEntry.TABLE_NAME, cv, Constants.NotifEntry.COLUMN_EVENT_NAME + " = \"" + name + "\"", null);

            //Clearing the ContentValues Object
            cv.clear();

            //Creating a SnackBar to notify user that item has been removed
            //and to give them option to UNDO their changes
            Snackbar snackbar = Snackbar.make(rootLayout, "Item removed!!", Snackbar.LENGTH_SHORT);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //If user wishes to undo his/her action

                    //Restoring the index
                    notifAdapter.restoreItem(deleteIndex, deletedItem);
                    cv.put(Constants.NotifEntry.COLUMN_SHOW, Constants.NOTIF_SHOW);
                    db.update(Constants.NotifEntry.TABLE_NAME, cv, Constants.NotifEntry.COLUMN_EVENT_NAME + " = \"" + name + "\"", null);
                    cv.clear();
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        }

    }

    @Override
    public void onBackPressed() {

        //Open the Launcher Screen is back button is pressed
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);

    }
}
