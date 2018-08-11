package in.ashutoshchaubey.forgotnot.helpers;

import android.support.v7.widget.RecyclerView;

/**
 * Created by ashutoshchaubey on 12/08/18.
 */

interface RecyclerItemTouchHelperListener {

    void onSwiped(RecyclerView.ViewHolder holder, int direction, int position);

}
