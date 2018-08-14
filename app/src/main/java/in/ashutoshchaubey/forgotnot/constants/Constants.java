package in.ashutoshchaubey.forgotnot.constants;

import android.provider.BaseColumns;

/**
 * Created by ashutoshchaubey on 31/07/18.
 */

public class Constants {

    /**
     * Defines the setOrNot property of the NotifItem
     * REMINDER_SET - User has set the reminder and Event is added
     * REMINDER_UNSET - User has decided not to set reminder for the Event
     * REMINDER_UNNOTICED - User has not seen the Event till and not decided
     *                      whether to add reminder or not
     */
    public static String REMINDER_SET = "Reminder Set";
    public static String REMINDER_UNSET = "Reminder Not Set";
    public static String REMINDER_UNNOTICED = "Unnoticed";

    /**
     * Defines the showOrNot property of the NotifItem
     * NOTIF_SHOW - Add the NotifItem to the RecyclerView
     * NOTIF_HIDE - Don't dispay the NotifItem in the RecyclerView
     */
    public static int NOTIF_SHOW = 1;
    public static int NOTIF_HIDE = 0;


    public class NotifEntry implements BaseColumns{

        public static final String TABLE_NAME = "notifs"; //Table Name for the notificaion list table
        public static final String _ID = BaseColumns._ID; //Default _ID
        public static final String COLUMN_EVENT_NAME = "name";  //Title of the event
        public static final String COLUMN_DATE_DD = "date_dd";  //DD of the Event in DDMMYYYY format
        public static final String COLUMN_DATE_MM = "date_mm";  //MM of the Event in DDMMYYYY format
        public static final String COLUMN_DATE_YYYY = "date_yyyy";  //YYYY of the Event in DDMMYYYY format
        public static final String COLUMN_TIME_HR = "time_hr";  //Hour of the event in 12 Hr format
        public static final String COLUMN_TIME_MIN = "time_min";  //Minute of the event
        public static final String COLUMN_TIME_AM_PM = "time_am_pm";
        public static final String COLUMN_SET_UNSET = "set_unset";  //The event's reminder is set, unset or unnoticed
        public static final String COLUMN_SHOW = "show_notif_in_list";  //The event should be shown in the list or not

    }

}
