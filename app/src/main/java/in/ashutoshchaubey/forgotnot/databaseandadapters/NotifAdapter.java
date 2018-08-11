package in.ashutoshchaubey.forgotnot.databaseandadapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import in.ashutoshchaubey.forgotnot.R;
import in.ashutoshchaubey.forgotnot.constants.Constants;

/**
 * Created by ashutoshchaubey on 07/08/18.
 */

public class NotifAdapter extends RecyclerView.Adapter<NotifAdapter.NotifViewHolder> {

    private ItemClickListener mClickListener;
    private LayoutInflater mInflater;
    private ArrayList<NotifItem> mData;

    public NotifAdapter(Context context, ArrayList<NotifItem> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public NotifAdapter.NotifViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.notif_item, parent, false);
        return new NotifViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotifAdapter.NotifViewHolder holder, int position) {
        holder.notifTitle.setText(mData.get(position).getTitle());
//        SimpleDateFormat sdf = new SimpleDateFormat("MMM MM dd, yyyy h:mm a");
//        String dateString = sdf.format(mData.get(position).getReminderTime());
        holder.dateTIme.setText(String.format("%1$tA %1$tb %1$td %1$tY at %1$tI:%1$tM %1$Tp", mData.get(position).getReminderTime()));
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
        if (mData.get(position).getSetOrNot().equals(Constants.REMINDER_UNNOTICED)) {
            holder.setReminder.setVisibility(View.VISIBLE);
        } else {
            holder.setReminder.setVisibility(View.INVISIBLE);
        }
        holder.setOrNot.setElevation(12.0f);
        holder.setReminder.setElevation(12.0f);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class NotifViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView notifTitle;
        TextView dateTIme;
        TextView setReminder;
        TextView setOrNot;

        public NotifViewHolder(View itemView) {
            super(itemView);
            notifTitle = (TextView) itemView.findViewById(R.id.notif_title);
            dateTIme = (TextView) itemView.findViewById(R.id.date_time);
            setReminder = (TextView) itemView.findViewById(R.id.set_reminder);
            setOrNot = (TextView) itemView.findViewById(R.id.set_or_not);
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

    public void removeItem(int pos){
        mData.remove(pos);
        notifyItemRemoved(pos);
    }

    public void restoreItem(int pos, NotifItem item){
        mData.add(pos, item);
        notifyItemInserted(pos);
    }

}
