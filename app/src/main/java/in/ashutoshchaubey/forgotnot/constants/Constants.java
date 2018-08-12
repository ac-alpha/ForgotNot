package in.ashutoshchaubey.forgotnot.constants;

import android.provider.BaseColumns;

/**
 * Created by ashutoshchaubey on 31/07/18.
 */

public class Constants {

    public static String REMINDER_SET = "Reminder Set";
    public static String REMINDER_UNSET = "Reminder Not Set";
    public static String REMINDER_UNNOTICED = "Unnoticed";
    public static int NOTIF_SHOW = 1;
    public static int NOTIF_HIDE = 0;
    public static String KEY_TOTAL_NO_OF_DELETED_NOTIFITEMS = "total_no_of_deleted_notif_items";

    public class NotifEntry implements BaseColumns{

        public static final String TABLE_NAME = "notifs";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_EVENT_NAME = "name";
        public static final String COLUMN_DATE_DD = "date_dd";
        public static final String COLUMN_DATE_MM = "date_mm";
        public static final String COLUMN_DATE_YYYY = "date_yyyy";
        public static final String COLUMN_TIME_HR = "time_hr";
        public static final String COLUMN_TIME_MIN = "time_min";
        public static final String COLUMN_TIME_AM_PM = "time_am_pm";
        public static final String COLUMN_SET_UNSET = "set_unset";
        public static final String COLUMN_SHOW = "show_notif_in_list";

    }

}
