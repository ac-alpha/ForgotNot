package in.ashutoshchaubey.forgotnot.databaseandadapters;

import java.util.Calendar;

/**
 * Created by ashutoshchaubey on 07/08/18.
 */

public class NotifItem {

    /**
     * Contains the title of the reminder which is basically
     * the subject of the mail from which it is extracted
     */
    private String title;

    /**
     * Time extracted from the email, at which the event will occur
     */
    private Calendar reminderTime;

    /**
     * Field depicting whether the reminder is set or not
     */
    private String setOrNot;

    /**
     * Field deciding whether the NotifItem should be displayed
     * or not (as the user might have swiped it out)
     */
    private boolean showOrNot = true;

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

    public boolean getShowOrNot() {
        return showOrNot;
    }

    public void setShowOrNot(boolean showOrNot) {
        this.showOrNot = showOrNot;
    }
}
