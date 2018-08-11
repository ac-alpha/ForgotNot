package in.ashutoshchaubey.forgotnot.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import in.ashutoshchaubey.forgotnot.constants.Constants.NotifEntry;

/**
 * Created by ashutoshchaubey on 31/07/18.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "NotifList5";
    private static final int DATABASE_VERSION = 1;

    public static final String CREATE_TABLE_NOTIFS = "CREATE TABLE " + NotifEntry.TABLE_NAME + "("+
            NotifEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NotifEntry.COLUMN_EVENT_NAME + " TEXT, " +
            NotifEntry.COLUMN_DATE_DD + " INTEGER, "+
            NotifEntry.COLUMN_DATE_MM + " INTEGER, "+
            NotifEntry.COLUMN_DATE_YYYY + " INTEGER, "+
            NotifEntry.COLUMN_TIME_HR + " INTEGER, "+
            NotifEntry.COLUMN_TIME_MIN + " INTEGER, "+
            NotifEntry.COLUMN_TIME_AM_PM + " INTEGER, "+
            NotifEntry.COLUMN_SET_UNSET+" TEXT );";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NOTIFS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NotifEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
