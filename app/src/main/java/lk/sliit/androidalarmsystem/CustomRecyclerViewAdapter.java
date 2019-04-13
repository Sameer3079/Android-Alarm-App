package lk.sliit.androidalarmsystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class CustomRecyclerViewAdapter
        extends RecyclerView.Adapter<CustomRecyclerViewAdapter.ViewHolder> {

    private final static String TAG = "APP-CustomRecyclerViewAdapter";

    private List<Alarm> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    // Data is passed into the constructor
    CustomRecyclerViewAdapter(Context context, List<Alarm> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // Inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // Binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Alarm alarm = mData.get(holder.getAdapterPosition());
        holder.nameTextView.setText(alarm.getName());
        holder.timeTextView.setText(alarm.getTime());
        holder.isEnabled.setChecked(alarm.isEnabled());
        holder.isEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarm.setEnabled(isChecked);
                AlarmDatabaseHelper db = new AlarmDatabaseHelper(context);
                db.update(alarm);

                // Updating the alarm which as already been set
                Intent intent = new Intent(context, AlarmService.class);
                intent.putExtra("command", AlarmCommand.UPDATE_ALARM);
                intent.putExtra("alarmId", alarm.getId());
                context.startService(intent);
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameTextView;
        TextView timeTextView;
        Switch isEnabled;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.alarmName);
            timeTextView = itemView.findViewById(R.id.time);
            isEnabled = itemView.findViewById(R.id.alarmEnableSwitch);
        }

        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick");
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Alarm getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}