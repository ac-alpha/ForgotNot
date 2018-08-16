package in.ashutoshchaubey.forgotnot.databaseandadapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.ashutoshchaubey.forgotnot.R;
import in.ashutoshchaubey.forgotnot.constants.Constants;

/**
 * Created by ashutoshchaubey on 07/08/18.
 */

public class NotifAdapter extends RecyclerView.Adapter<NotifAdapter.NotifViewHolder> {

    /**
     * ItemClickListener to listen onClick events
     */
    private ItemClickListener mClickListener;

    /**
     * LayoutInflater to inflate ViewHolders to the RecyclerView
     */
    private LayoutInflater mInflater;

    /**
     * ArrayList to store the data in form of NotifItems to populate to the RecyclerView
     */
    private ArrayList<NotifItem> mData;

    public NotifAdapter(Context context, ArrayList<NotifItem> data) {
        //Initializing the variables
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public NotifAdapter.NotifViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //Inflating one ViewHolder to the RecyclerView
        View view = mInflater.inflate(R.layout.notif_item, parent, false);

        return new NotifViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotifAdapter.NotifViewHolder holder, int position) {

        //setting notifTitle's text as the title FIELD of the NotifItem
        holder.notifTitle.setText(mData.get(position).getTitle());

        //setting dateTime's text to the date in specific format
        holder.dateTIme.setText(String.format("%1$tA %1$tb %1$td %1$tY at %1$tI:%1$tM %1$Tp", mData.get(position).getReminderTime()));

        //setting the text and background of setOrNot as per the setOrNot field of NotifItem
        if (mData.get(position).getSetOrNot().equals(Constants.REMINDER_SET)) {
            holder.setOrNot.setBackgroundResource(R.drawable.green_button_bg);
            holder.setOrNot.setText(Constants.REMINDER_SET);
        } else if (mData.get(position).getSetOrNot().equals(Constants.REMINDER_UNSET)) {
            holder.setOrNot.setBackgroundResource(R.drawable.yellow_button_bg);
            holder.setOrNot.setText(Constants.REMINDER_UNSET);
        } else {
            holder.setOrNot.setBackgroundResource(R.drawable.red_button_bg);
            holder.setOrNot.setText(Constants.REMINDER_UNNOTICED);
        }

        //If reminder is not set then making set button visible
        if (mData.get(position).getSetOrNot().equals(Constants.REMINDER_SET)) {
            holder.setReminder.setVisibility(View.INVISIBLE);
        } else {
            holder.setReminder.setVisibility(View.VISIBLE);
        }
        holder.setOrNot.setElevation(12.0f);
        holder.setReminder.setElevation(12.0f);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class NotifViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //Title of the notification
        TextView notifTitle;

        //TextView to display Date and Time of reminder
        TextView dateTIme;

        //Clickable TextView to set reminder if it is not already set
        TextView setReminder;

        //TextView to indicate whether reminder is set or not
        TextView setOrNot;

        //Background of the view which appears when swiped
        RelativeLayout viewBackground;

        //Foreground View, i/e/ basically the notif_item's front view
        public LinearLayout viewForeground;

        NotifViewHolder(View itemView) {
            super(itemView);

            //Fetching the Views from xml
            notifTitle = (TextView) itemView.findViewById(R.id.notif_title);
            dateTIme = (TextView) itemView.findViewById(R.id.date_time);
            setReminder = (TextView) itemView.findViewById(R.id.set_reminder);
            setOrNot = (TextView) itemView.findViewById(R.id.set_or_not);
            viewBackground = (RelativeLayout)itemView.findViewById(R.id.background_view);
            viewForeground = (LinearLayout) itemView.findViewById(R.id.foreground_view);

            //setting onClickListener to be current class
            setReminder.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public ArrayList<NotifItem> getmData() {
        return mData;
    }

    public void setmData(ArrayList<NotifItem> mData) {
        this.mData = mData;
    }

    /**
     * Function to remove a NotifItem from the RecyclerView
     * @param pos - Position of the NotifItem to be deleted
     */
    public void removeItem(int pos){
        mData.remove(pos);
        notifyItemRemoved(pos);
    }

    /**
     * Function to restore a previously deleted item
     * @param pos - Position at which the NotifItem is to be added
     * @param item - NotifItem to be added
     */
    public void restoreItem(int pos, NotifItem item){
        mData.add(pos, item);
        notifyItemInserted(pos);
    }

}
