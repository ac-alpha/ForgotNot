package in.ashutoshchaubey.forgotnot.databaseandadapters;

import java.util.Calendar;

/**
 * Created by ashutoshchaubey on 07/08/18.
 */

public class NotifItem {

    private String title;
    private Calendar reminderTime;
    private String setOrNot;

    public NotifItem(){}

    public NotifItem(String title, Calendar reminderTime, String setOrNot) {
        this.title = title;
        this.reminderTime = reminderTime;
        this.setOrNot = setOrNot;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Calendar getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(Calendar reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String getSetOrNot() {
        return setOrNot;
    }

    public void setSetOrNot(String setOrNot) {
        this.setOrNot = setOrNot;
    }
}
